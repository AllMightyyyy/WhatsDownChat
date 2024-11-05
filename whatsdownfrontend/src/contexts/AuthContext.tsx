// src/contexts/AuthContext.tsx
import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User } from "../types/user";
import {LoginResponse} from "../types/auth";

interface AuthContextType {
    user: User | null;
    token: string | null;
    loading: boolean;
    login: (token: string, apiResponse: LoginResponse) => void;
    logout: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    // Login function to save user and token on login
    const login = (authToken: string, apiResponse: LoginResponse) => {
        console.log("Logging in:", authToken, apiResponse);

        // Map LoginResponse to User type
        const currentUser: User = {
            id: apiResponse.id,
            username: apiResponse.username,
            email: apiResponse.email,
        };

        setToken(authToken);
        setUser(currentUser);

        localStorage.setItem('token', authToken);

        try {
            localStorage.setItem('user', JSON.stringify(currentUser));
        } catch (error) {
            console.error("Failed to save user data:", error);
        }
    };

    const logout = () => {
        setToken(null);
        setUser(null);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    };

    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        const storedUser = localStorage.getItem('user');
        console.log("AuthContext useEffect - Stored Token:", storedToken);
        console.log("AuthContext useEffect - Stored User:", storedUser);

        if (storedToken) {
            setToken(storedToken);
        }

        if (storedUser) {
            try {
                setUser(JSON.parse(storedUser)); // Parse user only if valid JSON
            } catch (error) {
                console.error("Error parsing stored user data:", error);
                localStorage.removeItem('user'); // Clear invalid user data
            }
        }

        setLoading(false); // Ensure this is called to stop loading
    }, []);

    if (loading) {
        return <div>Loading...</div>; // Display loading indicator while loading
    }

    return (
        <AuthContext.Provider value={{ user, token, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

// Hook to use auth context in components
export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};
