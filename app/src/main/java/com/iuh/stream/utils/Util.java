package com.iuh.stream.utils;

import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.models.jwt.TokenResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Util {
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

}
