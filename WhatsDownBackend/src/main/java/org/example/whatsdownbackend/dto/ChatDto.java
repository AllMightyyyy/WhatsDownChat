package org.example.whatsdownbackend.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private Long id;
    private Boolean isGroup;
    private String name; // Optional for one-on-one chats
    private List<Long> userIds;
    private List<MessageDto> messages; // Optional: Include messages if needed
}