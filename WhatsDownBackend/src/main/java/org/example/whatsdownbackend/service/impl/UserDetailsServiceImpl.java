package org.example.whatsdownbackend.service.impl;

import org.example.whatsdownbackend.entity.Permission;
import org.example.whatsdownbackend.entity.Role;
import org.example.whatsdownbackend.entity.User;
import org.example.whatsdownbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads the user by email and maps roles and permissions to GrantedAuthorities.
     *
     * @param email The email of the user.
     * @return UserDetails containing user information and authorities.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User Not Found with email: " + email));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                // Extract permissions from roles
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());

        // Optionally, include roles as authorities (prefixed with "ROLE_")
        Set<GrantedAuthority> roleAuthorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        authorities.addAll(roleAuthorities);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
