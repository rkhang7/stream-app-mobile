package com.iuh.stream.utils;

import com.google.firebase.auth.FirebaseAuth;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketClient {
    private static FirebaseAuth mAuth;
    private static Socket mSocket;
    private static void initSocket() {
        mAuth = FirebaseAuth.getInstance();
        IO.Options mOptions = new IO.Options();
        mOptions.query = "uid=" + mAuth.getCurrentUser().getUid();
        try {
            mSocket = IO.socket(MyConstant.BASE_URL, mOptions);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static Socket getInstance() {
        if (mSocket != null) {
            return mSocket;
        } else {
            initSocket();
            return mSocket;
        }
    }
}
