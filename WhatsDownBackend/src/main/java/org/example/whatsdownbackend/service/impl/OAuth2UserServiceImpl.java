package org.example.whatsdownbackend.service.impl;

import org.example.whatsdownbackend.entity.Role;
import org.example.whatsdownbackend.entity.User;
import org.example.whatsdownbackend.repository.RoleRepository;
import org.example.whatsdownbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to the default implementation for loading user details
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        // Extract provider details
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // e.g., google, facebook
        String providerId = oauth2User.getAttribute("sub") != null ? oauth2User.getAttribute("sub") :
                oauth2User.getAttribute("id"); // Google uses 'sub', Facebook uses 'id'
        String email = oauth2User.getAttribute("email");
        String username = oauth2User.getAttribute("name"); // Or another attribute based on provider

        // Check if user exists
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if(userOptional.isPresent()){
            user = userOptional.get();
            // Update provider details if necessary
            user.setProvider(registrationId.toUpperCase());
            user.setProviderId(providerId);
            userRepository.save(user);
        } else {
            // Register new user
            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setProvider(registrationId.toUpperCase());
            user.setProviderId(providerId);
            // Generate a random password or set a default one
            user.setPassword(UUID.randomUUID().toString());
            user.setStatus("online");

            // Assign default role
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            user.setRoles(Set.of(userRole));

            userRepository.save(user);
        }

        // Map roles to authorities
        Set<SimpleGrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        // Return a DefaultOAuth2User with authorities and attributes
        return new DefaultOAuth2User(authorities, oauth2User.getAttributes(), "email");
    }
}