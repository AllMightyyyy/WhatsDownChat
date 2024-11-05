package org.example.whatsdownbackend.mapper;

import org.example.whatsdownbackend.dto.ChatDto;
import org.example.whatsdownbackend.entity.Chat;
import org.example.whatsdownbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {MessageMapper.class})
public interface ChatMapper {

    @Mapping(target = "id", source = "chat.id")
    @Mapping(target = "isGroup", expression = "java(chat.getIsGroup())")
    @Mapping(target = "name", source = "chat.name")
    @Mapping(target = "userIds", expression = "java(mapUserIds(chat.getUsers()))")
    @Mapping(target = "messages", source = "chat.messages")
    ChatDto toDto(Chat chat);

    default List<Long> mapUserIds(Set<User> users) {
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
