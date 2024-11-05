/*
import React from 'react';
import { Chat } from '../types/chat';
import { Link } from 'react-router-dom';

interface ChatListProps {
    chats: Chat[];
}

const ChatList: React.FC<ChatListProps> = ({ chats }) => {
    return (
        <ul>
            {chats.map(chat => (
                <li key={chat.id}>
                    <Link to={`/chat/${chat.id}`}>
                        {chat.isGroup ? chat.name : chat.members.map(m => m.username).join(', ')}
                        {chat.latestMessage && <p>{chat.latestMessage.content}</p>}
                    </Link>
                </li>
            ))}
        </ul>
    );
};

export default ChatList;

 */
export {};
