package com.iuh.stream.interfaces;

import com.iuh.stream.models.User;
import com.iuh.stream.models.chatlist.PersonalChat;

import java.util.List;

public interface PersonalChatListAsyncResponse {
    void processFinnish(List<PersonalChat> friendArrayList);
}
