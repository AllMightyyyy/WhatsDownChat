export type Role = 'admin' | 'moderator' | 'user';

export interface UserWithRole {
    id: string;
    username: string;
    email: string;
    role: Role;
}

export interface UpdateUserRoleRequest {
    role: Role;
}
