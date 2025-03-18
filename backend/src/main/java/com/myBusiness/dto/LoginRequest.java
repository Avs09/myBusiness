package com.myBusiness.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * LoginRequest DTO encapsulates user login data.
 * It validates the email format and enforces password complexity.
 */
public class LoginRequest {

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El email debe tener un formato válido.")
    @Size(max = 255, message = "El email no puede exceder los 255 caracteres.")
    private final String email;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres.")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,64}$",
        message = "La contraseña debe incluir al menos una letra minúscula, una mayúscula, un número y un carácter especial."
    )
    private final String password;

    /**
     * Constructor for LoginRequest.
     *
     * @param email the user's email.
     * @param password the user's password.
     */
    @JsonCreator
    public LoginRequest(@JsonProperty("email") String email,
                        @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * @return the user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the user's password.
     */
    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                // No se incluye la contraseña para evitar exponer datos sensibles
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginRequest)) return false;
        LoginRequest that = (LoginRequest) o;
        return Objects.equals(email, that.email) &&
               Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}
