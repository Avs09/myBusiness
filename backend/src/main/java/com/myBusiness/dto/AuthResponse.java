package com.myBusiness.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * AuthResponse is a Data Transfer Object (DTO) for returning JWT tokens after authentication.
 * It contains both the access token and the refresh token.
 */
public class AuthResponse {

    private final String accessToken;
    private final String refreshToken;

    /**
     * Constructor to initialize both tokens.
     *
     * @param accessToken the JWT access token.
     * @param refreshToken the JWT refresh token.
     */
    @JsonCreator
    public AuthResponse(@JsonProperty("accessToken") String accessToken,
                        @JsonProperty("refreshToken") String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    /**
     * @return the access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @return the refresh token.
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthResponse)) return false;
        AuthResponse that = (AuthResponse) o;
        return Objects.equals(accessToken, that.accessToken) &&
               Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, refreshToken);
    }
}
