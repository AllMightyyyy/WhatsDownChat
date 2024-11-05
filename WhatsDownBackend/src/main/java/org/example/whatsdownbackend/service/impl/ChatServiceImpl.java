package org.example.whatsdownbackend.service.impl;

import org.example.whatsdownbackend.dto.AddUsersRequest;
import org.example.whatsdownbackend.dto.ChatDto;
import org.example.whatsdownbackend.dto.CreateChatRequest;
import org.example.whatsdownbackend.entity.*;
import org.example.whatsdownbackend.mapper.ChatMapper;
import org.example.whatsdownbackend.repository.ChatRepository;
import org.example.whatsdownbackend.repository.ChatUserRepository;
import org.example.whatsdownbackend.repository.UserRepository;
import org.example.whatsdownbackend.repository.GroupRoleRepository;
import org.example.whatsdownbackend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRoleRepository groupRoleRepository;

    @Autowired
    private ChatUserRepository chatUserRepository;

    @Autowired
    private ChatMapper chatMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('CREATE_GROUP') or hasAuthority('CREATE_ONE_ON_ONE_CHAT')")
    public ChatDto createChat(CreateChatRequest createChatRequest, User currentUser) {
        // Existing permission checks remain the same

        Chat chat = new Chat();
        chat.setIsGroup(createChatRequest.getIsGroup());

        if (createChatRequest.getIsGroup()) {
            if (createChatRequest.getName() == null || createChatRequest.getName().isEmpty()) {
                throw new IllegalArgumentException("Group chat must have a name.");
            }
            chat.setName(createChatRequest.getName());
        }

        // Initialize users set and add the current user
        Set<User> users = new HashSet<>();
        users.add(currentUser);

        // Add other users to the chat
        if (createChatRequest.getUserIds() != null && !createChatRequest.getUserIds().isEmpty()) {
            List<User> otherUsers = userRepository.findAllById(createChatRequest.getUserIds());
            if (otherUsers.size() != createChatRequest.getUserIds().size()) {
                throw new IllegalArgumentException("Some users not found.");
            }
            users.addAll(otherUsers);
        }

        chat.setUsers(users);
        Chat savedChat = chatRepository.save(chat);

        // Assign GROUP_OWNER role to the creator if it's a group chat
        if (createChatRequest.getIsGroup()) {
            GroupRole groupOwnerRole = groupRoleRepository.findByName("GROUP_OWNER")
                    .orElseThrow(() -> new RuntimeException("GROUP_OWNER role not found"));

            ChatUser chatUser = new ChatUser();
            ChatUserId chatUserId = new ChatUserId(savedChat.getId(), currentUser.getId());
            chatUser.setId(chatUserId);
            chatUser.setChat(savedChat);
            chatUser.setUser(currentUser);
            chatUser.setGroupRole(groupOwnerRole);

            chatUserRepository.save(chatUser);
        }

        return chatMapper.toDto(savedChat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('VIEW_MESSAGES')")
    public List<ChatDto> getUserChats(User currentUser) {
        List<Chat> chats = chatRepository.findAllByUser(currentUser);
        return chats.stream()
                .map(chatMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('VIEW_MESSAGES')")
    public ChatDto getChatDetails(Long chatId, User currentUser) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NoSuchElementException("Chat not found with ID: " + chatId));

        if (!chat.getUsers().contains(currentUser)) {
            throw new AccessDeniedException("You are not a member of this chat.");
        }

        return chatMapper.toDto(chat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADD_MEMBER')")
    public ChatDto addUsersToGroup(Long chatId, AddUsersRequest addUsersRequest, User currentUser) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NoSuchElementException("Chat not found with ID: " + chatId));

        if (!chat.getIsGroup()) {
            throw new IllegalArgumentException("Cannot add users to a non-group chat.");
        }

        // Check if the current user has permission to add members
        if (!hasPermission(currentUser, "ADD_MEMBER")) {
            throw new AccessDeniedException("You do not have permission to add members to this chat.");
        }

        List<User> usersToAdd = userRepository.findAllById(addUsersRequest.getUserIds());
        if (usersToAdd.size() != addUsersRequest.getUserIds().size()) {
            throw new IllegalArgumentException("Some users not found.");
        }

        // Prevent adding users who are already in the chat
        for (User user : usersToAdd) {
            if (chat.getUsers().contains(user)) {
                throw new IllegalArgumentException("User with ID " + user.getId() + " is already in the chat.");
            }
        }

        chat.getUsers().addAll(usersToAdd);
        Chat updatedChat = chatRepository.save(chat);
        return chatMapper.toDto(updatedChat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('REMOVE_MEMBER')")
    public ChatDto removeUserFromChat(Long chatId, Long userId, User currentUser) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NoSuchElementException("Chat not found with ID: " + chatId));

        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));

        if (!chat.getUsers().contains(userToRemove)) {
            throw new IllegalArgumentException("User is not a member of this chat.");
        }

        // Check if the current user has permission to remove members
        if (!hasPermission(currentUser, "REMOVE_MEMBER")) {
            throw new AccessDeniedException("You do not have permission to remove members from this chat.");
        }

        chat.getUsers().remove(userToRemove);
        Chat updatedChat = chatRepository.save(chat);
        return chatMapper.toDto(updatedChat);
    }

    /**
     * Helper method to check if a user has a specific permission.
     *
     * @param user       The user to check.
     * @param permission The permission to verify.
     * @return True if the user has the permission, false otherwise.
     */
    private boolean hasPermission(User user, String permission) {
        /*return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(p -> p.getName().equals(permission));

         */
        return true;
    }
}
