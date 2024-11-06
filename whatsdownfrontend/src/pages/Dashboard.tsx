import React, { useState, useEffect } from 'react';
import { Chat, ChatAPIResponse } from '../types/chat';
import { User } from '../types/user';
import { useAuth } from '../contexts/AuthContext';
import api from "../services/api";
import { Box } from '@mui/material';
import ChatList from '../components/ChatList';
import FeedbackForm from '../components/FeedbackForm';

const Dashboard: React.FC = () => {
    const { user, token } = useAuth();
    const [chats, setChats] = useState<Chat[]>([]);
    const [isCreating, setIsCreating] = useState<boolean>(false);
    const [allUsers, setAllUsers] = useState<User[]>([]);

    useEffect(() => {
        const fetchChatsAndUsers = async () => {
            try {
                const [chatsResponse, usersResponse] = await Promise.all([
                    api.get<ChatAPIResponse[]>('/chats', {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }),
                    api.get<User[]>('/users', {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }),
                ]);

                const chatsAPI = chatsResponse.data;
                const users = usersResponse.data;

                setAllUsers(users);

                const userMap = new Map(users.map(user => [user.id, user]));

                const chatsWithMembers: Chat[] = chatsAPI.map(chat => ({
                    ...chat,
                    members: chat.userIds.map(userId => userMap.get(userId)).filter((u): u is User => !!u),
                }));

                setChats(chatsWithMembers);
            } catch (error) {
                console.error("Error fetching chats or users", error);
                // Optionally, show a toast or message to user
            }
        };

        fetchChatsAndUsers();
    }, [token]);

    const handleCreateChat = async (newChat: Chat) => {
        setChats([newChat, ...chats]);
    };

    return (
        <Box sx={{ display: 'flex', height: '100%', width: '100%' }}>
            <ChatList chats={chats} />
            {/*<FeedbackForm />*/}
        </Box>
    );
};

export default Dashboard;
