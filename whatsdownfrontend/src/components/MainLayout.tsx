// src/components/MainLayout.tsx
import React from 'react';
import { Box } from '@mui/material';
import Sidebar from './Sidebar';
import ChatWindow from './ChatWindow';
import { Routes, Route } from 'react-router-dom';
import Dashboard from '../pages/Dashboard';
import Profile from '../pages/Profile';
import Roles from '../pages/Roles';
import ChatRoom from '../pages/ChatRoom';

const MainLayout: React.FC = () => {
    return (
        <Box sx={{ display: 'flex', height: '100vh' }}>
            <Sidebar />
            <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                <Routes>
                    <Route path="dashboard" element={<Dashboard />} />
                    <Route path="profile" element={<Profile />} />
                    <Route path="roles" element={<Roles />} />
                    <Route path="chat/:chatId" element={<ChatRoom />} />
                </Routes>
            </Box>
        </Box>
    );
};

export default MainLayout;
