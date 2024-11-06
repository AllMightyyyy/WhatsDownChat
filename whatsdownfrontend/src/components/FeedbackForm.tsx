// src/components/FeedbackForm.tsx
import React, { useState, FormEvent } from 'react';
import { Modal, Box, TextField, Button, Typography, IconButton } from '@mui/material';
import { FaTimes } from 'react-icons/fa';
import api from "../services/api";
import { toast } from 'react-toastify';

interface FeedbackFormProps {
    onClose: () => void;
}

const FeedbackForm: React.FC<FeedbackFormProps> = ({ onClose }) => {
    const [feedback, setFeedback] = useState<string>('');

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        try {
            await api.post('/feedback', { feedback }); // Adjusted endpoint
            setFeedback('');
            toast.success('Thank you for your feedback!');
            onClose();
        } catch (error) {
            console.error('Failed to submit feedback', error);
            toast.error('Failed to submit feedback');
        }
    };

    const style = {
        position: 'absolute' as 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        width: 400,
        bgcolor: 'background.paper',
        borderRadius: 2,
        boxShadow: 24,
        p: 4,
    };

    return (
        <Modal
            open={true}
            onClose={onClose}
            aria-labelledby="feedback-modal-title"
            aria-describedby="feedback-modal-description"
        >
            <Box sx={style}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography id="feedback-modal-title" variant="h6" component="h2">
                        Feedback
                    </Typography>
                    <IconButton onClick={onClose}>
                        <FaTimes />
                    </IconButton>
                </Box>
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
                    <Button type="submit" variant="contained" color="primary" fullWidth sx={{ mt: 2 }}>
                        Submit
                    </Button>
                </form>
            </Box>
        </Modal>
    );
};

export default FeedbackForm;
