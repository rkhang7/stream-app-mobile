package com.iuh.stream.models.chat;

import com.iuh.stream.models.User;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalChat {
    private String _id;
    private List<User> users;
    private int unreadMessagesCount;
    private List<Message> messages;
}
