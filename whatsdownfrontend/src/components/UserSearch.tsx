// src/components/UserSearch.tsx
import React, { useState, ChangeEvent } from 'react';
import { UserSearchResult } from '../types/userSearch';
import api from "../services/api";
import { TextField, List, ListItem, ListItemAvatar, Avatar, ListItemText, Box } from '@mui/material';
import { FaUserPlus } from 'react-icons/fa';
import { toast } from 'react-toastify';

const UserSearch: React.FC = () => {
    const [query, setQuery] = useState<string>('');
    const [results, setResults] = useState<UserSearchResult[]>([]);

    const handleChange = async (e: ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setQuery(value);
        if (value.length > 2) {
            try {
                const response = await api.get<UserSearchResult[]>(`/users?search=${value}`);
                setResults(response.data);
            } catch (error) {
                console.error('Search failed', error);
                toast.error('Failed to search users');
            }
        } else {
            setResults([]);
        }
    };

    const handleAddUser = (userId: string) => {
        // Logic to add user to a new chat or group
        toast.info('Feature not implemented yet');
    };

    return (
        <Box sx={{ p: 2 }}>
            <TextField
                fullWidth
                variant="outlined"
                placeholder="Search users..."
                value={query}
                onChange={handleChange}
                InputProps={{
                    startAdornment: <FaUserPlus style={{ marginRight: '8px' }} />,
                }}
            />
            <List>
                {results.map(user => (
                    <ListItem
                        key={user.id}
                        button
                        component="li"
                        onClick={() => handleAddUser(user.id)}
                        {...({} as any)}
                    >
                        <ListItemAvatar>
                            <Avatar src={user.avatarUrl}>
                                {user.username.charAt(0).toUpperCase()}
                            </Avatar>
                        </ListItemAvatar>
                        <ListItemText primary={user.username} secondary={user.email} />
                    </ListItem>
                ))}
            </List>
        </Box>
    );
};

export default UserSearch;
