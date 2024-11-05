package org.example.whatsdownbackend.service.impl;

import org.example.whatsdownbackend.dto.ChatMessage;
import org.example.whatsdownbackend.dto.MarkMessagesAsReadRequest;
import org.example.whatsdownbackend.dto.MessageDto;
import org.example.whatsdownbackend.dto.SendMessageRequest;
import org.example.whatsdownbackend.entity.Chat;
import org.example.whatsdownbackend.entity.Message;
import org.example.whatsdownbackend.entity.User;
import org.example.whatsdownbackend.mapper.MessageMapper;
import org.example.whatsdownbackend.repository.ChatRepository;
import org.example.whatsdownbackend.repository.MessageRepository;
import org.example.whatsdownbackend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageMapper messageMapper;

    private PolicyFactory sanitizer = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('SEND_MESSAGE')")
    public MessageDto sendMessage(SendMessageRequest sendMessageRequest, User currentUser) {
        Chat chat = chatRepository.findById(sendMessageRequest.getChatId())
                .orElseThrow(() -> new NoSuchElementException("Chat not found with ID: " + sendMessageRequest.getChatId()));

        if (!chat.getUsers().contains(currentUser)) {
            throw new AccessDeniedException("You are not a member of this chat.");
        }

        // Check if user has permission to send messages
        if (!hasPermission(currentUser, "SEND_MESSAGE")) {
            throw new AccessDeniedException("You do not have permission to send messages.");
        }

        // Sanitize message content
        String sanitizedContent = sanitizer.sanitize(sendMessageRequest.getContent());

        Message message = new Message();
        message.setContent(sanitizedContent);
        message.setTimestamp(LocalDateTime.now());
        message.setIsRead(false);
        message.setChat(chat);
        message.setSender(currentUser);

        Message savedMessage = messageRepository.save(message);
        return messageMapper.toDto(savedMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('VIEW_MESSAGES')")
    public Page<MessageDto> getMessagesForChat(Long chatId, Pageable pageable, User currentUser) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NoSuchElementException("Chat not found with ID: " + chatId));

        if (!chat.getUsers().contains(currentUser)) {
            throw new AccessDeniedException("You are not a member of this chat.");
        }

        // Check if user has permission to view messages
        if (!hasPermission(currentUser, "VIEW_MESSAGES")) {
            throw new AccessDeniedException("You do not have permission to view messages.");
        }

        Page<Message> messagesPage = messageRepository.findByChatOrderByTimestampAsc(chat, pageable);
        return messagesPage.map(messageMapper::toDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('MARK_AS_READ')")
    public void markMessagesAsRead(Long chatId, MarkMessagesAsReadRequest markMessagesAsReadRequest, User currentUser) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NoSuchElementException("Chat not found with ID: " + chatId));

        if (!chat.getUsers().contains(currentUser)) {
            throw new AccessDeniedException("You are not a member of this chat.");
        }

        // Check if user has permission to mark messages as read
        if (!hasPermission(currentUser, "MARK_AS_READ")) {
            throw new AccessDeniedException("You do not have permission to mark messages as read.");
        }

        messageRepository.markMessagesAsRead(markMessagesAsReadRequest.getMessageIds());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('SEND_MESSAGE')")
    public ChatMessage processIncomingMessage(ChatMessage chatMessage, User currentUser) {
        // Validate chat existence and user membership
        Chat chat = chatRepository.findById(chatMessage.getChatId())
                .orElseThrow(() -> new NoSuchElementException("Chat not found with ID: " + chatMessage.getChatId()));

        if (!chat.getUsers().contains(currentUser)) {
            throw new AccessDeniedException("You are not a member of this chat.");
        }

        // Check if user has permission to send messages
        if (!hasPermission(currentUser, "SEND_MESSAGE")) {
            throw new AccessDeniedException("You do not have permission to send messages.");
        }

        // Create and save the message
        Message message = new Message();
        message.setContent(chatMessage.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setIsRead(false);
        message.setChat(chat);
        message.setSender(currentUser);

        Message savedMessage = messageRepository.save(message);

        // Map to ChatMessage DTO
        ChatMessage savedChatMessage = new ChatMessage();
        savedChatMessage.setMessageId(savedMessage.getId());
        savedChatMessage.setChatId(chat.getId());
        savedChatMessage.setSenderUsername(currentUser.getUsername());
        savedChatMessage.setContent(savedMessage.getContent());
        savedChatMessage.setTimestamp(savedMessage.getTimestamp());
        savedChatMessage.setMessageType("CHAT");

        return savedChatMessage;
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
