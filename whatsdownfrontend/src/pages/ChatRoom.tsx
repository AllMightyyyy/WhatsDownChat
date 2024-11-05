// src/pages/ChatRoom.tsx
import React, {
    useState,
    useEffect,
    useContext,
    FormEvent,
    useRef,
} from 'react';
import { useParams } from 'react-router-dom';
import client from '../services/socket';
import { Message, SendMessageRequest } from '../types/message';
import { AuthContext } from '../contexts/AuthContext';
import api from "../services/api";
import {IMessage} from "@stomp/stompjs";

interface RouteParams {
    chatId: string;
}

const ChatRoom: React.FC = () => {
    const { chatId } = useParams<keyof RouteParams>() as RouteParams;
    const numericChatId = parseInt(chatId, 10); // Parse chatId as a number
    const { auth } = useContext(AuthContext);
    const [messages, setMessages] = useState<Message[]>([]);
    const [newMessage, setNewMessage] = useState<string>('');
    const messagesEndRef = useRef<HTMLDivElement | null>(null);
    const [subscription, setSubscription] = useState<any>(null);

    useEffect(() => {
        if (!numericChatId) return;

        const fetchMessages = async () => {
            try {
                const response = await api.get(`/messages/${numericChatId}?page=0&size=50`);
                const fetchedMessages = response.data.content;
                setMessages(fetchedMessages);
            } catch (error) {
                console.error('Failed to fetch messages', error);
            }
        };
        fetchMessages();

        // Subscribe to the chat room when connected
        if (client.connected) {
            subscribeToChat(numericChatId);
        } else {
            client.onConnect = function (frame) {
                console.log('Connected: ' + frame);
                subscribeToChat(numericChatId);
            };
        }

        // Cleanup on unmount
        return () => {
            unsubscribeFromChat();
        };
    }, [numericChatId]);

    const subscribeToChat = (chatId: number) => {
        const sub = client.subscribe(`/topic/chat/${chatId}`, (message: IMessage) => {
            const receivedMessage: Message = JSON.parse(message.body);
            console.log('Received message:', receivedMessage);
            setMessages((prev) => [...prev, receivedMessage]);
        });
        setSubscription(sub);
    };

    const unsubscribeFromChat = () => {
        if (subscription) {
            subscription.unsubscribe();
        }
    };

    // Scroll to the latest message whenever the messages array updates
    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
        }
    }, [messages]);

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        if (!newMessage.trim()) return;

        const payload: SendMessageRequest = {
            chatId: numericChatId,
            content: newMessage,
        };

        try {
            await api.post('/messages', payload);
            setNewMessage('');
        } catch (error) {
            console.error('Failed to send message', error);
        }
    };

    return (
        <div>
            <h2>Chat Room</h2>
            <div style={{ height: '400px', overflowY: 'scroll' }}>
                {messages.map((msg) => (
                    <div key={msg.id}>
                        <strong>{msg.senderUsername}</strong>: {msg.content}
                        <em>{new Date(msg.timestamp).toLocaleTimeString()}</em>
                    </div>
                ))}
                <div ref={messagesEndRef} />
            </div>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    placeholder="Type your message..."
                />
                <button type="submit">Send</button>
            </form>
        </div>
    );
};

export default ChatRoom;
