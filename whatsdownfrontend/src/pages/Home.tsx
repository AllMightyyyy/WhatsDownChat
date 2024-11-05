// src/pages/Home.tsx
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Box, Typography, Button } from '@mui/material';

const Home: React.FC = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    if (user) {
        return (
            <Box sx={{ p: 4, textAlign: 'center' }}>
                <Typography variant="h3" gutterBottom>
                    Welcome Back!
                </Typography>
                <Typography variant="h6" gutterBottom>
                    You are already logged in. Go to your dashboard to start chatting.
                </Typography>
                <Box sx={{ mt: 2 }}>
                    <Button variant="contained" color="primary" onClick={() => navigate('/dashboard')} sx={{ mr: 2 }}>
                        Go to Dashboard
                    </Button>
                    <Button variant="outlined" color="secondary" onClick={handleLogout}>
                        Logout
                    </Button>
                </Box>
            </Box>
        );
    }

    return (
        <Box sx={{ p: 4, textAlign: 'center' }}>
            <Typography variant="h3" gutterBottom>
                Welcome to the Chat App
            </Typography>
            <Typography variant="h6" gutterBottom>
                Connect with friends and family in real-time.
            </Typography>
            <Box sx={{ mt: 4 }}>
                <Button variant="contained" color="primary" component={Link} to="/login" sx={{ mr: 2 }}>
                    Login
                </Button>
                <Button variant="outlined" color="secondary" component={Link} to="/register">
                    Sign Up
                </Button>
            </Box>
        </Box>
    );
};

export default Home;
