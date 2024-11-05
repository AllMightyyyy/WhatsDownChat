import React, { useState, ChangeEvent } from 'react';
import { UserSearchResult } from '../types/userSearch';
import api from "../services/api";

const UserSearch: React.FC = () => {
    const [query, setQuery] = useState<string>('');
    const [results, setResults] = useState<UserSearchResult[]>([]);

    const handleChange = async (e: ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setQuery(value);
        if (value.length > 2) {
            try {
                const response = await api.get<UserSearchResult[]>(`/api/users?search=${value}`);
                setResults(response.data);
            } catch (error) {
                console.error('Search failed', error);
            }
        } else {
            setResults([]);
        }
    };

    return (
        <div>
            <input
                type="text"
                value={query}
                onChange={handleChange}
                placeholder="Search users..."
            />
            <ul>
                {results.map(user => (
                    <li key={user.id}>
                        <img src={user.avatarUrl} alt={user.username} width={30} />
                        {user.username} ({user.email})
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default UserSearch;
