package org.example.whatsdownbackend.service.impl;

import org.example.whatsdownbackend.dto.UserProfileDto;
import org.example.whatsdownbackend.entity.User;
import org.example.whatsdownbackend.repository.UserRepository;
import org.example.whatsdownbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found."));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserProfileDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatar(),
                user.getStatus(),
                user.getProvider()
        )).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfileDto getUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found."));
        return mapToUserProfileDto(user);
    }

    /**
     * Helper method to map User entity to UserProfileDto.
     */
    private UserProfileDto mapToUserProfileDto(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatar(),
                user.getStatus(),
                user.getProvider()
        );
    }

}
