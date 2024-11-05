package org.example.whatsdownbackend.repository;

import org.example.whatsdownbackend.entity.Chat;
import org.example.whatsdownbackend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Retrieves a paginated list of messages for a specific chat, ordered by timestamp ascending.
     *
     * @param chat     The chat whose messages are to be retrieved.
     * @param pageable Pagination information.
     * @return A page of messages.
     */
    Page<Message> findByChatOrderByTimestampAsc(Chat chat, Pageable pageable);

    /**
     * Marks multiple messages as read based on their IDs.
     *
     * @param messageIds List of message IDs to be marked as read.
     */
    void deleteByIdIn(Iterable<Long> messageIds);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.id IN :messageIds")
    void markMessagesAsRead(@Param("messageIds") Iterable<Long> messageIds);
}
