import React, { useEffect, useState } from 'react';
import { Box, Typography, IconButton, Button } from '@mui/material';
import ChatList from './ChatList';
import UserSearch from './UserSearch';
import api from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { Chat, ChatAPIResponse } from '../types/chat';
import NewChatModal from './NewChatModal';
import { User } from '../types/user';
import FeedbackForm from './FeedbackForm';
import { FaPlus } from 'react-icons/fa';
import { VscFeedback as FaFeedback } from "react-icons/vsc";

const Sidebar: React.FC = () => {
    const { token } = useAuth();
    const [chats, setChats] = useState<Chat[]>([]);
    const [isCreating, setIsCreating] = useState<boolean>(false);
    const [allUsers, setAllUsers] = useState<User[]>([]);
    const [isFeedbackOpen, setIsFeedbackOpen] = useState<boolean>(false);

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
                console.error("Failed to fetch chats or users", error);
                // show a toast or message to user
            }
        };

        fetchChatsAndUsers();
    }, [token]);

    const handleNewChat = async () => {
        setIsCreating(true);
    };

    const handleCloseModal = () => {
        setIsCreating(false);
    };

    const handleChatCreated = (newChat: Chat) => {
        setChats([newChat, ...chats]);
    };

    const handleOpenFeedback = () => {
        setIsFeedbackOpen(true);
    };

    const handleCloseFeedback = () => {
        setIsFeedbackOpen(false);
    };

    return (
        <Box sx={{ width: 300, borderRight: '1px solid #ddd', display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ p: 2, borderBottom: '1px solid #ddd', display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: 'secondary.main' }}>
                <Typography variant="h6" sx={{ color: '#FFFFFF' }}>Chats</Typography>
                <IconButton onClick={handleNewChat} sx={{ color: '#FFFFFF' }}>
                    <FaPlus />
                </IconButton>
            </Box>
            <UserSearch />
            <ChatList chats={chats} />
            <Box sx={{ p: 2, borderTop: '1px solid #ddd' }}>
                <Button
                    variant="outlined"
                    startIcon={<FaFeedback />}
                    fullWidth
                    onClick={handleOpenFeedback}
                    sx={{ color: 'secondary.main', borderColor: 'secondary.main' }}
                >
                    Feedback
                </Button>
            </Box>
            {isCreating && <NewChatModal open={isCreating} onClose={handleCloseModal} onChatCreated={handleChatCreated} />}
            {isFeedbackOpen && <FeedbackForm onClose={handleCloseFeedback} />} {/* Render FeedbackForm as modal */}
        </Box>
    );
};

export default Sidebar;
