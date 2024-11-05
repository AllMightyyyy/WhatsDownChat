package org.example.whatsdownbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String fileType;

    private String fileUrl; // URL or path where the file is stored

    @OneToOne
    @JoinColumn(name = "message_id")
    private Message message;
}
