import React, { createContext, useState, useEffect, ReactNode } from 'react';
import { getToken, setToken, removeToken, getRefreshToken, setRefreshToken, removeRefreshToken } from '../utils/tokenService';

interface AuthContextType {
    auth: {
        token: string | null;
        isAuthenticated: boolean;
    };
    login: (token: string, refreshToken: string) => void;
    logout: () => void;
}

export const AuthContext = createContext<AuthContextType>({
    auth: { token: null, isAuthenticated: false },
    login: () => {},
    logout: () => {},
});

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [auth, setAuth] = useState<{ token: string | null; isAuthenticated: boolean }>({
        token: getToken(),
        isAuthenticated: !!getToken(),
    });

    const login = (token: string, refreshToken: string) => {
        setToken(token);
        setRefreshToken(refreshToken);
        setAuth({ token, isAuthenticated: true });
    };

    const logout = () => {
        removeToken();
        removeRefreshToken();
        setAuth({ token: null, isAuthenticated: false });
    };

    return (
        <AuthContext.Provider value={{ auth, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
