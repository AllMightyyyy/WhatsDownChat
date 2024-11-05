// src/pages/Roles.tsx
import React, { useState, useEffect } from 'react';
import { UserWithRole, UpdateUserRoleRequest, Role } from '../types/roles';
import api from "../services/api";
import { useAuth } from '../contexts/AuthContext';
import {
    Box,
    Typography,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Select,
    MenuItem,
    Paper,
    CircularProgress
} from '@mui/material';
import { toast } from 'react-toastify';

const Roles: React.FC = () => {
    const { token } = useAuth();
    const [users, setUsers] = useState<UserWithRole[]>([]);
    const [loading, setLoading] = useState<boolean>(false);

    useEffect(() => {
        const fetchUsers = async () => {
            setLoading(true);
            try {
                const response = await api.get<UserWithRole[]>('/users', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setUsers(response.data);
            } catch (error) {
                console.error('Failed to fetch users', error);
                toast.error('Failed to load users');
            } finally {
                setLoading(false);
            }
        };
        fetchUsers();
    }, [token]);

    const handleRoleChange = async (userId: string, newRole: Role) => {
        const payload: UpdateUserRoleRequest = { role: newRole };
        try {
            await api.put(`/users/${userId}/roles`, payload, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setUsers(prevUsers =>
                prevUsers.map(user =>
                    user.id === userId ? { ...user, role: newRole } : user
                )
            );
            toast.success('Role updated successfully');
        } catch (error) {
            console.error('Failed to update role', error);
            toast.error('Failed to update role');
        }
    };

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ p: 4 }}>
            <Typography variant="h4" gutterBottom>
                User Roles Management
            </Typography>
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Username</TableCell>
                            <TableCell>Email</TableCell>
                            <TableCell>Role</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {users.map(user => (
                            <TableRow key={user.id}>
                                <TableCell>{user.username}</TableCell>
                                <TableCell>{user.email}</TableCell>
                                <TableCell>
                                    <Select
                                        value={user.role}
                                        onChange={(e) => handleRoleChange(user.id, e.target.value as Role)}
                                        fullWidth
                                    >
                                        <MenuItem value="admin">Admin</MenuItem>
                                        <MenuItem value="moderator">Moderator</MenuItem>
                                        <MenuItem value="user">User</MenuItem>
                                    </Select>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default Roles;
