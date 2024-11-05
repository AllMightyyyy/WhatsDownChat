package org.example.whatsdownbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "chat_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUser {

    @EmbeddedId
    private ChatUserId id;

    @ManyToOne
    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_role_id")
    private GroupRole groupRole;
}

