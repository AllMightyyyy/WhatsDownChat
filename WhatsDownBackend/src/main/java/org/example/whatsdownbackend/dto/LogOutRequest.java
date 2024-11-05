// src/main/java/org/example/whatsdownbackend/dto/LogOutRequest.java

package org.example.whatsdownbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogOutRequest {
    @NotBlank
    @Email
    private String email;
}
