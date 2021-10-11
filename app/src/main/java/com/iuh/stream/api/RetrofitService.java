package com.iuh.stream.api;

import com.iuh.stream.models.User;
import com.iuh.stream.utils.Utils;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitService {
    RetrofitService getInstance = new Retrofit.Builder()
            .baseUrl(Utils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RetrofitService.class);

    @POST("users/{uid}")
    Call<User> saveUser(@Body User user, @Path("uid") String uid);

    @GET("users/phone/{phoneNumber}")
    Call<User> getUserByPhoneNumber(@Path("phoneNumber") String phoneNumber);

    @GET("users/email/{email}")
    Call<User> getUserByEmail(@Path("email") String email);

}
