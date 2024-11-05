package org.example.whatsdownbackend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private String username;
    private String avatar;
    private String status;
}