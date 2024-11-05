package org.example.whatsdownbackend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private Long chatId;
    private String content;
}