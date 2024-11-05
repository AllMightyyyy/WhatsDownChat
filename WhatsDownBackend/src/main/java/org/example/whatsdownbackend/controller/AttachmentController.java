package org.example.whatsdownbackend.controller;

import org.example.whatsdownbackend.dto.AttachmentDto;
import org.example.whatsdownbackend.dto.MessageResponse;
import org.example.whatsdownbackend.entity.User;
import org.example.whatsdownbackend.service.AttachmentService;
import org.example.whatsdownbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private UserService userService;

    /**
     * Uploads an attachment and links it to a message.
     *
     * @param file         The file to upload.
     * @param messageId    The ID of the message to associate with the attachment.
     * @param userDetails  The authenticated user.
     * @return The uploaded attachment details.
     */
    @PostMapping
    //@PreAuthorize("hasAuthority('UPLOAD_ATTACHMENT')")
    public ResponseEntity<AttachmentDto> uploadAttachment(
            @RequestParam("file") @Valid MultipartFile file,
            @RequestParam("messageId") Long messageId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails);
        AttachmentDto attachmentDto = attachmentService.uploadAttachment(file, messageId, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(attachmentDto);
    }

    /**
     * Downloads an attachment by its ID.
     *
     * @param attachmentId The ID of the attachment.
     * @param userDetails  The authenticated user.
     * @return The attachment file as a byte array.
     */
    @GetMapping("/{attachmentId}")
    //@PreAuthorize("hasAuthority('DOWNLOAD_ATTACHMENT')")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long attachmentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails);
        byte[] fileData = attachmentService.downloadAttachment(attachmentId, currentUser);

        // Fetch Attachment details for file name and content type
        AttachmentDto attachmentDto = attachmentService.getAttachmentById(attachmentId, currentUser);

        // Create a resource from the file data
        ByteArrayResource resource = new ByteArrayResource(fileData);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachmentDto.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachmentDto.getFileName() + "\"")
                .body(resource);
    }
}