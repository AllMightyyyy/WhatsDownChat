// src/components/PublicRoute.tsx
import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

interface PublicRouteProps {
    children: JSX.Element;
}

const PublicRoute: React.FC<PublicRouteProps> = ({ children }) => {
    const { user } = useAuth();

    if (user) {
        return <Navigate to="/" replace />;
    }

    return children;
};

export default PublicRoute;
