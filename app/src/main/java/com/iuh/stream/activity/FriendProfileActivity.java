package com.iuh.stream.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.models.socket.AddFriendRequest;
import com.iuh.stream.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FriendProfileActivity extends AppCompatActivity {
    private User user;
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
                String currentUserId = mAuth.getCurrentUser().getUid();
                AddFriendRequest addFriendRequest = new AddFriendRequest(currentUserId, user.get_id());
                mSocket.emit(EVENT_REQUEST, addFriendRequest);
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
        URI uri = URI.create(Constants.BASE_URL);
        IO.Options options = IO.Options.builder()
                // ...
                .build();
        mSocket = IO.socket(uri, options);
        mSocket.connect();
        Log.e("TAG", "addControls: " + mSocket.connected() );
        mSocket.on(EVENT_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getParent().runOnUiThread(new Runnable() {
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
                            friendRequestBtn.setVisibility(View.GONE);
                            cancelFriendRequestBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });
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