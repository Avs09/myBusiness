package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.EmailDto;
import com.myBusiness.application.dto.UserProfileDto;
import com.myBusiness.application.dto.UserRegisterDto;
import com.myBusiness.application.dto.VerifyCodeDto;
import com.myBusiness.application.exception.InvalidCodeException;
import com.myBusiness.application.exception.InvalidUserException;
import com.myBusiness.application.exception.TokenExpiredException;
import com.myBusiness.application.usecase.RegisterUserUseCase;
import com.myBusiness.application.usecase.SendVerificationCodeUseCase;
import com.myBusiness.application.usecase.UpdateUserProfileUseCase;
import com.myBusiness.application.usecase.VerifyEmailCodeUseCase;
import com.myBusiness.domain.model.User;
import com.myBusiness.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final SendVerificationCodeUseCase sendCodeUseCase;
    private final VerifyEmailCodeUseCase verifyCodeUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getPrincipal();
    }

    /**
     * 1) Registrar usuario -> POST /api/users/register
     *    - Solo crea token con datos pendientes; NO guarda en tabla users.
     *    - Luego dispara el envío de correo (usando el token creado).
     */
    /** POST /api/users/register */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid UserRegisterDto dto) {
        String normalizedEmail = dto.getEmail().trim().toLowerCase();
        try {
            // 1) Si el email ya existe en usuarios → lanza InvalidUserException
            //    Si hay un token pendiente → también lanza InvalidUserException
            registerUserUseCase.checkIfEmailPending(normalizedEmail);

            // 2) Creamos el token “pendiente”
            String code = registerUserUseCase.createPendingUser(dto);

            // 3) Intentamos enviar el correo. Si falla SMTP, no abortamos el registro de token.
            try {
                sendCodeUseCase.execute(new EmailDto(normalizedEmail));
            } catch (Exception sendEx) {
                System.err.println("Falló envío de código de verificación: " + sendEx.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                "message", "Usuario pendiente creado. Revisa tu correo para el código."
            ));

        } catch (InvalidUserException iue) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", iue.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error registrando usuario: " + ex.getMessage()));
        }
    }


    /**
     * 2) Reenviar código → POST /api/users/send-code
     */
    @PostMapping("/send-code")
    public ResponseEntity<Void> sendCode(@RequestBody @Valid EmailDto dto) {
        sendCodeUseCase.execute(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(@RequestBody @Valid VerifyCodeDto dto) {
        try {
            // 1) Intentamos verificar (este método arroja InvalidCodeException si el código no existe,
            //    o TokenExpiredException si el código ya caducó).
            verifyCodeUseCase.execute(dto.getEmail(), dto.getCode());

            // 2) Si llegamos aquí, el código es válido:
            return ResponseEntity
                    .ok(Map.of("message", "Correo verificado correctamente."));
        }
        // Si el código es inválido o caducó, devolvemos 400 Bad Request + mensaje
        catch (InvalidCodeException | TokenExpiredException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", ex.getMessage()));
        }
        // Cualquier otro error interno:
        catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error verificando correo: " + ex.getMessage()));
        }
    }

    /**
     * 3) Obtener perfil de usuario.
     *    GET /api/users/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile() {
        try {
            Long userId = getCurrentUserId();
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            UserProfileDto profileDto = new UserProfileDto();
            profileDto.setName(user.getName());
            profileDto.setPhone(user.getPhone());
            profileDto.setLocation(user.getLocation());
            profileDto.setBio(user.getBio());

            return ResponseEntity.ok(profileDto);
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * 4) Actualizar perfil de usuario.
     *    PUT /api/users/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody @Valid UserProfileDto profileDto) {
        try {
            Long userId = getCurrentUserId();
            updateUserProfileUseCase.execute(userId, profileDto);
            return ResponseEntity.ok(Map.of("message", "Perfil actualizado correctamente"));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error actualizando perfil: " + ex.getMessage()));
        }
    }
}
