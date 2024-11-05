package org.example.whatsdownbackend.security;

import org.example.whatsdownbackend.dto.JwtResponse;
import org.example.whatsdownbackend.entity.User;
import org.example.whatsdownbackend.entity.Role;
import org.example.whatsdownbackend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Retrieve user details
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found after OAuth2 login"));

        // Generate JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Extract roles
        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        // Create JWT response
        JwtResponse jwtResponse = new JwtResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), roles);

        // Set response content type and write the JWT
        response.setContentType("application/json");
        response.getWriter().write(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(jwtResponse));
        response.getWriter().flush();
    }
}