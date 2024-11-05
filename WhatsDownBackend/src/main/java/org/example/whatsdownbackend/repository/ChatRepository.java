package org.example.whatsdownbackend.repository;

import org.example.whatsdownbackend.entity.Chat;
import org.example.whatsdownbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    /**
     * Finds all chats that a specific user is part of.
     *
     * @param user The user whose chats are to be retrieved.
     * @return A list of chats the user is involved in.
     */
    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u = :user")
    List<Chat> findAllByUser(User user);

    /**
     * Finds group chats by name containing the specified keyword.
     *
     * @param name The keyword to search within chat names.
     * @return A list of group chats matching the criteria.
     */
    @Query("SELECT c FROM Chat c WHERE c.isGroup = true AND c.name LIKE %:name%")
    List<Chat> findByIsGroupTrueAndNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Finds one-on-one chats between two users.
     *
     * @param user1 The first user.
     * @param user2 The second user.
     * @return A list containing the chat between the two users, if it exists.
     */
    @Query("SELECT c FROM Chat c JOIN c.users u1 JOIN c.users u2 WHERE u1 = :user1 AND u2 = :user2 AND c.isGroup = false")
    List<Chat> findOneOnOneChat(@Param("user1") User user1, @Param("user2") User user2);
}
