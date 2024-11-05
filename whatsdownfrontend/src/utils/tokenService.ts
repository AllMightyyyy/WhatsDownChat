export const getToken = (): string | null => localStorage.getItem('token');

export const setToken = (token: string): void => {
    localStorage.setItem('token', token);
};

export const removeToken = (): void => {
    localStorage.removeItem('token');
};

export const getRefreshToken = (): string | null => localStorage.getItem('refreshToken');

export const setRefreshToken = (refreshToken: string): void => {
    localStorage.setItem('refreshToken', refreshToken);
};

export const removeRefreshToken = (): void => {
    localStorage.removeItem('refreshToken');
};
