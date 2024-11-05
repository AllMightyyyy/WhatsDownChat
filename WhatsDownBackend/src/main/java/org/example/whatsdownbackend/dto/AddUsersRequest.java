package org.example.whatsdownbackend.dto;

import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUsersRequest {
    @NotEmpty(message = "User IDs list cannot be empty")
    private List<Long> userIds;
}