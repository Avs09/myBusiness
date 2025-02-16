package com.myBusiness.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.myBusiness.service.impl.UserDetailsServiceImpl;
import com.myBusiness.util.JwtAuthEntryPoint;
import com.myBusiness.util.JwtAuthFilter;
import com.myBusiness.util.JwtUtil;

@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtUtil jwtUtil;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, 
                          JwtAuthEntryPoint jwtAuthEntryPoint,
                          JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF
            .authorizeHttpRequests(request -> request
                .requestMatchers("/api/auth/**").permitAll() // Permitir endpoints públicos
                .anyRequest().authenticated() // Proteger todas las demás rutas
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthEntryPoint) // Manejo de errores de autenticación
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless con JWT
            );

        // Agregar filtro JWT antes del filtro de autenticación
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    protected AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // Configuración directa sin usar `.and()`:
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtil, userDetailsService); // Pasar dependencias requeridas
    }
}
