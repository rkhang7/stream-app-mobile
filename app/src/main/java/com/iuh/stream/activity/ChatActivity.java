package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.iuh.stream.R;
import com.iuh.stream.adapter.FriendsAdapter;
import com.iuh.stream.adapter.PersonalMessageAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.models.User;
import com.iuh.stream.models.chat.Message;
import com.iuh.stream.models.chat.PersonalChat;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiPopup;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedimagepicker.builder.TedImagePicker;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    // view
    private EditText messageEt;
    private ImageButton emojiBtn, sendBtn, imageBtn, fileBtn, backBtn;
    private User user;
    private CircleImageView avatarIv;
    private TextView nameTv, activeTv;
    private ImageView onlineIv, offlineIv;
    private EmojiPopup emojiPopup;
    private FirebaseAuth mAuth;


    private List<Message> messageList;
    private PersonalMessageAdapter personalMessageAdapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        addControls();
        addEvents();
    }

    private void addEvents() {
        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!TextUtils.isEmpty(editable.toString())){
                    sendBtn.setVisibility(View.VISIBLE);
                    imageBtn.setVisibility(View.GONE);
                    fileBtn.setVisibility(View.GONE);
                }
                else{
                    sendBtn.setVisibility(View.GONE);
                    imageBtn.setVisibility(View.VISIBLE);
                    fileBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        emojiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.toggle();
            }
        });
    }

    private void openImagePicker() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openBottomPicker();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(ChatActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void openBottomPicker() {
        TedImagePicker.with(this)
                .title("Chọn hình ảnh")
                .startMultiImage(uriList -> {
                    Log.e("TAG", "openBottomPicker: " + uriList.toString() );
                });
    }

    private void addControls() {
        messageEt = findViewById(R.id.messageEt);
        emojiBtn = findViewById(R.id.emoji_btn);
        sendBtn = findViewById(R.id.sendBtn);
        imageBtn = findViewById(R.id.chat_image_btn);
        fileBtn = findViewById(R.id.chat_file_btn);
        backBtn = findViewById(R.id.back_btn);
        avatarIv = findViewById(R.id.toolbar_image);
        nameTv = findViewById(R.id.toolbar_name_tv);
        onlineIv = findViewById(R.id.toolbar_online_iv);
        offlineIv = findViewById(R.id.toolbar_offline_iv);
        activeTv = findViewById(R.id.toolbar_active_tv);




        // emoji
        emojiPopup = EmojiPopup.Builder.fromRootView(findViewById(R.id.root_view))
                .build(messageEt);

        user = (User) getIntent().getSerializableExtra(FriendsAdapter.USER);
        // set info
        updateStatusUser(user.get_id(), DataLocalManager.getStringValue(Constants.ACCESS_TOKEN));
        Picasso.get().load(user.getImageURL()).into(avatarIv);
        nameTv.setText(user.getLastName());mAuth  = FirebaseAuth.getInstance();
        messageList =  new ArrayList<>();

        recyclerView = findViewById(R.id.chat_rcv);
        personalMessageAdapter = new PersonalMessageAdapter(this, mAuth.getCurrentUser().getUid(), user.getImageURL());
        personalMessageAdapter.setData(messageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(personalMessageAdapter);

        RetrofitService.getInstance.getPersonalChatById("618f1f8e1b0cb62c9053d3a2", DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<PersonalChat>() {
                    @Override
                    public void onResponse(Call<PersonalChat> call, Response<PersonalChat> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            RetrofitService.getInstance.getPersonalChatById("618f1f8e1b0cb62c9053d3a2", DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                                    .enqueue(new Callback<PersonalChat>() {
                                        @Override
                                        public void onResponse(Call<PersonalChat> call, Response<PersonalChat> response) {
                                            PersonalChat personalChat = response.body();
                                            messageList = personalChat.getMessages();
                                            personalMessageAdapter.setData(messageList);
                                            recyclerView.setAdapter(personalMessageAdapter);
                                        }

                                        @Override
                                        public void onFailure(Call<PersonalChat> call, Throwable t) {

                                        }
                                    });
                        }
                        else if(response.code() == 200){
                            PersonalChat personalChat = response.body();
                            messageList = personalChat.getMessages();
                            personalMessageAdapter.setData(messageList);
                            recyclerView.setAdapter(personalMessageAdapter);
                        }
                    }


                    @Override
                    public void onFailure(Call<PersonalChat> call, Throwable t) {

                    }
                });

        SocketClient.getInstance().on("offline user", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateStatusUser(user.get_id(), DataLocalManager.getStringValue(Constants.ACCESS_TOKEN));
                    }
                });
            }
        });

        SocketClient.getInstance().on("online user", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateStatusUser(user.get_id(), DataLocalManager.getStringValue(Constants.ACCESS_TOKEN));
                    }
                });
            }
        });
    }

    private void updateStatusUser(String id, String accessToken) {
        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            updateStatusUser(id, accessToken);
                        }
                        else{
                            User userUpdated = response.body();
                            if(user != null){
                                if(!userUpdated.isOnline()){
                                    offlineIv.setVisibility(View.VISIBLE);
                                    onlineIv.setVisibility(View.GONE);
                                    activeTv.setText("Hoạt động từ " + getDifferentTime(userUpdated.getLastOnline()));
                                }
                                else{
                                    offlineIv.setVisibility(View.GONE);
                                    onlineIv.setVisibility(View.VISIBLE);
                                    activeTv.setText("Đang hoạt động");
                                }

                            }
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

                    }
                });
    }

    private String getDifferentTime(Date lastOnline) {

        long duration = new Date().getTime() - lastOnline.getTime();

        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
        String s = "";
        if (diffInSeconds < 60) {
            s = diffInSeconds + " giây trước";
        } else {
            if (diffInMinutes < 60) {
                s = diffInMinutes + " phút trước";
            } else {
                if (diffInHours < 24) {
                    s = diffInHours + " giờ trước";
                } else {
                    s = diffInDays + " ngày trước";
                }
            }
        }
        return s;

    }
}
