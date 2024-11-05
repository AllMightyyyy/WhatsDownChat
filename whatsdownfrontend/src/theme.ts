// src/theme.ts
import { createTheme } from '@mui/material/styles';

const theme = createTheme({
    palette: {
        primary: {
            main: '#25D366', // WhatsApp Green
        },
        secondary: {
            main: '#075E54', // Darker green for headers
        },
        background: {
            default: '#ECE5DD', // Chat background
            paper: '#FFFFFF', // Message bubbles
        },
        text: {
            primary: '#000000',
            secondary: '#555555',
        },
    },
    typography: {
        fontFamily: 'Roboto, sans-serif',
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    textTransform: 'none',
                },
            },
        },
        MuiTextField: {
            styleOverrides: {
                root: {
                    backgroundColor: '#FFFFFF',
                    borderRadius: 8,
                },
            },
        },
    },
});

export default theme;
