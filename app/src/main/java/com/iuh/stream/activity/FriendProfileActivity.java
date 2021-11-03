package com.iuh.stream.activity;


import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.iuh.stream.R;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.models.socket.AddFriendRequest;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendProfileActivity extends AppCompatActivity {
    private User user, currentUser;
    private Socket mSocket;
    // views
    private CircleImageView avtIv;
    private TextView nameTv, genderTv, phoneNumberTv, emailTv,dobTv;
    private FlexboxLayout emailLayout, phoneNumberLayout;
    private Button friendRequestBtn,cancelFriendRequestBtn;
    private static final String EVENT_REQUEST = "add-friend";
    private static final String EVENT_RESPONSE = "add-friend-res";

    // firebase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        addControls();
        addEvents();
    }

    private void addEvents() {
        friendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send event
                String senderId = mAuth.getCurrentUser().getUid();
                String receiverId = user.get_id();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("senderID", senderId);
                    jsonObject.put("receiverID", receiverId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //
                mSocket.emit(EVENT_REQUEST, jsonObject);
            }
        });

        cancelFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String senderId = mAuth.getCurrentUser().getUid();
                String receiverId = user.get_id();
                final String OPTION = "friendInvitation";
                String accessToken = DataLocalManager.getStringValue(Constants.ACCESS_TOKEN);
                cancelFriendInvitation(senderId, receiverId, OPTION, accessToken);
            }
        });
    }
    private void cancelFriendInvitation(String senderId, String receiverId, String option, String accessToken) {
        RetrofitService.getInstance.deleteUserIDByOption(senderId, receiverId, option, accessToken)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            friendRequestBtn.setVisibility(View.VISIBLE);
                            cancelFriendRequestBtn.setVisibility(View.INVISIBLE);
                        } else if (response.code() == 500) {
                            CustomAlert.showToast(FriendProfileActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));
                        } else if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            cancelFriendInvitation(senderId, receiverId, option, accessToken);
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        CustomAlert.showToast(FriendProfileActivity.this, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }


    private void addControls() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = (User) getIntent().getSerializableExtra(AddFriendActivity.USER_KEY);

        // init views
        avtIv = findViewById(R.id.avt_iv);
        Glide.with(this).load(user.getImageURL()).into(avtIv);

        nameTv = findViewById(R.id.name_tv);
        nameTv.setText(user.getFirstName() + " " +  user.getLastName());

        dobTv = findViewById(R.id.friend_info_dob_et);
        // datetime format
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        dobTv.setText(simpleDateFormat.format(user.getDateOfBirth()));

        genderTv = findViewById(R.id.friend_info_gender_tv);
        genderTv.setText(user.getGender());

        phoneNumberTv = findViewById(R.id.friend_info_phone_tv);
        emailTv = findViewById(R.id.friend_info_email_tv);
        phoneNumberLayout = findViewById(R.id.phone_layout);
        emailLayout = findViewById(R.id.email_layout);
        if(user.getPhoneNumber() != null){
            emailLayout.setVisibility(View.GONE);
            phoneNumberLayout.setVisibility(View.VISIBLE);
            phoneNumberTv.setText(user.getPhoneNumber());
        }

        else if(!user.getEmail().equals("null")){
            emailLayout.setVisibility(View.VISIBLE);
            phoneNumberLayout.setVisibility(View.GONE);
            emailTv.setText(user.getEmail());
        }

        friendRequestBtn = findViewById(R.id.friend_request_btn);
        cancelFriendRequestBtn = findViewById(R.id.cancel_friend_request_btn);

        // inti firebase
        mAuth = FirebaseAuth.getInstance();

        // init socket
        try {
            IO.Options mOptions = new IO.Options();
            mOptions.query = "uid=" + mAuth.getCurrentUser().getUid();
            mSocket = IO.socket(Constants.SOCKET_URL, mOptions);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();

        mSocket.on(EVENT_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String result;
                        try {
                            result = data.getString("result");
                        } catch (JSONException e) {
                            return;
                        }
                        if(result.equals("Success")){
                            friendRequestBtn.setVisibility(View.INVISIBLE);
                            cancelFriendRequestBtn.setVisibility(View.VISIBLE);
                        }
                        else {
                            CustomAlert.showToast(FriendProfileActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));
                        }
                    }
                });
            }
        });


        updateStatusFriendRequest();

    }

    private void updateStatusFriendRequest() {
        RetrofitService.getInstance.getMeInfo(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(Constants.REFRESH_TOKEN);
                            updateStatusFriendRequest();
                        }
                        else{
                            currentUser = response.body();
                            List<String> list = currentUser.getFriendInvitations();
                            if(list.contains(user.get_id())){
                                friendRequestBtn.setVisibility(View.INVISIBLE);
                                cancelFriendRequestBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        CustomAlert.showToast(FriendProfileActivity.this, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}