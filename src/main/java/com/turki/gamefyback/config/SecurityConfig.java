package com.turki.gamefyback.config;

import com.turki.gamefyback.security.FirebaseAuthenticationFilter;
import com.turki.gamefyback.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Autowired
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(eh -> eh.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/users/**", "/api/auth/forgot-password-request",
                                "/api/auth/verify-reset-code",
                                "/api/auth/reset-password",
                                "/api/reservations/pc-availability",
                                "/api/reservations/pc/*/booked-slots",
                                "/api/users/count")
                        .permitAll()
                        .requestMatchers("/api/reservations").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Replace with your frontend's actual origin (e.g., "http://localhost:5173", "https://yourfrontend.com")
        // Read allowed origins from environment variable
        String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        //  if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
        // configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        // } else {
            // Default fallback
            configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        // }
        // configuration.setAllowedOrigins(List.of("http://localhost:5173","http://localhost:5174","http://217.182.93.224:8089")); // Allow your Vite frontend origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow common HTTP methods
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers (including Authorization, Content-Type)
        configuration.setAllowCredentials(true); // Allow sending cookies/auth headers
        configuration.setMaxAge(3600L); // How long the CORS pre-flight request can be cached
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this CORS config to all paths
        return source;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

