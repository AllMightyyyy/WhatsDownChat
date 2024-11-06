// src/components/MessageBubble.tsx
import React from 'react';
import { Box, Typography, Avatar } from '@mui/material';
import { Message } from '../types/message';

interface MessageBubbleProps {
    message: Message;
    isOwnMessage: boolean;
}

const MessageBubble: React.FC<MessageBubbleProps> = ({ message, isOwnMessage }) => {
    return (
        <Box
            sx={{
                display: 'flex',
                justifyContent: isOwnMessage ? 'flex-end' : 'flex-start',
                mb: 1,
            }}
        >
            {!isOwnMessage && (
                <Avatar sx={{ mr: 1 }}>
                    {message.senderUsername.charAt(0).toUpperCase()}
                </Avatar>
            )}
            <Box
                sx={{
                    maxWidth: '60%',
                    bgcolor: isOwnMessage ? '#DCF8C6' : '#FFFFFF',
                    color: '#000',
                    p: 1,
                    borderRadius: 2,
                    boxShadow: 1,
                }}
            >
                <Typography variant="body1" dangerouslySetInnerHTML={{ __html: message.content }} />
                {message.attachment && (
                    <Box sx={{ mt: 1 }}>
                        {message.fileType?.startsWith('image/') && (
                            <img src={message.attachment} alt={message.fileName} style={{ maxWidth: '100%', borderRadius: '8px' }} />
                        )}
                        {message.fileType?.startsWith('video/') && (
                            <video controls style={{ maxWidth: '100%', borderRadius: '8px' }}>
                                <source src={message.attachment} type={message.fileType} />
                                Your browser does not support the video tag.
                            </video>
                        )}
                    </Box>
                )}
                <Typography variant="caption" sx={{ display: 'block', textAlign: 'right', color: 'text.secondary' }}>
                    {new Date(message.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </Typography>
            </Box>
            {isOwnMessage && (
                <Typography variant="caption" sx={{ ml: 1, color: message.isRead ? 'secondary.main' : 'text.secondary' }}>
                    {message.isRead ? '✓✓' : '✓'}
                </Typography>
            )}
        </Box>
    );
};

export default MessageBubble;
