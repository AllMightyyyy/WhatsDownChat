import React, { useState, useEffect } from 'react';
import { UserWithRole, UpdateUserRoleRequest, Role } from '../types/roles';
import api from "../services/api";

const Roles: React.FC = () => {
    const [users, setUsers] = useState<UserWithRole[]>([]);

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await api.get<UserWithRole[]>('/api/users');
                setUsers(response.data);
            } catch (error) {
                console.error('Failed to fetch users', error);
            }
        };
        fetchUsers();
    }, []);

    const handleRoleChange = async (userId: string, newRole: Role) => {
        const payload: UpdateUserRoleRequest = { role: newRole };
        try {
            await api.put(`/api/users/${userId}/roles`, payload);
            setUsers(prevUsers =>
                prevUsers.map(user =>
                    user.id === userId ? { ...user, role: newRole } : user
                )
            );
        } catch (error) {
            console.error('Failed to update role', error);
        }
    };

    return (
        <div>
            <h2>User Roles Management</h2>
            <table>
                <thead>
                <tr>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {users.map(user => (
                    <tr key={user.id}>
                        <td>{user.username}</td>
                        <td>{user.email}</td>
                        <td>{user.role}</td>
                        <td>
                            <select
                                value={user.role}
                                onChange={(e) => handleRoleChange(user.id, e.target.value as Role)}
                            >
                                <option value="admin">Admin</option>
                                <option value="moderator">Moderator</option>
                                <option value="user">User</option>
                            </select>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default Roles;
