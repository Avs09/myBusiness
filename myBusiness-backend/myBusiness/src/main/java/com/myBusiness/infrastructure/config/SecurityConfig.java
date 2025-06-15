package com.myBusiness.infrastructure.config;

import com.myBusiness.infrastructure.security.JwtAuthenticationFilter;
import com.myBusiness.infrastructure.security.JpaUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final JpaUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          // 1) Habilita CORS con el bean corsConfigurationSource()
          .cors(Customizer.withDefaults())
          // 2) Deshabilita CSRF (stateless REST)
          .csrf(csrf -> csrf.disable())
          // 3) Usa sesiones stateless (JWT)
          .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          // 4) Reglas de autorización
          .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .requestMatchers("/api/auth/**", "/api/users/**").permitAll()
                .requestMatchers("/api/dashboard/**").authenticated()
                .requestMatchers("/api/products/**", "/api/reports/**").authenticated()
                .requestMatchers("/api/movements/**").authenticated()
                .requestMatchers("/api/notifications/**").authenticated()
                .anyRequest().authenticated()
          )
          // 5) Proveedor de autenticación con BCrypt y UserDetailsService
          .authenticationProvider(new org.springframework.security.authentication.dao.DaoAuthenticationProvider() {{
              setUserDetailsService(userDetailsService);
              setPasswordEncoder(passwordEncoder);
          }})
          // 6) Filtro JWT
          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

  
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Orígenes permitidos
        config.setAllowedOrigins(List.of(
            "http://localhost:3000", 
            "https://mybusiness.com"
        ));
        // Métodos permitidos
        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        // Cabeceras permitidas
        config.setAllowedHeaders(List.of("*"));
        // Permitir credenciales (cookies, headers, etc.)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
