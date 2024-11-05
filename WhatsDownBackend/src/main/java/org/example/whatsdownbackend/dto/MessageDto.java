package org.example.whatsdownbackend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private Boolean isRead;
    private Long chatId;
    private Long senderId;
    private String senderUsername; // Optional: Include sender's username
    private AttachmentDto attachment; // Optional: Include attachment details
}