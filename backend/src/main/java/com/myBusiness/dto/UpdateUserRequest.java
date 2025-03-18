package com.myBusiness.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;

/**
 * UpdateUserRequest DTO is used to update user details such as email and roles.
 * It ensures that the email is valid and at least one role is provided.
 */
public class UpdateUserRequest {

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El email debe tener un formato válido.")
    @Size(max = 255, message = "El email no puede exceder los 255 caracteres.")
    private final String email;

    @NotNull(message = "Los roles no pueden ser nulos.")
    @Size(min = 1, message = "Debe asignarse al menos un rol.")
    private final Set<@NotNull(message = "El ID del rol no puede ser nulo.") Long> roles;

    /**
     * Constructor for UpdateUserRequest.
     *
     * @param email the user's email.
     * @param roles the set of role IDs to assign to the user.
     */
    @JsonCreator
    public UpdateUserRequest(@JsonProperty("email") String email,
                             @JsonProperty("roles") Set<Long> roles) {
        this.email = email;
        this.roles = roles;
    }

    /**
     * @return the updated email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the set of role IDs to assign to the user.
     */
    public Set<Long> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateUserRequest)) return false;
        UpdateUserRequest that = (UpdateUserRequest) o;
        return Objects.equals(email, that.email) &&
               Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, roles);
    }
}
