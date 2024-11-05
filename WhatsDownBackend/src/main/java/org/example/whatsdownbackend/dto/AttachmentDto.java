package org.example.whatsdownbackend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDto {
    private Long id;
    private String fileName;
    private String fileType;
    private String fileUrl;
    private Long messageId;
}