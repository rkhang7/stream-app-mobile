package com.iuh.stream.utils;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.models.User;
import com.iuh.stream.models.jwt.TokenResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public static String getTime(Date lastOnline) {

        long duration = new Date().getTime() - lastOnline.getTime();

        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(lastOnline);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String minStr = min + "";
        if(min < 10){
            minStr = "0" + min;
        }
        String s = "";
        if (diffInDays == 0){
            s = hour + ":" + minStr ;
        }
        else if(diffInDays  == 1){
            s = "Hôm qua " + hour + ":" + minStr ;
        }

        else if(diffInDays > 1){
            String pattern = "dd/MM/yyyy HH:mm";
            DateFormat df = new SimpleDateFormat(pattern);
            String todayAsString = df.format(lastOnline);
            s = todayAsString;
        }

        return s;

    }

}
