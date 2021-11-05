package com.iuh.stream.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.models.jwt.TokenResponse;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Util {
    private static FirebaseAuth mAuth;
    public static void refreshToken(String refreshToken){
        RetrofitService.getInstance.refreshToken(refreshToken).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                TokenResponse tokenResponse = response.body();
                if(tokenResponse != null){
                    String refreshToken = tokenResponse.getAccessToken();
                    DataLocalManager.putStringValue(Constants.ACCESS_TOKEN, refreshToken);
                }
            }
            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {

            }
        });
    }

    public static Socket getSocket(){
        mAuth = FirebaseAuth.getInstance();
        Socket socket = null;
        try {
            IO.Options mOptions = new IO.Options();
            mOptions.query = "uid=" + mAuth.getCurrentUser().getUid();
            socket = IO.socket(Constants.BASE_URL, mOptions);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return socket;
    }

}
