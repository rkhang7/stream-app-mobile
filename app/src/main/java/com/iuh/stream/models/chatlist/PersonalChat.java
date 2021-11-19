package com.iuh.stream.models.chatlist;

import com.iuh.stream.models.User;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalChat {
    private String _id;
    private List<User> users;
    private int unreadMessagesCount;
    private LastLine latestLine;
}
