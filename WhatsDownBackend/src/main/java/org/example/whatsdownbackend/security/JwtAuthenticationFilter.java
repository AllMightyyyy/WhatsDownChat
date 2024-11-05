package org.example.whatsdownbackend.security;

import org.example.whatsdownbackend.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Skip JWT processing for public endpoints
        if (requestURI.startsWith("/api/auth/register") || requestURI.startsWith("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return; // Exit filter early for public paths
        }

        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String email = jwtUtils.getUserEmailFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }


    private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        logger.info("Authorization Header: " + headerAuth);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            logger.info("Extracted JWT Token: " + token);
            return token;
        }

        logger.warn("No JWT Token found in Authorization header or header is malformed.");
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip JWT filter for WebSocket requests (e.g., `/ws` or `/socket.io`)
        return request.getRequestURI().startsWith("/ws") || request.getRequestURI().startsWith("/socket.io");
    }

}
