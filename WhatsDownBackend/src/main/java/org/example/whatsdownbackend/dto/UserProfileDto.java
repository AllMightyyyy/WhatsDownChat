package org.example.whatsdownbackend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String status;
    private String provider;
    // Add more fields as needed
}