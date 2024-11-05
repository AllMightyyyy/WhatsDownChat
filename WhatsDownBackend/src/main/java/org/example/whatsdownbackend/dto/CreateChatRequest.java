package org.example.whatsdownbackend.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRequest {
    @NotNull(message = "isGroup flag is mandatory")
    private Boolean isGroup;

    private String name; // Required if isGroup is true

    private List<Long> userIds; // IDs of users to include in the chat
}