package org.finalproject.java.fp_spring.Security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(2)
public class MvcSecurityConfig {
    // filter chain per mvc
    @Bean
    public SecurityFilterChain mvcFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().hasAuthority("SUPERADMIN"))
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .permitAll());
        return http.build();
    }
}
