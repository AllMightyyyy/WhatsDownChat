package org.example.whatsdownbackend.repository;

import org.example.whatsdownbackend.entity.Attachment;
import org.example.whatsdownbackend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    /**
     * Finds an attachment by its associated message.
     *
     * @param message The message associated with the attachment.
     * @return An optional attachment.
     */
    Optional<Attachment> findByMessage(Message message);
}
