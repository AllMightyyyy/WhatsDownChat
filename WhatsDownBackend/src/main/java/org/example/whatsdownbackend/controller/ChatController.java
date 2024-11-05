package org.example.whatsdownbackend.controller;

import org.example.whatsdownbackend.dto.*;
import org.example.whatsdownbackend.entity.User;
import org.example.whatsdownbackend.service.ChatService;
import org.example.whatsdownbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    /**
     * Creates a new chat (group or one-on-one).
     *
     * @param createChatRequest The request containing chat details.
     * @param userDetails       The authenticated user.
     * @return The created chat details.
     */
    @PostMapping
    public ResponseEntity<ChatDto> createChat(
            @Valid @RequestBody CreateChatRequest createChatRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Fetch the current user from the UserService
        User currentUser = userService.getCurrentUser(userDetails);
        ChatDto createdChat = chatService.createChat(createChatRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChat);
    }

    /**
     * Retrieves all chats that the current user is part of.
     *
     * @param userDetails The authenticated user.
     * @return A list of chats.
     */
    @GetMapping
    public ResponseEntity<List<ChatDto>> getUserChats(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails);
        List<ChatDto> chats = chatService.getUserChats(currentUser);
        return ResponseEntity.ok(chats);
    }

    /**
     * Retrieves details of a specific chat.
     *
     * @param chatId      The ID of the chat.
     * @param userDetails The authenticated user.
     * @return The chat details.
     */
    @GetMapping("/{chatId}")
    public ResponseEntity<ChatDto> getChatDetails(
            @PathVariable Long chatId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails);
        ChatDto chatDetails = chatService.getChatDetails(chatId, currentUser);
        return ResponseEntity.ok(chatDetails);
    }

    /**
     * Adds users to a group chat.
     *
     * @param chatId           The ID of the group chat.
     * @param addUsersRequest  The request containing user IDs to add.
     * @param userDetails      The authenticated user.
     * @return The updated chat details.
     */
    @PutMapping("/{chatId}/add-users")
    public ResponseEntity<ChatDto> addUsersToGroup(
            @PathVariable Long chatId,
            @Valid @RequestBody AddUsersRequest addUsersRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails);
        ChatDto updatedChat = chatService.addUsersToGroup(chatId, addUsersRequest, currentUser);
        return ResponseEntity.ok(updatedChat);
    }

    /**
     * Removes a user from a chat.
     *
     * @param chatId      The ID of the chat.
     * @param userId      The ID of the user to remove.
     * @param userDetails The authenticated user.
     * @return The updated chat details.
     */
    @DeleteMapping("/{chatId}/remove-user/{userId}")
    public ResponseEntity<ChatDto> removeUserFromChat(
            @PathVariable Long chatId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails);
        ChatDto updatedChat = chatService.removeUserFromChat(chatId, userId, currentUser);
        return ResponseEntity.ok(updatedChat);
    }
}
