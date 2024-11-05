package org.example.whatsdownbackend.controller;

import org.example.whatsdownbackend.dto.ChatMessage;
import org.example.whatsdownbackend.dto.SendMessageRequest;
import org.example.whatsdownbackend.entity.User;
import org.example.whatsdownbackend.repository.UserRepository;
import org.example.whatsdownbackend.service.ChatService;
import org.example.whatsdownbackend.service.MessageService;
import org.example.whatsdownbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Handles incoming messages sent to /app/chat.sendMessage
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage,
                            @AuthenticationPrincipal UserDetails userDetails) {
        // Retrieve the current user
        User currentUser = userService.getCurrentUser(userDetails);

        // Create and save the message via MessageService
        ChatMessage savedMessage = messageService.processIncomingMessage(chatMessage, currentUser);

        // Broadcast the message to the chat's subscribers
        messagingTemplate.convertAndSend("/topic/chat/" + savedMessage.getChatId(), savedMessage);
    }

    /**
     * Handles user joining a chat
     */
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage,
                        @AuthenticationPrincipal UserDetails userDetails) {
        // Retrieve the current user
        User currentUser = userService.getCurrentUser(userDetails);

        // Notify all subscribers that a user has joined
        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setChatId(chatMessage.getChatId());
        joinMessage.setSenderUsername(currentUser.getUsername());
        joinMessage.setContent(currentUser.getUsername() + " has joined the chat.");
        joinMessage.setTimestamp(java.time.LocalDateTime.now());
        joinMessage.setMessageType("JOIN");

        messagingTemplate.convertAndSend("/topic/chat/" + joinMessage.getChatId(), joinMessage);
    }

    /**
     * Handles user leaving a chat
     */
    @MessageMapping("/chat.removeUser")
    public void removeUser(@Payload ChatMessage chatMessage,
                           @AuthenticationPrincipal UserDetails userDetails) {
        // Retrieve the current user
        User currentUser = userService.getCurrentUser(userDetails);

        // Notify all subscribers that a user has left
        ChatMessage leaveMessage = new ChatMessage();
        leaveMessage.setChatId(chatMessage.getChatId());
        leaveMessage.setSenderUsername(currentUser.getUsername());
        leaveMessage.setContent(currentUser.getUsername() + " has left the chat.");
        leaveMessage.setTimestamp(java.time.LocalDateTime.now());
        leaveMessage.setMessageType("LEAVE");

        messagingTemplate.convertAndSend("/topic/chat/" + leaveMessage.getChatId(), leaveMessage);
    }
    /**
     * Handles user connection events
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // Optional: Handle user connection events
        // You can retrieve session attributes if needed
    }

    /**
     * Handles user disconnection events
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            // Update user status to offline
            Optional<User> optionalUser = userService.findByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setStatus("offline");
                userRepository.save(user);

                // Notify other users about the status change
                ChatMessage leaveMessage = new ChatMessage();
                leaveMessage.setChatId(null); // Broadcast to all or specific chat
                leaveMessage.setSenderUsername("System");
                leaveMessage.setContent(username + " is now offline.");
                leaveMessage.setTimestamp(LocalDateTime.now());
                leaveMessage.setMessageType("SYSTEM");

                messagingTemplate.convertAndSend("/topic/public", leaveMessage);
            }
        }
    }
}
