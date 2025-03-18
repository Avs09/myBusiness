package com.myBusiness.service.impl;

import com.myBusiness.model.User;
import com.myBusiness.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * UserDetailsServiceImpl implements the Spring Security UserDetailsService interface.
 * It loads user-specific data (including roles and credentials) from the repository for authentication.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection.
     *
     * @param userRepository Repository to load user details.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user by email, constructs and returns a Spring Security UserDetails object.
     *
     * @param email The email of the user.
     * @return UserDetails instance containing the user's credentials and authorities.
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        logger.info("User found with email: {}", email);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> "ROLE_" + role.getName())
                        .toArray(String[]::new))
                .build();
    }
}
