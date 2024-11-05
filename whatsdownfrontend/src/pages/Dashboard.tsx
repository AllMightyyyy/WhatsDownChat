import React, { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { Chat } from '../types/chat';
import { User } from '../types/user';
import { AuthContext } from '../contexts/AuthContext';
import FeedbackForm from '../components/FeedbackForm';
import api from "../services/api";

// Temporary Chat interface including `userIds`
interface ChatWithIds extends Omit<Chat, 'members'> {
    userIds: number[];
}

const Dashboard: React.FC = () => {
    const { auth } = useContext(AuthContext);
    const [chats, setChats] = useState<Chat[]>([]);
    const [chatsWithIds, setChatsWithIds] = useState<ChatWithIds[]>([]);
    const [users, setUsers] = useState<User[]>([]);
    const [isCreating, setIsCreating] = useState<boolean>(false);
    const [isGroup, setIsGroup] = useState<boolean>(false);
    const [name, setName] = useState<string>('');
    const [userIds, setUserIds] = useState<number[]>([]);

    // Fetch users only once on component mount
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

    // Fetch chats only once on component mount
    useEffect(() => {
        const fetchChats = async () => {
            try {
                const response = await api.get<ChatWithIds[]>('/chats');
                setChatsWithIds(response.data);
            } catch (error) {
                console.error('Failed to fetch chats', error);
            }
        };
        fetchChats();
    }, []);

    // Map `userIds` to `members` after both `chatsWithIds` and `users` are loaded
    useEffect(() => {
        if (users.length > 0 && chatsWithIds.length > 0) {
            const mappedChats = chatsWithIds.map(chat => ({
                ...chat,
                members: chat.userIds.map(id => users.find(user => user.id === id)).filter(Boolean) as User[],
            }));
            setChats(mappedChats);
        }
    }, [chatsWithIds, users]);  // This effect only runs when both are loaded

    const handleCreateChat = async (e: React.FormEvent) => {
        e.preventDefault();
        const payload = { isGroup, name, userIds };
        try {
            const response = await api.post<ChatWithIds>('/chats', payload);
            const newChat = {
                ...response.data,
                members: response.data.userIds.map(id => users.find(user => user.id === id)).filter(Boolean) as User[],
            };
            setChats([...chats, newChat]);
            setIsCreating(false);
            setUserIds([]);
        } catch (error) {
            console.error('Failed to create chat', error);
        }
    };

    const handleUserSelection = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const selectedIds = Array.from(e.target.selectedOptions, option => parseInt(option.value, 10));
        setUserIds(selectedIds);
    };

    const getUsernames = (members: User[] | undefined): string => {
        if (!members || members.length === 0) return 'No Members';
        return members.map(member => member.username).join(', ');
    };

    return (
        <div>
            <h2>Chats</h2>
            <button onClick={() => setIsCreating(!isCreating)}>
                {isCreating ? 'Cancel' : 'Create New Chat'}
            </button>
            {isCreating && (
                <form onSubmit={handleCreateChat}>
                    <div>
                        <label>
                            <input
                                type="checkbox"
                                checked={isGroup}
                                onChange={(e) => setIsGroup(e.target.checked)}
                            />
                            Create Group Chat
                        </label>
                    </div>
                    {isGroup && (
                        <div>
                            <label>Group Name:</label>
                            <input
                                type="text"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                required={isGroup}
                            />
                        </div>
                    )}
                    <div>
                        <label>Select Users:</label>
                        <select
                            multiple
                            value={userIds.map(String)}
                            onChange={handleUserSelection}
                            required
                        >
                            {users.map(user => (
                                <option key={user.id} value={user.id}>
                                    {user.username}
                                </option>
                            ))}
                        </select>
                    </div>
                    <button type="submit">Create Chat</button>
                </form>
            )}
            <ul>
                {chats.map(chat => (
                    <li key={chat.id}>
                        <Link to={`/chat/${chat.id}`}>
                            {chat.isGroup
                                ? chat.name || 'Unnamed Group Chat'
                                : getUsernames(chat.members)}
                            {chat.latestMessage && <p>{chat.latestMessage}</p>}
                        </Link>
                    </li>
                ))}
            </ul>
            <FeedbackForm />
        </div>
    );
};

export default Dashboard;
