package com.iuh.stream.models.chat;

import java.util.Date;
import java.util.List;

public class Message {
    private String sender;
    private List<Line> lines;
    private Date createdAt;

    public Message() {
    }

    public Message(String sender, List<Line> lines, Date createdAt) {
        this.sender = sender;
        this.lines = lines;
        this.createdAt = createdAt;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", lines=" + lines +
                ", createdAt=" + createdAt +
                '}';
    }
}
