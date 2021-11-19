package com.iuh.stream.api;

import com.iuh.stream.interfaces.ChatIdListener;
import com.iuh.stream.utils.SocketClient;

import io.socket.emitter.Emitter;

public class ChatUtil {
    public void getChatId(String senderId, String receiverId, ChatIdListener callback){
        SocketClient.getInstance().on("create-personal-chat-res", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String tempId = (String) args[0];
                callback.onGetChatId(tempId);
            }
        });
    }
}
