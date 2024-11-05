package org.example.whatsdownbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // e.g., GROUP_OWNER, GROUP_ADMIN, GROUP_MEMBER
}
