export interface Message {
    id: number;
    content: string;
    timestamp: string;
    isRead: boolean;
    chatId: number;
    senderId: number;
    senderUsername: string;
    attachment?: string | null;
}

export interface SendMessageRequest {
    chatId: number;
    content: string;
}

export interface MarkAsReadRequest {
    messageIds: string[];
}
