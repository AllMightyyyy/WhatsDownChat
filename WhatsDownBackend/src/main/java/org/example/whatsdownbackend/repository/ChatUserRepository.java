package org.example.whatsdownbackend.repository;

import org.example.whatsdownbackend.entity.ChatUser;
import org.example.whatsdownbackend.entity.ChatUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, ChatUserId> {
    // Additional query methods can be defined here if needed
}