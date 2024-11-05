import { User } from './user';

interface Message {
    content: string;
    isRead: boolean;
    timestamp: string;
}

export interface Chat extends Omit<ChatAPIResponse, 'userIds'> {
    members: User[];
}

export interface ChatAPIResponse {
    id: number;
    isGroup: boolean;
    name?: string | null;
    userIds: number[];
    latestMessage?: Message;
}

export interface CreateChatRequest {
    isGroup: boolean;
    name?: string;
    userIds: string[];
}
