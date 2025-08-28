package org.finalproject.java.fp_spring.Security.jwt;

import java.io.IOException;

import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetailService;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private DatabaseUserDetailService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, DatabaseUserDetailService databaseUserDetailService) {
        this.jwtService = jwtService;
        this.userDetailsService = databaseUserDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);

            if(jwtService.isTokenExpired(jwt)){
                throw new JwtException("Token expired");
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                DatabaseUserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"state\": \"expired\", \"error\": \"Token expired or invalid\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
