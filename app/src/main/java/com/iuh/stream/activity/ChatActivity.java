package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.models.chat.Line;
import com.iuh.stream.models.chat.Message;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiPopup;


import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
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
    private String chatId;
    private LinearLayout nameLayout;
    private static final String CREATE_PERSONAL_CHAT_REQUEST = "create-personal-chat";
    private static final String CREATE_PERSONAL_CHAT_RESPONSE = "create-personal-chat-res";
    private LinearLayoutManager linearLayoutManager;
    private ImageButton scrollLastPositionBtn;
    private TextView newMessageTv;
    private ProgressBar progressBar;

    private static final int LEFT_ITEM = 1;
    private static final int RIGHT_ITEM = 2;



    private List<Message> messageList;
    private PersonalMessageAdapter personalMessageAdapter;
    private RecyclerView recyclerView;

    private int visibleThreshold = 2; // trigger just one item before the end
    private int lastVisibleItem, totalItemCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        addControls();
        addEvents();
    }

    private void addEvents() {
        avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hisId = user.get_id();
                viewInfo(hisId, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
            }
        });
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hisId = user.get_id();
                viewInfo(hisId, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
            }
        });
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

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = messageEt.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("content", content);
                    jsonObject.put("type", MyConstant.TYPE_TEXT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String senderId = mAuth.getCurrentUser().getUid();
                SocketClient.getInstance().emit(MyConstant.PRIVATE_MESSAGE, new Object[]{chatId, senderId, jsonObject});

                // reset message
                messageEt.setText("");

                // close keyboard
                closeKeyBoard();
            }
        });

        scrollLastPositionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        });

        newMessageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        });


    }

    private void viewInfo(String id, String accessToken) {
        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            viewInfo(id, accessToken);
                        }
                        else{
                            User user = response.body();
                            if (user != null) {
                                if(user.isDeleted()){
                                    CustomAlert.showToast(ChatActivity.this, CustomAlert.INFO, "Tài khoản đã bị xóa");
                                }
                                else{
                                    Intent intent = new Intent(ChatActivity.this, FriendProfileActivity.class);
                                    intent.putExtra(AddFriendActivity.USER_KEY, user);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        CustomAlert.showToast(ChatActivity.this, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
        nameLayout = findViewById(R.id.name_layout);
        scrollLastPositionBtn = findViewById(R.id.scroll_last_position_btn);
        newMessageTv = findViewById(R.id.new_message_tv);
        progressBar = findViewById(R.id.chat_pb);




        // emoji
        emojiPopup = EmojiPopup.Builder.fromRootView(findViewById(R.id.root_view))
                .build(messageEt);

        user = (User) getIntent().getSerializableExtra(MyConstant.USER_KEY);
        // set info
        updateStatusUser(user.get_id(), DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
        Picasso.get().load(user.getImageURL()).into(avatarIv);
        nameTv.setText(user.getLastName());mAuth  = FirebaseAuth.getInstance();
        messageList =  new ArrayList<>();

        recyclerView = findViewById(R.id.chat_rcv);
        personalMessageAdapter = new PersonalMessageAdapter(this, mAuth.getCurrentUser().getUid(), user.getImageURL());
        personalMessageAdapter.setData(messageList);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(personalMessageAdapter);



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                if(dy > 0){
//                    scrollLastPositionBtn.setVisibility(View.GONE);
//                }
//                else{
//                    scrollLastPositionBtn.setVisibility(View.VISIBLE);
//                }
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (totalItemCount <= (lastVisibleItem + visibleThreshold) ){
                    newMessageTv.setVisibility(View.GONE);
                    scrollLastPositionBtn.setVisibility(View.GONE);
                }
                else{
                    scrollLastPositionBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

            }
        });





        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        getChatId();

        SocketClient.getInstance().on("offline user", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateStatusUser(user.get_id(), DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
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
                        updateStatusUser(user.get_id(), DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
                    }
                });
            }
        });

        SocketClient.getInstance().on(MyConstant.MESSAGE_SENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
//                        JSONObject jsonObject = (JSONObject) args[0];
//                        // get line
//                       try {
//                           JSONObject lineJsonObject = jsonObject.getJSONObject("line");
//                           String content = lineJsonObject.getString("content");
//                           String type = lineJsonObject.getString("type");
//                           boolean received = jsonObject.getBoolean("received");
//
//                           Line line = null;
//                           String stringDate = lineJsonObject.getString("createdAt");
//                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                               long l = Instant.parse(stringDate)
//                                       .toEpochMilli();
//                               Date date = new Date(l);
//                               line = Line.builder().content(content).createdAt(date).type(type)
//                                       .received(received).build();
//                           }
//
//                           // get newMessageId;
//                           String newMessageId = jsonObject.getString("newMessageId");
//                           String sender = mAuth.getCurrentUser().getUid();
//                           List<Line> lineList = new ArrayList<>();
//                           lineList.add(line);
//
//                           Message message = new Message(lineList, sender, newMessageId);
//                           messageList.add(message);
//                           personalMessageAdapter.setData(messageList);
//                           recyclerView.setAdapter(personalMessageAdapter);
//
//                       } catch (Exception e) {
//                           e.printStackTrace();
//                       }
                       loadMessage(chatId, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN), RIGHT_ITEM);
                       Log.e("TAG", "right: " );
                   }
               });
            }
        });

        SocketClient.getInstance().on(MyConstant.PRIVATE_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        loadMessage(chatId, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN), LEFT_ITEM);
                        Log.e("TAG", "left: " );
