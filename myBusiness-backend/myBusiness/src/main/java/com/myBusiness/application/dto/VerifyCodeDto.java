package com.myBusiness.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VerifyCodeDto {
    @NotBlank(message = "Email obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Código obligatorio")
    private String code;
}
