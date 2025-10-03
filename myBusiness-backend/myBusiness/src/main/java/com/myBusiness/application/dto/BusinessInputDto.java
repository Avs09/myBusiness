package com.myBusiness.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessInputDto {

    @NotBlank(message = "El nombre del negocio es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "El NIT es obligatorio")
    @Size(min = 5, max = 20, message = "El NIT debe tener entre 5 y 20 caracteres")
    private String nit;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String address;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String phone;

    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Size(max = 200, message = "El sitio web no puede exceder 200 caracteres")
    private String website;

    @Size(max = 500, message = "La URL del logo no puede exceder 500 caracteres")
    private String logoUrl;

    @Size(max = 50, message = "La industria no puede exceder 50 caracteres")
    private String industry;
}