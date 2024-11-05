// src/components/NewChatModal.tsx
import React, { useState, useEffect } from 'react';
import { Modal, Box, Typography, TextField, Checkbox, FormControlLabel, Button, FormGroup, FormLabel } from '@mui/material';
import api from '../services/api';
import { Chat } from '../types/chat';
import { User } from '../types/user';
import { FaTimes } from 'react-icons/fa';

interface NewChatModalProps {
    open: boolean;
    onClose: () => void;
    onChatCreated: (chat: Chat) => void;
}

const style = {
    position: 'absolute' as 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 400,
    bgcolor: 'background.paper',
    borderRadius: 2,
    boxShadow: 24,
    p: 4,
};

const NewChatModal: React.FC<NewChatModalProps> = ({ open, onClose, onChatCreated }) => {
    const [isGroup, setIsGroup] = useState<boolean>(false);
    const [name, setName] = useState<string>('');
    const [users, setUsers] = useState<User[]>([]);
    const [selectedUserIds, setSelectedUserIds] = useState<number[]>([]);

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await api.get<User[]>('/users');
                setUsers(response.data);
            } catch (error) {
                console.error('Failed to fetch users', error);
            }
        };
        fetchUsers();
    }, []);

    const handleUserSelection = (userId: number) => {
        setSelectedUserIds(prev => {
            if (prev.includes(userId)) {
                return prev.filter(id => id !== userId);
            } else {
                return [...prev, userId];
            }
        });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const payload = {
            isGroup,
            name: isGroup ? name : undefined,
            userIds: selectedUserIds,
        };
        try {
            const response = await api.post<Chat>('/chats', payload);
            onChatCreated(response.data);
            onClose();
        } catch (error) {
            console.error('Failed to create chat', error);
        }
    };

    return (
        <Modal
            open={open}
            onClose={onClose}
            aria-labelledby="new-chat-modal-title"
            aria-describedby="new-chat-modal-description"
        >
            <Box sx={style}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography id="new-chat-modal-title" variant="h6" component="h2">
                        New Chat
                    </Typography>
                    <FaTimes style={{ cursor: 'pointer' }} onClick={onClose} />
                </Box>
                <form onSubmit={handleSubmit}>
                    <FormGroup>
                        <FormControlLabel
                            control={<Checkbox checked={isGroup} onChange={(e) => setIsGroup(e.target.checked)} />}
                            label="Create Group Chat"
                        />
                    </FormGroup>
                    {isGroup && (
                        <TextField
                            label="Group Name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                            fullWidth
                            margin="normal"
                        />
                    )}
                    <FormLabel component="legend">Select Users</FormLabel>
                    <Box sx={{ maxHeight: 200, overflowY: 'auto', border: '1px solid #ddd', borderRadius: 1, p: 1, mb: 2 }}>
                        {users.map(user => (
                            <FormControlLabel
                                key={user.id}
                                control={
                                    <Checkbox
                                        checked={selectedUserIds.includes(user.id)}
                                        onChange={() => handleUserSelection(user.id)}
                                    />
                                }
                                label={user.username}
                            />
                        ))}
                    </Box>
                    <Button type="submit" variant="contained" color="primary" fullWidth>
                        Create
                    </Button>
                </form>
            </Box>
        </Modal>
    );
};

export default NewChatModal;
