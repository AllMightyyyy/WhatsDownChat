import axios from 'axios';
import { toast } from 'react-toastify';
import { getToken, getRefreshToken, setToken, setRefreshToken, removeToken, removeRefreshToken } from '../utils/tokenService';

const api = axios.create({
    baseURL: 'http://localhost:8081/api',
});

// Add a request interceptor
api.interceptors.request.use(
    (config) => {
        const token = getToken();

        // Skip Authorization header for unauthenticated routes
        const isAuthRoute = config.url?.includes('/auth/register') || config.url?.includes('/auth/login');

        if (token && !isAuthRoute) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

// response interceptor for token refresh
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const refreshToken = getRefreshToken();
                const response = await api.post('/auth/refreshtoken', { refreshToken });
                setToken(response.data.accessToken);
                setRefreshToken(response.data.refreshToken);
                originalRequest.headers['Authorization'] = `Bearer ${response.data.accessToken}`;
                return api(originalRequest);
            } catch (err) {
                removeToken();
                removeRefreshToken();
                toast.error('Session expired. Please log in again.');
                window.location.href = '/login';
                return Promise.reject(err);
            }
        }
        toast.error(error.response?.data?.message || 'An error occurred');
        return Promise.reject(error);
    }
);

export default api;
