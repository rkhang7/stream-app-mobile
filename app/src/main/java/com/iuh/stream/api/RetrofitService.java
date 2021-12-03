package com.iuh.stream.api;

import com.iuh.stream.models.chat.Message;
import com.iuh.stream.models.chat.PersonalChat;
import com.iuh.stream.models.chatlist.ChatList;
import com.iuh.stream.models.jwt.IdToken;
import com.iuh.stream.models.jwt.Token;
import com.iuh.stream.models.User;
import com.iuh.stream.models.jwt.TokenResponse;
import com.iuh.stream.models.response.FileSizeResponse;
import com.iuh.stream.models.response.ImageUrlResponse;
import com.iuh.stream.models.response.UpdateUserResponse;
import com.iuh.stream.utils.MyConstant;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    RetrofitService getInstance = new Retrofit.Builder()
            .baseUrl(MyConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RetrofitService.class);

    @POST("users/{uid}")
    Call<User> saveUser(@Body User user, @Path("uid") String uid);

    @GET("users/phone/{phoneNumber}")
    Call<User> getUserByPhoneNumber(@Path("phoneNumber") String phoneNumber, @Header("Authorization") String accessToken);

    @GET("users/email/{email}")
    Call<User> getUserByEmail(@Path("email") String email, @Header("Authorization") String accessToken);

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") String id, @Header("Authorization") String accessToken);


    @POST("auth/idtoken")
    Call<Token> getToken(@Body IdToken idToken);

    @GET("users/me/info")
    Call<User> getMeInfo(@Header("Authorization") String accessToken);

    @DELETE("users/me")
    Call<Void> deleteMe(@Header("Authorization") String accessToken);

    @POST("auth/token")
    @FormUrlEncoded
    Call<TokenResponse> refreshToken(@Field("token") String refreshToken);

    @PUT("/users/me/info")
    Call<UpdateUserResponse> updateUser(@Body User user, @Header("Authorization") String accessToken);

    @PUT("/users/me/info")
    @FormUrlEncoded
    Call<UpdateUserResponse> updateAvatar(@Field("image") String imageBase64, @Header("Authorization") String accessToken);

    @DELETE("/friends")
    Call<Void> deleteUserIDByOption(@Query("senderID") String senderID, @Query("receiverID") String
            receiverID, @Query("option") String option, @Header("Authorization") String accessToken);

    @POST("/friends/accept")
    @FormUrlEncoded
    Call<Void> acceptFriendRequest(@Field("receiverID") String receiverID, @Header("Authorization") String accessToken);

    @POST("/friends/addFriend")
    @FormUrlEncoded
    Call<Void> addFriendRequest(@Field("receiverID") String receiverID, @Header("Authorization") String accessToken);


    @GET("/chats/{id}")
    Call<PersonalChat> getPersonalChatById(@Path("id") String id, @Header("Authorization") String accessToken);

    @GET("/chats/{id}/messages")
    Call<List<Message>> getMessageById( @Path("id") String chatId, @Query("page") int page, @Header("Authorization") String accessToken);

    @GET("/chats")
    Call<ChatList> getChatList(@Header("Authorization") String accessToken);

    @DELETE("/chats/{id}")
    Call<Void> deleteChatById(@Path("id") String chatId, @Header("Authorization") String accessToken);

    @Multipart
    @POST("/files/uploadImage")
    Call<ImageUrlResponse> uploadImageChat(@Part MultipartBody.Part image , @Header("Authorization") String accessToken);

    @Multipart
    @POST("/files/upload")
    Call<String> uploadFileChat(@Part MultipartBody.Part file , @Header("Authorization") String accessToken);

    @GET("/files/size/{name}")
    Call<FileSizeResponse> getFileSizeByName(@Path("name") String name, @Header("Authorization") String accessToken);
}
