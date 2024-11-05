// src/pages/Login.tsx
import React from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import { TextField, Button, Container, Typography, Box } from '@mui/material';
import api from '../services/api';
import { LoginResponse } from '../types/auth';
import { toast } from 'react-toastify';

interface LoginFormValues {
    email: string;
    password: string;
}

const Login: React.FC = () => {
    const { login } = useAuth();
    const navigate = useNavigate();

    const initialValues: LoginFormValues = { email: '', password: '' };

    const validationSchema = Yup.object({
        email: Yup.string().email('Invalid email').required('Required'),
        password: Yup.string().min(6, 'Minimum 6 characters').required('Required'),
    });

    const handleSubmit = async (
        values: LoginFormValues,
        { setSubmitting, setErrors }: any
    ) => {
        try {
            const response = await api.post<LoginResponse>('/auth/login', values);
            const { token } = response.data;

            login(token, response.data);
            toast.success('Logged in successfully');
            navigate('/dashboard');
        } catch (error: any) {
            console.error('Login failed', error);
            setErrors({ email: 'Invalid credentials' });
            toast.error('Login failed');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <Container maxWidth="sm" sx={{ mt: 8 }}>
            <Typography variant="h4" component="h1" gutterBottom align="center">
                Login
            </Typography>
            <Box sx={{ mt: 4 }}>
                <Formik
                    initialValues={initialValues}
                    validationSchema={validationSchema}
                    onSubmit={handleSubmit}
                >
                    {({ isSubmitting, touched, errors }) => (
                        <Form>
                            <Field
                                as={TextField}
                                name="email"
                                label="Email"
                                fullWidth
                                margin="normal"
                                error={touched.email && Boolean(errors.email)}
                                helperText={<ErrorMessage name="email" />}
                            />
                            <Field
                                as={TextField}
                                name="password"
                                label="Password"
                                type="password"
                                fullWidth
                                margin="normal"
                                error={touched.password && Boolean(errors.password)}
                                helperText={<ErrorMessage name="password" />}
                            />
                            <Button
                                type="submit"
                                variant="contained"
                                color="primary"
                                disabled={isSubmitting}
                                fullWidth
                                sx={{ mt: 2 }}
                            >
                                {isSubmitting ? 'Logging in...' : 'Login'}
                            </Button>
                        </Form>
                    )}
                </Formik>
                <Typography variant="body2" align="center" sx={{ mt: 2 }}>
                    Don't have an account? <Link to="/register">Register here</Link>
                </Typography>
            </Box>
        </Container>
    );
};

export default Login;