//                       String chatId = (String) args[0];
//                       String currentUserId = (String) args[1];
//                       String newMessageId = (String) args[2];
//
//                        // get line
//                        try {
//                            JSONObject lineObject = (JSONObject) args[3];
//                            String content = lineObject.getString("content");
//                            String type = lineObject.getString("type");
//
//                            Line line = null;
//                            String stringDate = lineObject.getString("createdAt");
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                long l = Instant.parse(stringDate)
//                                        .toEpochMilli();
//                                Date date = new Date(l);
//                                line = Line.builder().content(content).createdAt(date).type(type).build();
//
//                            }
//                            List<Line> lineList = new ArrayList<>();
//                            lineList.add(line);
//                            Message message = new Message(lineList, currentUserId, newMessageId);
//                            messageList.add(message);
//                            personalMessageAdapter.setData(messageList);
//                        }catch (Exception e){
//                            CustomAlert.showToast(ChatActivity.this, CustomAlert.WARNING, e.getMessage());
//                        }


                    }
                });
            }
        });

        SocketClient.getInstance().on(MyConstant.READ_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                loadMessage(chatId, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN), RIGHT_ITEM);
            }
        });


    }

    private void getChatId() {
        String senderId = mAuth.getCurrentUser().getUid();
        String receiverId = user.get_id();

        JSONObject obj = new JSONObject();
        try {
            obj.put("senderId", senderId);
            obj.put("receiverId", receiverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SocketClient.getInstance().emit(CREATE_PERSONAL_CHAT_REQUEST, new String[]{senderId, receiverId});

        SocketClient.getInstance().on(CREATE_PERSONAL_CHAT_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String tempId = (String) args[0];
                chatId = tempId;
                loadMessage(chatId, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN), RIGHT_ITEM);
            }
        });
    }

    private void loadMessage(String id, String accessToken, int type) {
        RetrofitService.getInstance.getMessageById(id, accessToken)
              .enqueue(new Callback<List<Message>>() {
                  @Override
                  public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                      if(response.body() == null){
                          CustomAlert.showToast(ChatActivity.this, CustomAlert.INFO, "Không có tin nhắn");
                      }
                      else if(response.code() == 403){
                          Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                          loadMessage(id, accessToken, type);
                      }
                      else if(response.code() == 200){
                          messageList = response.body();

                          if(type == RIGHT_ITEM){
                              recyclerView.setVisibility(View.VISIBLE);
                              progressBar.setVisibility(View.GONE);
                              personalMessageAdapter.setData(messageList);
                              recyclerView.setAdapter(personalMessageAdapter);
                          }
                          else{
                              if(scrollLastPositionBtn.getVisibility() == View.VISIBLE){
                                  scrollLastPositionBtn.setVisibility(View.GONE);
                                  newMessageTv.setVisibility(View.VISIBLE);
                              }
                              personalMessageAdapter.setData(messageList);
                          }
                      }
                      else{
                          CustomAlert.showToast(ChatActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));
                      }
                  }

                  @Override
                  public void onFailure(Call<List<Message>> call, Throwable t) {
                      CustomAlert.showToast(ChatActivity.this, CustomAlert.WARNING, t.getMessage());
                  }
              });

    }

    private void updateStatusUser(String id, String accessToken) {
        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
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
