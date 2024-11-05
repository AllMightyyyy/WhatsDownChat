export interface User {
    id: number;
    username: string;
    email: string;
    avatarUrl?: string;
}

export interface UpdateUserRequest {
    username?: string;
    email?: string;
    avatar?: File;
}

