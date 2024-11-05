import React, { useState, FormEvent } from 'react';
import { TextField, Button, Container, Typography } from '@mui/material';
import api from "../services/api";

const FeedbackForm: React.FC = () => {
    const [feedback, setFeedback] = useState<string>('');

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        try {
            await api.post('/api/feedback', { feedback });
            setFeedback('');
            alert('Thank you for your feedback!');
        } catch (error) {
            console.error('Failed to submit feedback', error);
        }
    };

    return (
        <Container maxWidth="sm">
            <Typography variant="h5">Feedback</Typography>
            <form onSubmit={handleSubmit}>
                <TextField
                    label="Your Feedback"
                    multiline
                    rows={4}
                    value={feedback}
                    onChange={(e) => setFeedback(e.target.value)}
                    variant="outlined"
                    fullWidth
                    required
                />
                <Button type="submit" variant="contained" color="primary" style={{ marginTop: '1rem' }}>
                    Submit
                </Button>
            </form>
        </Container>
    );
};

export default FeedbackForm;
