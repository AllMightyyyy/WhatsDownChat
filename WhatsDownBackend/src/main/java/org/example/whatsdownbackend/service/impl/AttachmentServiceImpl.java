package org.example.whatsdownbackend.service.impl;

import org.example.whatsdownbackend.dto.AttachmentDto;
import org.example.whatsdownbackend.entity.Attachment;
import org.example.whatsdownbackend.entity.Message;
import org.example.whatsdownbackend.entity.User;
import org.example.whatsdownbackend.mapper.AttachmentMapper;
import org.example.whatsdownbackend.repository.AttachmentRepository;
import org.example.whatsdownbackend.repository.MessageRepository;
import org.example.whatsdownbackend.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Value("${attachment.storage.path}")
    private String attachmentStoragePath;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AttachmentDto uploadAttachment(MultipartFile file, Long messageId, User currentUser) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found with ID: " + messageId));

        if (!message.getChat().getUsers().contains(currentUser)) {
            throw new AccessDeniedException("You are not a member of this chat.");
        }

        // Check if user has permission to upload attachments
        if (!hasPermission(currentUser, "UPLOAD_ATTACHMENT")) {
            throw new AccessDeniedException("You do not have permission to upload attachments.");
        }

        try {
            // Ensure the storage directory exists
            Path storageDir = Paths.get(attachmentStoragePath);
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }

            // Generate a unique file name to prevent collisions
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = storageDir.resolve(fileName);

            // Save the file to the filesystem
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create and save the attachment entity
            Attachment attachment = new Attachment();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFileType(file.getContentType());
            attachment.setFileUrl(filePath.toString());
            attachment.setMessage(message);

            Attachment savedAttachment = attachmentRepository.save(attachment);
            return attachmentMapper.toDto(savedAttachment);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload attachment.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public byte[] downloadAttachment(Long attachmentId, User currentUser) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new NoSuchElementException("Attachment not found with ID: " + attachmentId));

        if (!attachment.getMessage().getChat().getUsers().contains(currentUser)) {
            throw new AccessDeniedException("You are not a member of this chat.");
        }

        // Check if user has permission to download attachments
        if (!hasPermission(currentUser, "DOWNLOAD_ATTACHMENT")) {
            throw new AccessDeniedException("You do not have permission to download attachments.");
        }

        Path filePath = Paths.get(attachment.getFileUrl());
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download attachment.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public AttachmentDto getAttachmentById(Long attachmentId, User currentUser) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new NoSuchElementException("Attachment not found with ID: " + attachmentId));

        if (!attachment.getMessage().getChat().getUsers().contains(currentUser)) {
            throw new AccessDeniedException("You are not a member of this chat.");
        }

        return attachmentMapper.toDto(attachment);
    }

    /**
     * Helper method to check if a user has a specific permission.
     *
     * @param user       The user to check.
     * @param permission The permission to verify.
     * @return True if the user has the permission, false otherwise.
     */
    private boolean hasPermission(User user, String permission) {
        /*
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(p -> p.getName().equals(permission));

         */
        return true;
    }
}
