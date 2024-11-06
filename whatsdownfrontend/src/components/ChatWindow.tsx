// src/components/ChatWindow.tsx
import React, { useState, useEffect, useRef } from 'react';
import {Box, TextField, IconButton, Typography, Avatar} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import { Message } from '../types/message';
import { useParams } from 'react-router-dom';
import { FaPaperclip, FaRegSmile } from 'react-icons/fa';
import client from '../services/socket';
import { toast } from 'react-toastify';
import ChatHeader from './ChatHeader';
import EmojiPicker, { EmojiClickData } from 'emoji-picker-react';
import { debounce } from 'lodash';
import MessageBubble from './MessageBubble';

const ChatWindow: React.FC = () => {
    const { chatId } = useParams<{ chatId: string }>();
    const { user, token } = useAuth();
    const [messages, setMessages] = useState<Message[]>([]);
    const [newMessage, setNewMessage] = useState<string>('');
    const [attachment, setAttachment] = useState<File | null>(null);
    const messagesEndRef = useRef<HTMLDivElement | null>(null);
    const [typingUsers, setTypingUsers] = useState<string[]>([]);
    const [showEmojiPicker, setShowEmojiPicker] = useState<boolean>(false);
    const typingTimeouts = useRef<{ [key: string]: NodeJS.Timeout }>({});

    useEffect(() => {
        if (!chatId) return;
        const numericChatId = parseInt(chatId, 10);

        const fetchMessages = async () => {
            try {
                const response = await api.get<{ content: Message[] }>(`/messages/${numericChatId}?page=0&size=50`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setMessages(response.data.content || []);
            } catch (error) {
                console.error('Failed to fetch messages', error);
                toast.error('Failed to load messages');
            }
        };
        fetchMessages();

        const subscription = client.subscribe(`/topic/chat/${chatId}`, (message) => {
            const receivedMessage: Message = JSON.parse(message.body);
            setMessages(prev => [...prev, receivedMessage]);
        });

        const typingSubscription = client.subscribe(`/topic/chat/${chatId}/typing`, (message) => {
            const data = JSON.parse(message.body);
            const username = data.user;

            setTypingUsers(prev => {
                if (!prev.includes(username)) {
                    return [...prev, username];
                }
                return prev;
            });

            // Clear existing timeout for the user
            if (typingTimeouts.current[username]) {
                clearTimeout(typingTimeouts.current[username]);
            }

            // Set a new timeout to remove the user after 3 seconds of inactivity
            typingTimeouts.current[username] = setTimeout(() => {
                setTypingUsers(prev => prev.filter(u => u !== username));
                delete typingTimeouts.current[username];
            }, 3000);
        });

        return () => {
            subscription.unsubscribe();
            typingSubscription.unsubscribe();
            // Clear all timeouts
            Object.values(typingTimeouts.current).forEach(timeout => clearTimeout(timeout));
        };
    }, [chatId, token]);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages, typingUsers]);

    const emitTyping = debounce(() => {
        if (!chatId) return;
        client.publish({
            destination: `/app/chat/${chatId}/typing`,
            body: JSON.stringify({ user: user?.username }),
        });
    }, 300);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setNewMessage(e.target.value);
        emitTyping();
    };

    const handleSendMessage = async () => {
        if (!newMessage.trim()) return;

        const numericChatId = parseInt(chatId || '0', 10);

        const messagePayload = {
            chatId: numericChatId,
            content: newMessage,
            attachment: attachment ? await uploadAttachment(attachment) : null, // Handle attachment
        };

        console.log("Sending message:", newMessage);

        try {
            await api.post('/messages', messagePayload, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
            setNewMessage('');
            setAttachment(null);
            setShowEmojiPicker(false);
        } catch (error) {
            console.error('Failed to send message', error);
            toast.error('Failed to send message');
        }
    };

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSendMessage();
        }
    };

    const onEmojiClick = (emojiData: EmojiClickData, event: MouseEvent) => {
        setNewMessage(prev => prev + emojiData.emoji);
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            setAttachment(e.target.files[0]);
        }
    };

    const uploadAttachment = async (file: File): Promise<string | null> => {
        const formData = new FormData();
        formData.append('file', file);
        try {
            const response = await api.post('/attachments', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': `Bearer ${token}`,
                },
            });
            return response.data.url; // Adjust based on your API response
        } catch (error) {
            console.error('Failed to upload attachment', error);
            toast.error('Failed to upload attachment');
            return null;
        }
    };

    return (
        <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
            <ChatHeader chatId={parseInt(chatId || '0', 10)} />

            <Box sx={{ flex: 1, p: 2, overflowY: 'auto', backgroundColor: '#ECE5DD' }}>
                {messages.length > 0 ? (
                    messages.map(msg => (
                        <MessageBubble
                            key={msg.id}
                            message={msg}
                            isOwnMessage={msg.senderUsername === user?.username}
                        />
                    ))
                ) : (
                    <Typography>No messages yet</Typography>
                )}
                {typingUsers.length > 0 && (
                    <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                        <Avatar sx={{ width: 24, height: 24, mr: 1 }}>
                            {typingUsers[0].charAt(0).toUpperCase()}
                        </Avatar>
                        <Typography variant="body2" color="textSecondary">
                            {typingUsers.join(', ')} {typingUsers.length > 1 ? 'are' : 'is'} typing...
                        </Typography>
                    </Box>
                )}
                <div ref={messagesEndRef} />
            </Box>

            <Box sx={{ p: 2, borderTop: '1px solid #ddd', display: 'flex', alignItems: 'center', backgroundColor: '#F0F0F0', position: 'relative' }}>
                <IconButton onClick={() => setShowEmojiPicker(!showEmojiPicker)}>
                    <FaRegSmile />
                </IconButton>
                <IconButton component="label">
                    <FaPaperclip />
                    <input type="file" hidden onChange={handleFileChange} />
                </IconButton>
                {showEmojiPicker && (
                    <Box sx={{ position: 'absolute', bottom: '60px', left: '20px' }}>
                        <EmojiPicker onEmojiClick={onEmojiClick} />
                    </Box>
                )}
                <TextField
                    variant="outlined"
                    placeholder="Type a message"
                    multiline
                    maxRows={4}
                    value={newMessage}
                    onChange={handleInputChange}
                    onKeyPress={handleKeyPress}
                    sx={{ flex: 1, mx: 1, backgroundColor: '#FFFFFF', borderRadius: 1 }}
                />
                <IconButton color="primary" onClick={handleSendMessage}>
                    <SendIcon />
                </IconButton>
            </Box>
        </Box>
    );
};

export default ChatWindow;
