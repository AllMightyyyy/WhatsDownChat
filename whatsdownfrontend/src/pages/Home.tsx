import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';

const Home: React.FC = () => {
    const { auth, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    if (auth.isAuthenticated) {
        return (
            <div>
                <h1>Welcome Back!</h1>
                <p>You are already logged in. Go to your dashboard to start chatting.</p>
                <button onClick={() => navigate('/dashboard')}>Go to Dashboard</button>
                <button onClick={handleLogout} style={{ marginLeft: '10px' }}>Logout</button>
            </div>
        );
    }

    return (
        <div>
            <h1>Welcome to the Chat App</h1>
            <p>Connect with friends and family in real-time.</p>
            <Link to="/login">
                <button>Login</button>
            </Link>
            <Link to="/register">
                <button>Sign Up</button>
            </Link>
        </div>
    );
};

export default Home;
