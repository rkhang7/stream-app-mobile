package com.iuh.stream.api;

import com.iuh.stream.models.User;

import java.util.List;

public interface UserListAsyncResponse {
    void processFinnish(List<User> friendArrayList);
}
