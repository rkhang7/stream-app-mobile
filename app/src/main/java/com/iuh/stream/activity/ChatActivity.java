package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.iuh.stream.adapter.PersonalMessageAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.models.chat.Line;
import com.iuh.stream.models.chat.Message;
import com.iuh.stream.models.response.ImageUrlResponse;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.RealPathUtil;
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiPopup;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.ContentUriUtils;
import gun0912.tedimagepicker.builder.TedImagePicker;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    private List<Message> messageList;
    private PersonalMessageAdapter personalMessageAdapter;
    private RecyclerView recyclerView;
    private int visibleThreshold = 1; // trigger just one item before the end
    private int lastVisibleItem, totalItemCount;
    private ProgressDialog progressDialog;

    private boolean isActivityRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        isActivityRunning = true;
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
                if (!TextUtils.isEmpty(editable.toString())) {
                    sendBtn.setVisibility(View.VISIBLE);
                    imageBtn.setVisibility(View.GONE);
                    fileBtn.setVisibility(View.GONE);
                } else {
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
                    jsonObject.put("type", MyConstant.TEXT_TYPE);
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
                scrollLastPositionBtn.setVisibility(View.GONE);
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        });

        newMessageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        });

        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });


    }

    private void openFilePicker() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                String[] zipTypes = {"zip","rar"};
                String[] apkType = {"apk"};
                String[] jsonType = {"json"};
                String[] csvType = {"csv"};
                String[] htmlType = {"html"};
                String[] cssType = {"css"};

                FilePickerBuilder.getInstance()
                        .setMaxCount(10) //optional
                        .addFileSupport("ZIP", zipTypes)
                        .addFileSupport("APK", apkType)
                        .addFileSupport("JSON", jsonType)
                        .addFileSupport("CSV", csvType)
                        .addFileSupport("HTML", htmlType)
                        .addFileSupport("CSS", cssType)
                        .setActivityTheme(R.style.LibAppTheme) //optional
                        .pickFile(ChatActivity.this);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(ChatActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void viewInfo(String id, String accessToken) {
        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            viewInfo(id, accessToken);
                        } else {
                            User user = response.body();
                            if (user != null) {
                                if (user.isDeleted()) {
                                    CustomAlert.showToast(ChatActivity.this, CustomAlert.INFO, "Tài khoản đã bị xóa");
                                } else {
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
        if (view != null) {
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
                    progressDialog.show();
                    // update image
                    for (Uri uri: uriList){
                        String realPath = RealPathUtil.getRealPath(this, uri);
                        File file = new File(realPath);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        MultipartBody.Part mPartImage = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
                        uploadImage(mPartImage);

                    }

                    progressDialog.dismiss();
                });




    }

    private synchronized void uploadImage(MultipartBody.Part file) {
        RetrofitService.getInstance.uploadImageChat(file, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<ImageUrlResponse>() {
                    @Override
                    public void onResponse(Call<ImageUrlResponse> call, Response<ImageUrlResponse> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            uploadImage(file);
                        }
                        else if(response.code() == 400){
                            CustomAlert.showToast(ChatActivity.this, CustomAlert.WARNING, "Không thể tải ảnh lên");
                        }
                        else if(response.code() == 200){
                            ImageUrlResponse imageUrlResponse = response.body();
                            String imageURL = imageUrlResponse.getImageURL();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("content", imageURL);
                                jsonObject.put("type", MyConstant.IMAGE_TYPE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String senderId = mAuth.getCurrentUser().getUid();
                            SocketClient.getInstance().emit(MyConstant.PRIVATE_MESSAGE, new Object[]{chatId, senderId, jsonObject});

                        }
                        else {
                            CustomAlert.showToast(ChatActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageUrlResponse> call, Throwable t) {
                        CustomAlert.showToast(ChatActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));

                    }
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Vui lòng đợi");


        // emoji
        emojiPopup = EmojiPopup.Builder.fromRootView(findViewById(R.id.root_view))
                .build(messageEt);

        user = (User) getIntent().getSerializableExtra(MyConstant.USER_KEY);
        // set info
        updateStatusUser(user.get_id(), DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
        Picasso.get().load(user.getImageURL()).into(avatarIv);
        nameTv.setText(user.getLastName());
        mAuth = FirebaseAuth.getInstance();
        messageList = new ArrayList<>();

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
                if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    newMessageTv.setVisibility(View.GONE);
                    scrollLastPositionBtn.setVisibility(View.GONE);
                } else {
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


        // khi mình gửi tin nhắn, server trả về
        SocketClient.getInstance().on(MyConstant.MESSAGE_SENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        // get line
                        try {
                            JSONObject lineJsonObject = jsonObject.getJSONObject("line");
                            String content = lineJsonObject.getString("content");
                            String type = lineJsonObject.getString("type");

                            String newMessageId = jsonObject.getString("newMessageId");

                            Line newLine = null;
                            String stringDate = lineJsonObject.getString("createdAt");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                long l = Instant.parse(stringDate)
                                        .toEpochMilli();
                                Date date = new Date(l);
                                newLine = Line.builder().content(content).createdAt(date).type(type)
                                        .build();

                                // last message
                                int lengthMessageList = messageList.size();

                                if (lengthMessageList > 0) {
                                    Message lastMessage = messageList.get(lengthMessageList - 1);
                                    String myId = mAuth.getCurrentUser().getUid();
                                    if (!lastMessage.getSender().equals(mAuth.getCurrentUser().getUid())) {
                                        List<Line> lineList = new ArrayList<>();
                                        lineList.add(newLine);
                                        Message message = new Message(lineList, myId, null, newMessageId);
                                        messageList.add(message);
                                    } else {
                                        int lengthLineList = lastMessage.getLines().size();
                                        List<Line> lastLineList = lastMessage.getLines();
                                        Line lastLine = lastMessage.getLines().get(lengthLineList - 1);

                                        long duration = date.getTime() - lastLine.getCreatedAt().getTime();
                                        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                                        if (diffInMinutes >= 1) {
                                            List<Line> lineList = new ArrayList<>();
                                            lineList.add(newLine);
                                            Message message = new Message(lineList, myId, null, newMessageId);
                                            messageList.add(message);

                                        } else {
                                            lastLineList.add(newLine);
                                        }
                                    }
                                } else {
                                    List<Line> lineList = new ArrayList<>();
                                    lineList.add(newLine);
                                    Message message = new Message(lineList, mAuth.getCurrentUser().getUid(), null, newMessageId);
                                    messageList.add(message);
                                }

                            }
                            personalMessageAdapter.setData(messageList);
                            recyclerView.setAdapter(personalMessageAdapter);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        loadMessage(chatId, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN), RIGHT_ITEM);
//                        Log.e("TAG", "right: ");
                    }
                });
            }
        });

        // người khác nhắn tin, server trả về
        SocketClient.getInstance().on(MyConstant.PRIVATE_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String chatId = (String) args[0];
                        String currentUserId = (String) args[1];
                        String newMessageId = (String) args[2];

                        // get line
                        try {
                            JSONObject lineObject = (JSONObject) args[3];
                            String content = lineObject.getString("content");
                            String type = lineObject.getString("type");

                            Line newLine = null;
                            String stringDate = lineObject.getString("createdAt");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                long l = Instant.parse(stringDate)
                                        .toEpochMilli();
                                Date date = new Date(l);
                                newLine = Line.builder().content(content).createdAt(date).type(type).build();

                                // last message
                                int lengthMessageList = messageList.size();
                                if (lengthMessageList > 0) {
                                    Message lastMessage = messageList.get(lengthMessageList - 1);
                                    if (lastMessage.getSender().equals(mAuth.getCurrentUser().getUid())) {
                                        List<Line> lineList = new ArrayList<>();
                                        lineList.add(newLine);
                                        Message message = new Message(lineList, currentUserId, null, newMessageId);
                                        messageList.add(message);
                                    } else {
                                        int lengthLineList = lastMessage.getLines().size();
                                        List<Line> lastLineList = lastMessage.getLines();
                                        Line lastLine = lastMessage.getLines().get(lengthLineList - 1);

                                        long duration = date.getTime() - lastLine.getCreatedAt().getTime();
                                        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                                        if (diffInMinutes >= 1) {
                                            List<Line> lineList = new ArrayList<>();
                                            lineList.add(newLine);
                                            Message message = new Message(lineList, currentUserId, null, newMessageId);
                                            messageList.add(message);

                                        } else {
                                            lastLineList.add(newLine);
                                        }
                                    }
                                } else {
                                    List<Line> lineList = new ArrayList<>();
                                    lineList.add(newLine);
                                    Message message = new Message(lineList, currentUserId, null, newMessageId);
                                    messageList.add(message);
                                }
                            }
                            if (scrollLastPositionBtn.getVisibility() == View.VISIBLE) {
                                scrollLastPositionBtn.setVisibility(View.GONE);
                                newMessageTv.setVisibility(View.VISIBLE);
                            }
                            personalMessageAdapter.setData(messageList);

                            // emit
                            if(isActivityRunning){
                                SocketClient.getInstance().emit(MyConstant.READ_MESSAGE, chatId, newLine.getId(), mAuth.getCurrentUser().getUid());
                            }


                        } catch (Exception e) {
                            CustomAlert.showToast(ChatActivity.this, CustomAlert.WARNING, e.getMessage());
                        }


//                        loadMessage(chatId, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN), LEFT_ITEM);
//                        Log.e("TAG", "left: " );
                    }
                });
            }
        });

        // người khác nhận tin nhắn
        SocketClient.getInstance().on(MyConstant.RECEIVE_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String chatId = (String) args[0];
                        String lineId = (String) args[1];
                        String senderId = (String) args[2];
                        // last message
                        int lengthMessageList = messageList.size();
                        if (lengthMessageList > 0) {
                            Message lastMessage = messageList.get(lengthMessageList - 1);
                            if (lastMessage.getSender().equals(mAuth.getCurrentUser().getUid())) {
                                List<Line> lineList = lastMessage.getLines();
                                Line lastLine = lineList.get(lineList.size() - 1);
                                lastLine.setReceived(true);
                                personalMessageAdapter.setData(messageList);
                            }
                        }
                    }
                });
            }
        });

        // người khác đọc tin nhắn
        SocketClient.getInstance().on(MyConstant.READ_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String chatId = (String) args[0];
                        String lineId = (String) args[1];
                        String senderId = (String) args[2];

                        // last message
                        int lengthMessageList = messageList.size();

                        if (lengthMessageList > 0) {
                            Message lastMessage = messageList.get(lengthMessageList - 1);
                            if (!lastMessage.getSender().equals(senderId)) {
                                List<Line> lineList = lastMessage.getLines();
                                Line lastLine = lineList.get(lineList.size() - 1);
                                if (lastLine.getReadedUsers() == null) {
                                    lastLine.setReadedUsers(new ArrayList<>());
                                }
                                lastLine.getReadedUsers().add(senderId);
                                personalMessageAdapter.setData(messageList);
                            }
                        }
                    }
                });
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
                loadMessage(chatId, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
            }
        });
    }

    private void loadMessage(String id, String accessToken) {
        RetrofitService.getInstance.getMessageById(id, accessToken)
                .enqueue(new Callback<List<Message>>() {
                    @Override
                    public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                        if (response.body() == null) {
                            CustomAlert.showToast(ChatActivity.this, CustomAlert.INFO, "Không có tin nhắn");
                        } else if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            loadMessage(id, accessToken);
                        } else if (response.code() == 200) {
                            messageList = response.body();

                            // emit read message
                            // last message
                            int lengthMessageList = messageList.size();
                            if (lengthMessageList > 0) {
                                Message lastMessage = messageList.get(lengthMessageList - 1);
                                if (!lastMessage.getSender().equals(mAuth.getCurrentUser().getUid())) {
                                    int lengthLineList = lastMessage.getLines().size();
                                    Line lastLine = lastMessage.getLines().get(lengthLineList - 1);
                                    SocketClient.getInstance().emit(MyConstant.READ_MESSAGE, chatId, lastLine.getId(), mAuth.getCurrentUser().getUid());
                                }
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            personalMessageAdapter.setData(messageList);
                            recyclerView.setAdapter(personalMessageAdapter);


                        } else {
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
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            updateStatusUser(id, accessToken);
                        } else {
                            User userUpdated = response.body();
                            if (user != null) {
                                if (!userUpdated.isOnline()) {
                                    offlineIv.setVisibility(View.VISIBLE);
                                    onlineIv.setVisibility(View.GONE);
                                    activeTv.setText("Hoạt động từ " + getDifferentTime(userUpdated.getLastOnline()));
                                } else {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<Uri> docPaths = new ArrayList<>();
                    docPaths.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));

                    for (Uri uri: docPaths){
                        String realPath = RealPathUtil.getRealPath(this, uri);
                        File file = new File(realPath);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        MultipartBody.Part mPartImage = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                        uploadFile(mPartImage);

                    }
                    
                }
                break;
        }
    }

    private void uploadFile(MultipartBody.Part file) {
        RetrofitService.getInstance.uploadFileChat(file, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            uploadImage(file);
                        }
                        else if(response.code() == 200){
                            String nameFile = response.body();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("content", nameFile);
                                jsonObject.put("type", MyConstant.FILE_TYPE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String senderId = mAuth.getCurrentUser().getUid();
                            SocketClient.getInstance().emit(MyConstant.PRIVATE_MESSAGE, new Object[]{chatId, senderId, jsonObject});
                        }
                        else {
                            CustomAlert.showToast(ChatActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRunning = false;
    }
}