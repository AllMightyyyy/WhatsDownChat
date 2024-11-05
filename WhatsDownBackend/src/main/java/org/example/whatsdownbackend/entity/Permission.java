package org.example.whatsdownbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // e.g., CREATE_POST, DELETE_COMMENT
}


/*
SEND_MESSAGE
DELETE_MESSAGE
CREATE_GROUP
DELETE_GROUP
ADD_MEMBER
REMOVE_MEMBER
VIEW_MESSAGES
MANAGE_CONTACTS
 */