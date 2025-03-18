package com.myBusiness.repository;

import com.myBusiness.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for RefreshToken entities.
 * Provides methods to find a refresh token by its token string and to delete tokens by user ID.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Finds a refresh token by its unique token string.
     *
     * @param token the refresh token string.
     * @return an Optional containing the RefreshToken if found, or empty if not found.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Deletes all refresh tokens associated with the specified user ID.
     *
     * @param userId the ID of the user whose tokens should be deleted.
     */
    void deleteByUserId(Long userId);
}
