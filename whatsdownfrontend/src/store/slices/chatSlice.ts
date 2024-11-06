// src/slices/chatSlice.ts
import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Chat } from '../../types/chat';

interface ChatState {
    chats: Chat[];
    selectedChatId: number | null;
}

const initialState: ChatState = {
    chats: [],
    selectedChatId: null,
};

const chatSlice = createSlice({
    name: 'chat',
    initialState,
    reducers: {
        setChats: (state, action: PayloadAction<Chat[]>) => {
            state.chats = action.payload;
        },
        addChat: (state, action: PayloadAction<Chat>) => {
            state.chats.unshift(action.payload);
        },
        selectChat: (state, action: PayloadAction<number>) => {
            state.selectedChatId = action.payload;
        },
    },
});

export const { setChats, addChat, selectChat } = chatSlice.actions;
export default chatSlice.reducer;
