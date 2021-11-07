package com.iuh.stream.utils;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.models.User;
import com.iuh.stream.models.jwt.TokenResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class Util {
    public static void refreshToken(String refreshToken){
        RetrofitService.getInstance.refreshToken(refreshToken).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(@NonNull Call<TokenResponse> call, @NonNull Response<TokenResponse> response) {
                TokenResponse tokenResponse = response.body();
                if(tokenResponse != null){
                    String refreshToken = tokenResponse.getAccessToken();
                    DataLocalManager.putStringValue(Constants.ACCESS_TOKEN, refreshToken);
                }
            }
            @Override
            public void onFailure(@NonNull Call<TokenResponse> call, @NonNull Throwable t) {

            }
        });
    }



    // Function to sort person list name by alphabet
    public static List<User> sortListFriend(List<User> friendArrayList){
        Collections.sort(friendArrayList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getFirstName().compareTo(o2.getFirstName());
            }
        });

        return friendArrayList;
    }

}
