package org.example.whatsdownbackend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long messageId;
    private Long chatId;
    private String senderUsername;
    private String content;
    private LocalDateTime timestamp;
    private String messageType; // e.g., CHAT, JOIN, LEAVE, SYSTEM
}
