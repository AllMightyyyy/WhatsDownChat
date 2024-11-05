package org.example.whatsdownbackend.service;

import org.example.whatsdownbackend.dto.ChatMessage;
import org.example.whatsdownbackend.dto.MessageDto;
import org.example.whatsdownbackend.dto.SendMessageRequest;
import org.example.whatsdownbackend.dto.MarkMessagesAsReadRequest;
import org.example.whatsdownbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    /**
     * Sends a new message in a chat.
     *
     * @param sendMessageRequest The request containing message details.
     * @param currentUser        The user sending the message.
     * @return The created message details.
     */
    MessageDto sendMessage(SendMessageRequest sendMessageRequest, User currentUser);

    /**
     * Retrieves paginated messages for a specific chat.
     *
     * @param chatId      The ID of the chat.
     * @param pageable    Pagination information.
     * @param currentUser The current authenticated user.
     * @return A paginated list of messages.
     */
    Page<MessageDto> getMessagesForChat(Long chatId, Pageable pageable, User currentUser);

    /**
     * Marks specified messages as read.
     *
     * @param chatId                    The ID of the chat.
     * @param markMessagesAsReadRequest The request containing message IDs to mark as read.
     * @param currentUser               The current authenticated user.
     */
    void markMessagesAsRead(Long chatId, MarkMessagesAsReadRequest markMessagesAsReadRequest, User currentUser);

    /**
     * Processes incoming chat messages from WebSocket and saves them.
     *
     * @param chatMessage The incoming chat message payload.
     * @param currentUser The user sending the message.
     * @return The saved ChatMessage DTO.
     */
    ChatMessage processIncomingMessage(ChatMessage chatMessage, User currentUser);
}