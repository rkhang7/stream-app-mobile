package com.iuh.stream.models.socket;

import java.io.Serializable;

public class AddFriendRequest implements Serializable {
    private String senderID;
    private String receiverID;

    public AddFriendRequest() {
    }

    public AddFriendRequest(String senderID, String receiverID) {
        this.senderID = senderID;
        this.receiverID = receiverID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    @Override
    public String toString() {
        return "AddFriendRequest{" +
                "senderID='" + senderID + '\'' +
                ", receiverID='" + receiverID + '\'' +
                '}';
    }
}
