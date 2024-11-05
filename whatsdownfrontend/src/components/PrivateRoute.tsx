// src/components/PrivateRoute.tsx
import React, { useEffect } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

interface PrivateRouteProps {
    children: JSX.Element;
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
    const { user, loading } = useAuth();
    const location = useLocation();

    useEffect(() => {
        if (!loading && !user) {
            console.log("User is not logged in, redirecting to login.");
        }
    }, [loading, user]);

    if (loading) {
        return <div>Loading...</div>;
    }

    console.log("USEEEEEEEEER : ", user);

    return user ? children : <Navigate to="/login" replace state={{ from: location }} />;
};

export default PrivateRoute;
