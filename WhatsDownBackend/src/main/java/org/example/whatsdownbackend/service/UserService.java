package org.example.whatsdownbackend.service;

import org.example.whatsdownbackend.dto.UserProfileDto;
import org.example.whatsdownbackend.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {

    /**
     * Fetches the User entity based on UserDetails.
     *
     * @param userDetails The authenticated user's details.
     * @return The User entity.
     */
    User getCurrentUser(UserDetails userDetails);

    /**
     * Finds a user by username.
     *
     * @param username The username.
     * @return The User entity.
     */
    Optional<User> findByUsername(String username);

    List<UserProfileDto> getAllUsers();
}
