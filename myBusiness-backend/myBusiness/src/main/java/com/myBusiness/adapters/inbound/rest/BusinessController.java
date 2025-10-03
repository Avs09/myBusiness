package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.BusinessInputDto;
import com.myBusiness.application.dto.BusinessOutputDto;
import com.myBusiness.application.usecase.CreateBusinessUseCase;
import com.myBusiness.application.usecase.GetBusinessUseCase;
import com.myBusiness.application.usecase.UpdateBusinessUseCase;
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
@RequestMapping("/api/business")
@RequiredArgsConstructor
@Validated
public class BusinessController {

    private final CreateBusinessUseCase createBusinessUseCase;
    private final GetBusinessUseCase getBusinessUseCase;
    private final UpdateBusinessUseCase updateBusinessUseCase;

    /**
     * GET /api/business - Obtener el negocio del usuario autenticado
     */
    @GetMapping
    public ResponseEntity<BusinessOutputDto> getBusiness() {
        try {
            Long userId = getCurrentUserId();
            BusinessOutputDto business = getBusinessUseCase.execute(userId);
            return ResponseEntity.ok(business);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * POST /api/business - Crear un nuevo negocio para el usuario autenticado
     */
    @PostMapping
    public ResponseEntity<?> createBusiness(@RequestBody @Valid BusinessInputDto inputDto) {
        try {
            Long userId = getCurrentUserId();
            BusinessOutputDto business = createBusinessUseCase.execute(userId, inputDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(business);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error creando negocio: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/business - Actualizar el negocio del usuario autenticado
     */
    @PutMapping
    public ResponseEntity<?> updateBusiness(@RequestBody @Valid BusinessInputDto inputDto) {
        try {
            Long userId = getCurrentUserId();
            BusinessOutputDto business = updateBusinessUseCase.execute(userId, inputDto);
            return ResponseEntity.ok(business);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error actualizando negocio: " + e.getMessage()));
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("Usuario no autenticado");
    }
}