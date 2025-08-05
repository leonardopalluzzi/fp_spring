package org.finalproject.java.fp_spring.Security.config;

import org.finalproject.java.fp_spring.Security.jwt.JwtAuthenticationFilter;
import org.finalproject.java.fp_spring.Security.jwt.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration {

    // QUI BISOGNA SEPARARE LA SICUREZZA DELL'MVC DA QUELLA DELL'API CON 2 FILTER
    // CHAIN SEPARATE

    // databse userdatils service
    @Bean
    DatabaseUserDetailService userDetailService() {
        return new DatabaseUserDetailService();
    }

    // password encoder
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // auth provider
    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, DatabaseUserDetailService databaseUserDetailService) {
        return new JwtAuthenticationFilter(jwtService, databaseUserDetailService);
    }

    @Bean
    public JwtService jwtService(){
        return new JwtService();
    }
}
