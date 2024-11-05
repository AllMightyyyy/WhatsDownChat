package org.example.whatsdownbackend.mapper;

import org.example.whatsdownbackend.dto.MessageDto;
import org.example.whatsdownbackend.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {AttachmentMapper.class})
public interface MessageMapper {

    @Mapping(target = "id", source = "message.id")
    @Mapping(target = "content", source = "message.content")
    @Mapping(target = "timestamp", source = "message.timestamp")
    @Mapping(target = "isRead", expression = "java(message.getIsRead())")
    @Mapping(target = "senderUsername", expression = "java(getSenderUsername(message))")
    @Mapping(target = "senderId", expression = "java(getSenderId(message))")
    @Mapping(target = "chatId", expression = "java(getChatId(message))")
    MessageDto toDto(Message message);

    default String getSenderUsername(Message message) {
        return message.getSender() != null ? message.getSender().getUsername() : null;
    }

    default Long getSenderId(Message message) {
        return message.getSender() != null ? message.getSender().getId() : null;
    }

    default Long getChatId(Message message) {
        return message.getChat() != null ? message.getChat().getId() : null;
    }
}
