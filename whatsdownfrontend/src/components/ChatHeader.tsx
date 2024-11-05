import React, { useEffect, useState } from 'react';
import { Box, Avatar, Typography, IconButton } from '@mui/material';
import { FaPhone, FaVideo, FaEllipsisV } from 'react-icons/fa';
import api from '../services/api';
import { Chat, ChatAPIResponse } from '../types/chat';
import { User } from '../types/user';
import { toast } from 'react-toastify';
import { useAuth } from '../contexts/AuthContext';

interface ChatHeaderProps {
    chatId: number;
}

const ChatHeader: React.FC<ChatHeaderProps> = ({ chatId }) => {
    const [chat, setChat] = useState<Chat | null>(null);
    const { token } = useAuth();
    const [allUsers, setAllUsers] = useState<User[]>([]);

    useEffect(() => {
        const fetchChatAndUsers = async () => {
            try {
                const [chatResponse, usersResponse] = await Promise.all([
                    api.get<ChatAPIResponse>(`/chats/${chatId}`, {
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

                const chatAPI = chatResponse.data;
                const users = usersResponse.data;

                setAllUsers(users);

                const userMap = new Map(users.map(user => [user.id, user]));

                const chatWithMembers: Chat = {
                    ...chatAPI,
                    members: chatAPI.userIds.map(userId => userMap.get(userId)).filter((u): u is User => !!u),
                };

                setChat(chatWithMembers);
            } catch (error) {
                console.error('Failed to fetch chat details', error);
                toast.error('Failed to load chat details');
            }
        };

        fetchChatAndUsers();
    }, [chatId, token]);

    if (!chat) {
        return (
            <Box sx={{ p: 2, borderBottom: '1px solid #ddd', display: 'flex', alignItems: 'center', backgroundColor: 'secondary.main' }}>
                <Typography variant="h6" sx={{ color: '#FFFFFF' }}>Loading...</Typography>
            </Box>
        );
    }

    const displayName = chat.isGroup
        ? chat.name || 'Unnamed Group'
        : chat.members.map(m => m.username).join(', ');

    const statusText = chat.isGroup
        ? `${chat.members.length} members`
        : 'Online';

    return (
        <Box sx={{ p: 2, borderBottom: '1px solid #ddd', display: 'flex', alignItems: 'center', backgroundColor: 'secondary.main' }}>
            <Avatar sx={{ bgcolor: '#25D366', mr: 2 }}>
                {chat.isGroup ? (
                    chat.name?.charAt(0).toUpperCase() || 'G'
                ) : (
                    chat.members[0]?.username.charAt(0).toUpperCase() || 'U'
                )}
            </Avatar>
            <Box sx={{ flex: 1 }}>
                <Typography variant="h6" sx={{ color: '#FFFFFF' }}>
                    {displayName}
                </Typography>
                <Typography variant="body2" sx={{ color: '#D4FCDD' }}>
                    {statusText}
                </Typography>
            </Box>
            <IconButton sx={{ color: '#FFFFFF' }}>
                <FaPhone />
            </IconButton>
            <IconButton sx={{ color: '#FFFFFF' }}>
                <FaVideo />
            </IconButton>
            <IconButton sx={{ color: '#FFFFFF' }}>
                <FaEllipsisV />
            </IconButton>
        </Box>
    );
};

export default ChatHeader;
