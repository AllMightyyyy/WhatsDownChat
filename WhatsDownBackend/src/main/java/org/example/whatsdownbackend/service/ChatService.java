package org.example.whatsdownbackend.service;

import org.example.whatsdownbackend.dto.ChatDto;
import org.example.whatsdownbackend.dto.CreateChatRequest;
import org.example.whatsdownbackend.dto.AddUsersRequest;
import org.example.whatsdownbackend.entity.User;

import java.util.List;

public interface ChatService {

    /**
     * Creates a new chat (group or one-on-one).
     *
     * @param createChatRequest The request containing chat details.
     * @param currentUser       The user initiating the chat creation.
     * @return The created chat details.
     */
    ChatDto createChat(CreateChatRequest createChatRequest, User currentUser);

    /**
     * Retrieves all chats that the current user is part of.
     *
     * @param currentUser The current authenticated user.
     * @return A list of chats.
     */
    List<ChatDto> getUserChats(User currentUser);

    /**
     * Retrieves details of a specific chat.
     *
     * @param chatId      The ID of the chat.
     * @param currentUser The current authenticated user.
     * @return The chat details.
     */
    ChatDto getChatDetails(Long chatId, User currentUser);

    /**
     * Adds users to a group chat.
     *
     * @param chatId          The ID of the group chat.
     * @param addUsersRequest The request containing user IDs to add.
     * @param currentUser     The current authenticated user.
     * @return The updated chat details.
     */
    ChatDto addUsersToGroup(Long chatId, AddUsersRequest addUsersRequest, User currentUser);

    /**
     * Removes a user from a chat.
     *
     * @param chatId      The ID of the chat.
     * @param userId      The ID of the user to remove.
     * @param currentUser The current authenticated user.
     * @return The updated chat details.
     */
    ChatDto removeUserFromChat(Long chatId, Long userId, User currentUser);
}