package com.myBusiness.infrastructure.security;

import com.myBusiness.domain.model.User;
import com.myBusiness.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepo.findByEmail(email.trim().toLowerCase())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        if (!u.isEnabled()) {
            throw new DisabledException("Email no verificado");
        }

        return new org.springframework.security.core.userdetails.User(
            u.getUsername(),
            u.getPassword(),
            true,            
            true,           
            true,            
            true,           
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
