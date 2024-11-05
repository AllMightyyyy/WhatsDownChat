export interface Chat {
    id: number;
    isGroup: boolean;
    name?: string;
    members: User[];
    latestMessage?: string;
}

export interface CreateChatRequest {
    isGroup: boolean;
    name?: string;
    userIds: string[];
}
