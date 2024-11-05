// src/services/socket.ts
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getToken } from '../utils/tokenService';

const client = new Client({
    connectHeaders: {
        token: getToken() || '', // Use "token" header to match backend expectation
    },
    debug: (str) => console.log(str),
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    webSocketFactory: () => new SockJS('http://localhost:8081/ws'),
});

client.onConnect = (frame) => {
    console.log('Connected: ' + frame);
};

client.onStompError = (frame) => {
    console.error('Broker error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

client.activate();

export default client;
