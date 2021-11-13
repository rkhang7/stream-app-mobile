package com.iuh.stream.models.chat;

public class Reaction {
    private String senderId;
    private String emoji;

    public Reaction() {
    }

    public Reaction(String senderId, String emoji) {
        this.senderId = senderId;
        this.emoji = emoji;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    @Override
    public String toString() {
        return "Reaction{" +
                "senderId='" + senderId + '\'' +
                ", emoji='" + emoji + '\'' +
                '}';
    }
}
