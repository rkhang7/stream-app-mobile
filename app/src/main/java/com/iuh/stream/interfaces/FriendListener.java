package com.iuh.stream.interfaces;

import com.iuh.stream.models.User;

import java.util.List;

public interface FriendListener {
    void onFriendChange(List<String> list);
}
