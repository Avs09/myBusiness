package com.myBusiness.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableWebSecurity
@EnableMethodSecurity // Mejora 5: Habilita seguridad a nivel de método (@PreAuthorize, etc.)
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

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
            // Deshabilita CSRF por tratarse de un servicio sin estado con JWT
            .csrf(csrf -> csrf.disable())
            // Mejora 2: Definir endpoints públicos para autenticación, documentación, consola H2 y recursos estáticos.
            .authorizeHttpRequests(request -> request
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**", "/static/**").permitAll()
                .anyRequest().authenticated()
            )
            // Mejora 3: Registro detallado de intentos de acceso no autorizado.
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    logger.error("Intento de acceso no autorizado a: {}", request.getRequestURI(), authException);
                    jwtAuthEntryPoint.commence(request, response, authException);
                })
            )
            // Configuración de sesión sin estado (stateless)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        // Se inserta el filtro JWT antes del filtro de autenticación estándar.
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        // Para permitir el acceso a la consola H2.
        http.headers(headers -> headers.frameOptions().disable());

        return http.build();
    }

    @Bean
    protected AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
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
        return new JwtAuthFilter(jwtUtil, userDetailsService);
    }
}
