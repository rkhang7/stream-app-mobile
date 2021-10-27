package com.iuh.stream.api;

import com.iuh.stream.models.jwt.IdToken;
import com.iuh.stream.models.jwt.Token;
import com.iuh.stream.models.User;
import com.iuh.stream.models.jwt.TokenResponse;
import com.iuh.stream.models.responce.UpdateUserResponse;
import com.iuh.stream.utils.Constants;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitService {

    RetrofitService getInstance = new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RetrofitService.class);

    @POST("users/{uid}")
    Call<User> saveUser(@Body User user, @Path("uid") String uid);

    @GET("users/phone/{phoneNumber}")
    Call<User> getUserByPhoneNumber(@Path("phoneNumber") String phoneNumber, @Header("Authorization") String accessToken);

    @GET("users/email/{email}")
    Call<User> getUserByEmail(@Path("email") String email, @Header("Authorization") String accessToken);

    @POST("auth/idtoken")
    Call<Token> getToken(@Body IdToken idToken);

    @GET("users/me/info")
    Call<User> getMeInfo(@Header("Authorization") String accessToken);
    
    @POST("auth/token")
    @FormUrlEncoded
    Call<TokenResponse> refreshToken(@Field("token") String refreshToken);

    @PUT("/users/me/info")
    Call<UpdateUserResponse> updateUser(@Body User user, @Header("Authorization") String accessToken);

    @PUT("/users/me/info")
    @FormUrlEncoded
    Call<UpdateUserResponse> updateAvatar(@Field("image") String imageBase64, @Header("Authorization") String accessToken);

}
