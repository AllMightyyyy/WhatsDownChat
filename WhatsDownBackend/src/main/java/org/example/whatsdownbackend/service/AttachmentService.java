package org.example.whatsdownbackend.service;

import org.example.whatsdownbackend.dto.AttachmentDto;
import org.example.whatsdownbackend.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {

    /**
     * Uploads an attachment and links it to a message.
     *
     * @param file      The file to upload.
     * @param messageId The ID of the message to associate with the attachment.
     * @param currentUser The current authenticated user.
     * @return The uploaded attachment details.
     */
    AttachmentDto uploadAttachment(MultipartFile file, Long messageId, User currentUser);

    /**
     * Downloads an attachment by its ID.
     *
     * @param attachmentId The ID of the attachment.
     * @param currentUser The current authenticated user.
     * @return The attachment file as a byte array.
     */
    byte[] downloadAttachment(Long attachmentId, User currentUser);

    /**
     * Retrieves attachment details by its ID.
     *
     * @param attachmentId The ID of the attachment.
     * @param currentUser  The current authenticated user.
     * @return The attachment details.
     */
    AttachmentDto getAttachmentById(Long attachmentId, User currentUser);
}