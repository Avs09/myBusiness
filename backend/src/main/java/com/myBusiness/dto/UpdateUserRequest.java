package com.myBusiness.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class UpdateUserRequest {

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El email debe tener un formato válido.")
    @Size(max = 255, message = "El email no puede exceder los 255 caracteres.")
    private String email;

    @NotNull(message = "Los roles no pueden ser nulos.")
    @Size(min = 1, message = "Debe asignarse al menos un rol.")
    private Set<@NotNull(message = "El ID del rol no puede ser nulo.") Long> roles;

    // Getters y setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Long> getRoles() {
        return roles;
    }

    public void setRoles(Set<Long> roles) {
        this.roles = roles;
    }
}
