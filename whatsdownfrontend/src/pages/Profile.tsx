// src/pages/Profile.tsx
import React, { useState, useEffect, FormEvent } from 'react';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import { User, UpdateUserRequest } from '../types/user';
import { Box, TextField, Button, Typography, Avatar, CircularProgress } from '@mui/material';
import { toast } from 'react-toastify';

const Profile: React.FC = () => {
    const { user: authUser, token } = useAuth();
    const [user, setUser] = useState<User | null>(authUser);
    const [username, setUsername] = useState<string>(authUser?.username || '');
    const [email, setEmail] = useState<string>(authUser?.email || '');
    const [avatar, setAvatar] = useState<File | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    useEffect(() => {
        const fetchUser = async () => {
            if (!authUser) return;
            setLoading(true);
            try {
                const response = await api.get<User>(`/users/${authUser.id}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setUser(response.data);
                setUsername(response.data.username);
                setEmail(response.data.email);
            } catch (error) {
                console.error('Failed to fetch user', error);
                toast.error('Failed to load profile');
            } finally {
                setLoading(false);
            }
        };
        fetchUser();
    }, [authUser, token]);

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        if (!user) return;

        const formData = new FormData();
        const payload: UpdateUserRequest = { username, email };
        Object.entries(payload).forEach(([key, value]) => {
            if (value) formData.append(key, value as string);
        });
        if (avatar) {
            formData.append('avatar', avatar);
        }

        try {
            const response = await api.put<User>(`/users/${user.id}`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    Authorization: `Bearer ${token}`,
                },
            });
            setUser(response.data);
            setUsername(response.data.username);
            setEmail(response.data.email);
            toast.success('Profile updated successfully');
        } catch (error) {
            console.error('Failed to update profile', error);
            toast.error('Failed to update profile');
        }
    };

    if (loading || !user) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ p: 4, maxWidth: 600, margin: '0 auto' }}>
            <Typography variant="h4" gutterBottom>
                Profile
            </Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <Avatar src={user.avatarUrl} sx={{ width: 80, height: 80, mr: 3 }}>
                    {user.username.charAt(0).toUpperCase()}
                </Avatar>
                <Button variant="contained" component="label">
                    Change Avatar
                    <input type="file" hidden accept="image/*" onChange={(e) => {
                        if (e.target.files && e.target.files[0]) {
                            setAvatar(e.target.files[0]);
                        }
                    }} />
                </Button>
            </Box>
            <form onSubmit={handleSubmit}>
                <TextField
                    label="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    fullWidth
                    margin="normal"
                />
                <Button type="submit" variant="contained" color="primary" fullWidth sx={{ mt: 2 }}>
                    Update Profile
                </Button>
            </form>
        </Box>
    );
};

export default Profile;
