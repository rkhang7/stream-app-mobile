package com.iuh.stream.models.chatlist;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatList {
    private List<Chats> chats;
    private int totalUnreadMessages;
}
