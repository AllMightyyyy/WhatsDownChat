import React, { useState, FormEvent } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { RegisterRequest } from '../types/auth';
import { Container, TextField, Button, Typography } from '@mui/material';
import api from '../services/api';

const Register: React.FC = () => {
    const [username, setUsername] = useState<string>('');
    const [email, setEmail] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const navigate = useNavigate();

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        const payload: RegisterRequest = { username, email, password };

        console.log('Register form submitted:', payload);

        try {
            const response = await api.post('/auth/register', payload);
            console.log('Registration successful:', response.data);
            navigate('/login');
        } catch (error) {
            console.error('Registration failed:', error);
        }
    };

    return (
        <Container maxWidth="sm">
            <Typography variant="h4" component="h1" gutterBottom>
                Register
            </Typography>
            <form onSubmit={handleSubmit}>
                <TextField
                    label="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    fullWidth
                    margin="normal"
                />
                <Button type="submit" variant="contained" color="primary" fullWidth>
                    Register
                </Button>
            </form>
            <Typography variant="body2" align="center" style={{ marginTop: '1rem' }}>
                Already have an account? <Link to="/login">Login here</Link>
            </Typography>
        </Container>
    );
};

export default Register;
