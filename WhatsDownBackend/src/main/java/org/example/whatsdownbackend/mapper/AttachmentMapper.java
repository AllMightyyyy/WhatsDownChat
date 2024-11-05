package org.example.whatsdownbackend.mapper;

import org.example.whatsdownbackend.dto.AttachmentDto;
import org.example.whatsdownbackend.entity.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    @Mapping(target = "messageId", source = "message.id")
    AttachmentDto toDto(Attachment attachment);
}