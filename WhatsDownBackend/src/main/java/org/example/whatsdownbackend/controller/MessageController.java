package org.example.whatsdownbackend.controller;

import org.example.whatsdownbackend.dto.*;
import org.example.whatsdownbackend.entity.User;
import org.example.whatsdownbackend.service.MessageService;
import org.example.whatsdownbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Sends a new message in a chat.
     *
     * @param sendMessageRequest The request containing message details.
     * @param userDetails        The authenticated user.
     * @return The created message details.
     */
    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(
            @Valid @RequestBody SendMessageRequest sendMessageRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authenticated user authorities: " + auth.getAuthorities());

        // Save the message
        MessageDto messageDto = messageService.sendMessage(sendMessageRequest, currentUser);

        // Broadcast the message to the chat's subscribers
        messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getChatId(), messageDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(messageDto);
    }

    /**
     * Retrieves paginated messages for a specific chat.
     *
     * @param chatId       The ID of the chat.
     * @param page         The page number (zero-based).
     * @param size         The size of the page.
     * @param userDetails  The authenticated user.
     * @return A paginated list of messages.
     */
    @GetMapping("/{chatId}")
    public ResponseEntity<Page<MessageDto>> getMessagesForChat(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails);
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").ascending());
        Page<MessageDto> messages = messageService.getMessagesForChat(chatId, pageable, currentUser);
        return ResponseEntity.ok(messages);
    }

    /**
     * Marks specified messages as read.
     *
     * @param chatId                  The ID of the chat.
     * @param markMessagesAsReadRequest The request containing message IDs to mark as read.
     * @param userDetails             The authenticated user.
     * @return A success message.
     */
    @PutMapping("/{chatId}/read")
    public ResponseEntity<MessageResponse> markMessagesAsRead(
            @PathVariable Long chatId,
            @Valid @RequestBody MarkMessagesAsReadRequest markMessagesAsReadRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails);
        messageService.markMessagesAsRead(chatId, markMessagesAsReadRequest, currentUser);
        return ResponseEntity.ok(new MessageResponse("Messages marked as read successfully."));
    }
}