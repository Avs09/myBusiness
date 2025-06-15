package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.EmailDto;
import com.myBusiness.application.dto.UserRegisterDto;
import com.myBusiness.application.dto.VerifyCodeDto;
import com.myBusiness.application.exception.InvalidCodeException;
import com.myBusiness.application.exception.InvalidUserException;
import com.myBusiness.application.exception.TokenExpiredException;
import com.myBusiness.application.usecase.RegisterUserUseCase;
import com.myBusiness.application.usecase.SendVerificationCodeUseCase;
import com.myBusiness.application.usecase.VerifyEmailCodeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
