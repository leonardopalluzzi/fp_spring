package org.finalproject.java.fp_spring.Security.config;

import java.util.List;

import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
@Order(1)
public class ApiSecurityConfig {

        // va cvreatre una classe JwtAuthenticationFilter con la llogica per estrarre il
        // jwt dall'header verificarlo caricare l'tente settare l'autenticazione nel
        // securityContextHolder, serve anche una classe jwtService da mettere nei
        // servizi

        @Value("${FRONTEND_URL:http://localhost:5173}")
        private String frontendUrl;

        @Bean
        public SecurityFilterChain apiFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter)
                        throws Exception {
                http
                                .securityMatcher("/api/v1/**") // Applica solo alle rotte API
                                .cors(cors -> cors.configurationSource(request -> {
                                        CorsConfiguration conrsConfig = new CorsConfiguration();
                                        conrsConfig.setAllowedOrigins(List.of(frontendUrl));
                                        conrsConfig.setAllowedMethods(
                                                        List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                                        conrsConfig.setAllowedHeaders(List.of("*"));
                                        conrsConfig.setAllowCredentials(true);
                                        return conrsConfig;
                                }))
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth 
                                                .requestMatchers("/api/v1/auth/**").permitAll()
                                                .anyRequest().authenticated()) 
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        String.format("{\"error\": \"%s\"}",
                                                                                        authException.getMessage()));
                                                })
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        String.format("{\"error\": \"%s\"}",
                                                                                        accessDeniedException
                                                                                                        .getMessage()));
                                                }))

                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable());

                return http.build();
        }

}
