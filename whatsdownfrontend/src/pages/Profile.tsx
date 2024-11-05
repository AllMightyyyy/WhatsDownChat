import React, { useState, useEffect, useContext, FormEvent } from 'react';
import api from '../services/api';
import { AuthContext } from '../contexts/AuthContext';
import { User, UpdateUserRequest } from '../types/user';

const Profile: React.FC = () => {
    const { auth } = useContext(AuthContext);
    const [user, setUser] = useState<User | null>(null);
    const [username, setUsername] = useState<string>('');
    const [email, setEmail] = useState<string>('');
    const [avatar, setAvatar] = useState<File | null>(null);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await api.get<User>(`/api/users/${auth.token}`);
                setUser(response.data);
                setUsername(response.data.username);
                setEmail(response.data.email);
            } catch (error) {
                console.error('Failed to fetch user', error);
            }
        };
        fetchUser();
    }, [auth.token]);

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        const formData = new FormData();
        const payload: UpdateUserRequest = { username, email };
        Object.entries(payload).forEach(([key, value]) => {
            if (value) formData.append(key, value as string);
        });
        if (avatar) {
            formData.append('avatar', avatar);
        }

        try {
            await api.put(`/api/users/${user?.id}`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            // Optionally, refetch user data or update state
        } catch (error) {
            console.error('Failed to update profile', error);
        }
    };

    if (!user) return <div>Loading...</div>;

    return (
        <form onSubmit={handleSubmit}>
            <h2>Profile</h2>
            <div>
                <label>Username:</label>
                <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />
            </div>
            <div>
                <label>Email:</label>
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
            </div>
            <div>
                <label>Avatar:</label>
                <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => {
                        if (e.target.files) setAvatar(e.target.files[0]);
                    }}
                />
                {user.avatarUrl && <img src={user.avatarUrl} alt="Avatar" width={100} />}
            </div>
            <button type="submit">Update Profile</button>
        </form>
    );
};

export default Profile;
