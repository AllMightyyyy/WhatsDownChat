import React from 'react';
import { Chat } from '../types/chat';
import { Link, useLocation } from 'react-router-dom';
import {
    List,
    ListItem,
    ListItemAvatar,
    Avatar,
    ListItemText,
    Typography,
    Badge,
    Divider,
    Box,
} from '@mui/material';
import { FaRegSmile } from 'react-icons/fa';
import { useAuth } from '../contexts/AuthContext';

interface ChatListProps {
    chats: Chat[];
}

const ChatList: React.FC<ChatListProps> = ({ chats }) => {
    const location = useLocation();
    const currentChatId = parseInt(location.pathname.split('/').pop() || '0', 10);

    return (
        <List sx={{ flex: 1, overflowY: 'auto', width: '100%', bgcolor: 'background.paper' }}>
            {chats.map(chat => (
                <React.Fragment key={chat.id}>
                    <ListItem
                        component={Link}
                        to={`/chat/${chat.id}`}
                        alignItems="flex-start"
                        sx={{
                            paddingY: 1.5,
                            backgroundColor: chat.id === currentChatId ? 'rgba(0, 153, 51, 0.1)' : 'inherit',
                            '&:hover': {
                                backgroundColor: 'rgba(0, 153, 51, 0.05)',
                            },
                            textDecoration: 'none',
                            color: 'inherit',
                            ...(chat.id === currentChatId && { selected: true })
                        }}
                    >
                        <ChatItemContent chat={chat} />
                    </ListItem>
                    <Divider component="li" />
                </React.Fragment>
            ))}
        </List>
    );
};

interface ChatItemContentProps {
    chat: Chat;
}

const ChatItemContent: React.FC<ChatItemContentProps> = ({ chat }) => {
    const { user: currentUser } = useAuth();
    const hasUnreadMessages = Boolean(chat.latestMessage && !chat.latestMessage.isRead);
    const formattedTime = new Date(chat.latestMessage?.timestamp || '').toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit',
    });

    const displayName = chat.isGroup
        ? chat.name || 'Unnamed Group'
        : chat.members.filter(member => member.id !== currentUser?.id).map(m => m.username).join(', ') || 'Unknown User';

    const avatarUrl = chat.isGroup ? null : chat.members[0]?.avatarUrl;

    return (
        <>
            <ListItemAvatar>
                <Badge
                    color="primary"
                    variant="dot"
                    overlap="circular"
                    invisible={!hasUnreadMessages}
                    anchorOrigin={{
                        vertical: 'bottom',
                        horizontal: 'right',
                    }}
                >
                    <Avatar>
                        {chat.isGroup ? (
                            <FaRegSmile />
                        ) : avatarUrl ? (
                            <img
                                src={avatarUrl}
                                alt={displayName}
                                style={{ width: '100%', height: '100%', borderRadius: '50%' }}
                            />
                        ) : (
                            displayName.charAt(0).toUpperCase()
                        )}
                    </Avatar>
                </Badge>
            </ListItemAvatar>
            <ListItemText
                primary={
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Typography variant="subtitle1" sx={{ fontWeight: hasUnreadMessages ? 'bold' : 'normal' }}>
                            {displayName}
                        </Typography>
                        <Typography variant="caption" color="textSecondary">
                            {formattedTime}
                        </Typography>
                    </Box>
                }
                secondary={
                    <Typography
                        variant="body2"
                        color="textSecondary"
                        noWrap
                        sx={{ fontWeight: hasUnreadMessages ? 'bold' : 'normal' }}
                    >
                        {chat.latestMessage?.content || 'No messages yet'}
                    </Typography>
                }
            />
        </>
    );
};

export default ChatList;
