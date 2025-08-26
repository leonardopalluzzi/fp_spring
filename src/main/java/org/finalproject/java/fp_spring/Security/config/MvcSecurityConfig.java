package org.finalproject.java.fp_spring.Security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@Order(2)
public class MvcSecurityConfig {
        @Bean
        public SecurityFilterChain mvcFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                                                .requestMatchers("/admin/**").hasAuthority("ADMIN").anyRequest()
                                                .authenticated())
                                .formLogin(form -> form
                                                .defaultSuccessUrl("/", true)
                                                .permitAll())
                                .logout(logout -> logout.permitAll())
                                .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));
                return http.build();
        }
}
