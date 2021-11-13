package com.iuh.stream.models.chat;

import com.iuh.stream.models.User;

import java.util.List;

public class PersonalChat {
    private List<User> users;
    private List<Message> messages;
    public PersonalChat() {
    }

    public PersonalChat(List<User> users, List<Message> messages) {
        this.users = users;
        this.messages = messages;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "PersonalChat{" +
                "users=" + users +
                ", messages=" + messages +
                '}';
    }
}
