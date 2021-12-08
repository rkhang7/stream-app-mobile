package com.iuh.stream.models.response;

import com.google.gson.annotations.SerializedName;
import com.iuh.stream.models.User;
import com.iuh.stream.models.chat.Message;
import com.iuh.stream.models.chatlist.Group;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupResponse {
    private List<User> users;
    private Group group;
    @SerializedName("_id")
    private String id;
    private List<Message> messages;
}
