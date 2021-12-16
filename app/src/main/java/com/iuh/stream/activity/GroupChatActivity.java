package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.iuh.stream.R;
import com.iuh.stream.adapter.GroupMessageAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.models.chat.Line;
import com.iuh.stream.models.chat.Message;
import com.iuh.stream.models.chatlist.Group;
import com.iuh.stream.models.response.ImageUrlResponse;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.RealPathUtil;
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;
import com.vanniktech.emoji.EmojiPopup;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import gun0912.tedimagepicker.builder.TedImagePicker;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupChatActivity extends AppCompatActivity {
    // view
    private EditText messageEt;
    private ImageButton emojiBtn, sendBtn, imageBtn, fileBtn, backBtn, optionBtn;
    private Group group;
    private TextView numberMemberTv, textingTv;
    public static TextView groupNameTv;
    private EmojiPopup emojiPopup;
    private FirebaseAuth mAuth;
    private String chatId, myId;
    private LinearLayout nameLayout;
    private static final String TEXTING_EVENT = "texting";
    private static final String STOP_TEXTING_EVENT = "stop-texting";
    private static final int FIRST_LOAD = 11;
    private static final int NEXT_PAGE_LOAD = 22;
    private LinearLayoutManager linearLayoutManager;
    private ImageButton scrollLastPositionBtn;
    private TextView newMessageTv;
    private ProgressBar progressBar, pagingPb;
    private List<Message> messageList;
    private GroupMessageAdapter groupMessageAdapter;
    private RecyclerView recyclerView;
    private final int visibleThreshold = 1; // trigger just one item before the end
    private int lastVisibleItem, totalItemCount;
    private ProgressDialog progressDialog;
    private int currentPage = 1;
    private List<User> memberList;
    private Bundle bundle;

    private boolean isActivityRunning = false;
    private boolean isLoading = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        isActivityRunning = true;
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
                if (!TextUtils.isEmpty(editable.toString())) {
                    sendBtn.setVisibility(View.VISIBLE);
                    imageBtn.setVisibility(View.GONE);
                    fileBtn.setVisibility(View.GONE);
                    SocketClient.getInstance().emit(TEXTING_EVENT, chatId, myId);
                } else {
                    sendBtn.setVisibility(View.GONE);
                    imageBtn.setVisibility(View.VISIBLE);
                    fileBtn.setVisibility(View.VISIBLE);
                    SocketClient.getInstance().emit(STOP_TEXTING_EVENT, chatId, myId);
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                SocketClient.getInstance().emit(STOP_TEXTING_EVENT, chatId, myId);
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

                SocketClient.getInstance().emit(MyConstant.PRIVATE_MESSAGE, chatId, myId, jsonObject);

                // reset message
                messageEt.setText("");

                // close keyboard
                closeKeyBoard();
            }
        });

        scrollLastPositionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMessageTv.setVisibility(View.GONE);
                scrollLastPositionBtn.setVisibility(View.GONE);
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        });

        newMessageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMessageTv.setVisibility(View.GONE);
                scrollLastPositionBtn.setVisibility(View.GONE);
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        });

        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupOptionActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }

    private void openFilePicker() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                String[] zipTypes = {"zip", "rar"};
                String[] mp3Type = {"mp3"};
                String[] apkType = {"apk"};
                String[] jsonType = {"json"};
                String[] csvType = {"csv"};
                String[] htmlType = {"html"};
                String[] cssType = {"css"};

                FilePickerBuilder.getInstance()
                        .setMaxCount(10) //optional
                        .addFileSupport("RAR", zipTypes)
                        .addFileSupport("MP3", mp3Type)
                        .addFileSupport("APK", apkType)
                        .addFileSupport("JSON", jsonType)
                        .addFileSupport("CSV", csvType)
                        .addFileSupport("HTML", htmlType)
                        .addFileSupport("CSS", cssType)
                        .setActivityTheme(R.style.LibAppTheme) //optional
                        .pickFile(GroupChatActivity.this);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(GroupChatActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
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
                Toast.makeText(GroupChatActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
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
                    for (Uri uri : uriList) {
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
                    public void onResponse(@NonNull Call<ImageUrlResponse> call, @NonNull Response<ImageUrlResponse> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            uploadImage(file);
                        } else if (response.code() == 400) {
                            CustomAlert.showToast(GroupChatActivity.this, CustomAlert.WARNING, "Không thể tải ảnh lên");
                        } else if (response.code() == 200) {
                            ImageUrlResponse imageUrlResponse = response.body();
                            String imageURL = imageUrlResponse.getImageURL();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("content", imageURL);
                                jsonObject.put("type", MyConstant.IMAGE_TYPE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            SocketClient.getInstance().emit(MyConstant.PRIVATE_MESSAGE, chatId, myId, jsonObject);
                            SocketClient.getInstance().emit(MyConstant.RENDER_IMAGE_REQUEST, chatId);

                        } else {
                            CustomAlert.showToast(GroupChatActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ImageUrlResponse> call, @NonNull Throwable t) {
                        CustomAlert.showToast(GroupChatActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));

                    }
                });
    }

    private void addControls() {
        pagingPb = findViewById(R.id.paging_pb);
        textingTv = findViewById(R.id.texting_tv);
        messageEt = findViewById(R.id.messageEt);
        emojiBtn = findViewById(R.id.emoji_btn);
        sendBtn = findViewById(R.id.sendBtn);
        imageBtn = findViewById(R.id.chat_image_btn);
        optionBtn = findViewById(R.id.toolbar_information_btn);
        fileBtn = findViewById(R.id.chat_file_btn);
        backBtn = findViewById(R.id.back_btn);
        groupNameTv = findViewById(R.id.group_name_tv);
        numberMemberTv = findViewById(R.id.number_member_tv);
        nameLayout = findViewById(R.id.name_layout);
        scrollLastPositionBtn = findViewById(R.id.scroll_last_position_btn);
        newMessageTv = findViewById(R.id.new_message_tv);
        progressBar = findViewById(R.id.chat_pb);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Vui lòng đợi");


        // emoji
        emojiPopup = EmojiPopup.Builder.fromRootView(findViewById(R.id.root_view))
                .build(messageEt);

        bundle = getIntent().getExtras();
        group = (Group) bundle.getSerializable(MyConstant.GROUP_KEY);
        // set info
        groupNameTv.setText(group.getName());

        // set number member
        int numberMember = bundle.getInt(MyConstant.NUMBER_MEMBER);
        numberMemberTv.setText(numberMember + " thành viên");

        // get chat id;
        chatId = bundle.getString(MyConstant.GROUP_CHAT_ID);

        // get list member
        memberList = (List<User>) bundle.getSerializable(MyConstant.LIST_MEMBER_KEY);


        mAuth = FirebaseAuth.getInstance();
        myId = mAuth.getCurrentUser().getUid();
        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.chat_rcv);
        groupMessageAdapter = new GroupMessageAdapter(this, myId, memberList, chatId);
        groupMessageAdapter.setData(messageList);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(groupMessageAdapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (totalItemCount <= (lastVisibleItem + visibleThreshold) && newMessageTv.getVisibility() == View.GONE) {
                    newMessageTv.setVisibility(View.GONE);
                    scrollLastPositionBtn.setVisibility(View.GONE);
                } else {
                    scrollLastPositionBtn.setVisibility(View.VISIBLE);
                }

//                 paging
                int firstCompletelyVisibleItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstCompletelyVisibleItemPosition == 0) {
                    if (isLoading) {
                        currentPage = currentPage + 1;
                        loadMessage(chatId, currentPage, NEXT_PAGE_LOAD);
                    }
                }


            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

            }
        });


        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        loadMessage(chatId, currentPage, FIRST_LOAD);



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
                            String lineId = lineJsonObject.getString("_id");

                            String newMessageId = jsonObject.getString("messageId");

                            Line newLine = null;
                            String stringDate = lineJsonObject.getString("createdAt");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                long l = Instant.parse(stringDate)
                                        .toEpochMilli();
                                Date date = new Date(l);
                                newLine = Line.builder().id(lineId).content(content).createdAt(date).type(type)
                                        .build();

                                // last message
                                int lengthMessageList = messageList.size();

                                if (lengthMessageList > 0) {
                                    Message lastMessage = messageList.get(lengthMessageList - 1);
                                    if (!lastMessage.getSender().equals(myId)) {
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
                                            groupMessageAdapter.notifyItemInserted(messageList.size());

                                        } else {
                                            lastLineList.add(newLine);
                                            groupMessageAdapter.notifyDataSetChanged();
                                        }
                                    }
                                } else {
                                    List<Line> lineList = new ArrayList<>();
                                    lineList.add(newLine);
                                    Message message = new Message(lineList, myId, null, newMessageId);
                                    messageList.add(message);
                                    groupMessageAdapter.notifyItemInserted(messageList.size());
                                }

                            }

//                            personalMessageAdapter.setData(messageList);
                            recyclerView.setAdapter(groupMessageAdapter);

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
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {

                        String chatId = (String) args[0];
                        String newMessageId = (String) args[1];
                        String currentUserId = (String) args[2];


                        // get line
                        try {
                            JSONObject lineObject = (JSONObject) args[3];
                            String content = lineObject.getString("content");
                            String type = lineObject.getString("type");
                            String lineId = lineObject.getString("_id");

                            Line newLine = null;
                            String stringDate = lineObject.getString("createdAt");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                long l = Instant.parse(stringDate)
                                        .toEpochMilli();
                                Date date = new Date(l);
                                newLine = Line.builder().id(lineId).content(content).createdAt(date).type(type).build();

                                // last message
                                int lengthMessageList = messageList.size();
                                if (lengthMessageList > 0) {
                                    Message lastMessage = messageList.get(lengthMessageList - 1);
                                    if (lastMessage.getSender().equals(myId)) {
                                        List<Line> lineList = new ArrayList<>();
                                        lineList.add(newLine);
                                        Message message = new Message(lineList, currentUserId, null, newMessageId);
                                        messageList.add(message);
                                        groupMessageAdapter.notifyItemInserted(messageList.size());
                                        newMessageTv.setVisibility(View.VISIBLE);
                                    } else {
                                        int lengthLineList = lastMessage.getLines().size();
                                        List<Line> lastLineList = lastMessage.getLines();
                                        Line lastLine = lastMessage.getLines().get(lengthLineList - 1);

                                        long duration = date.getTime() - lastLine.getCreatedAt().getTime();
                                        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                                        if (diffInMinutes >= 1 || !currentUserId.equals(lastMessage.getSender())) {
                                            List<Line> lineList = new ArrayList<>();
                                            lineList.add(newLine);
                                            Message message = new Message(lineList, currentUserId, null, newMessageId);
                                            messageList.add(message);
                                            groupMessageAdapter.notifyItemInserted(messageList.size());
                                            newMessageTv.setVisibility(View.VISIBLE);
                                        } else {
                                            lastLineList.add(newLine);
                                            groupMessageAdapter.notifyDataSetChanged();

                                            if (scrollLastPositionBtn.getVisibility() == View.VISIBLE) {
                                                scrollLastPositionBtn.setVisibility(View.GONE);
                                                newMessageTv.setVisibility(View.VISIBLE);
                                            }
                                        }


                                    }
                                } else {
                                    List<Line> lineList = new ArrayList<>();
                                    lineList.add(newLine);
                                    Message message = new Message(lineList, currentUserId, null, newMessageId);
                                    messageList.add(message);
                                    groupMessageAdapter.notifyItemInserted(messageList.size());

                                    if (scrollLastPositionBtn.getVisibility() != View.GONE) {
                                        scrollLastPositionBtn.setVisibility(View.GONE);
                                        newMessageTv.setVisibility(View.VISIBLE);
                                    }
                                }
                            }


                            // emit
                            if (isActivityRunning) {
                                SocketClient.getInstance().emit(MyConstant.READ_MESSAGE, chatId, newLine.getId(), myId);
                            }


                        } catch (Exception e) {
                            CustomAlert.showToast(GroupChatActivity.this, CustomAlert.WARNING, e.getMessage());
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
                            if (lastMessage.getSender().equals(myId)) {
                                List<Line> lineList = lastMessage.getLines();
                                Line lastLine = lineList.get(lineList.size() - 1);
                                lastLine.setReceived(true);
                                groupMessageAdapter.setData(messageList);
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
                                groupMessageAdapter.setData(messageList);
                            }
                        }
                    }
                });
            }
        });

        // người khác đang nhập tin nhắn
        SocketClient.getInstance().on(TEXTING_EVENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textingTv.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        SocketClient.getInstance().on(STOP_TEXTING_EVENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textingTv.setVisibility(View.GONE);
                    }
                });
            }
        });

        // người khác thu hồi tin nhắn
        SocketClient.getInstance().on(MyConstant.RECALL_LINE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String chatId = (String) args[0];
                        String lineId = (String) args[1];

                        for (Message message: messageList){
                            List<Line> lineList = message.getLines();
                            for(Line line: lineList){
                                if(line.getId().equals(lineId)){
                                    line.setType(MyConstant.TEXT_TYPE);
                                    line.setContent(getString(R.string.recall_line));
                                    break;
                                }
                            }
                        }

                        groupMessageAdapter.setData(messageList);
                    }
                });
            }
        });


    }


    private void loadMessage(String id, int page, int type) {
        pagingPb.setVisibility(View.VISIBLE);
        RetrofitService.getInstance.getMessageById(id, page, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<List<Message>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                         if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            loadMessage(id, page, type);
                        } else if (response.code() == 200) {
                            pagingPb.setVisibility(View.GONE);
                            List<Message> tempMessageList = response.body();
                            if (tempMessageList.size() == 0) {
                                isLoading = false;
                            }
                            if (type == NEXT_PAGE_LOAD) {
                                messageList.addAll(0, tempMessageList);
                            } else {
                                messageList = tempMessageList;
                            }


                            // emit read message
                            // last message
                            int lengthMessageList = messageList.size();
                            if (lengthMessageList > 0) {
                                Message lastMessage = messageList.get(lengthMessageList - 1);
                                if (!lastMessage.getSender().equals(myId)) {
                                    int lengthLineList = lastMessage.getLines().size();
                                    Line lastLine = lastMessage.getLines().get(lengthLineList - 1);
                                    SocketClient.getInstance().emit(MyConstant.READ_MESSAGE, chatId, lastLine.getId(), myId);
                                }
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            if(type == NEXT_PAGE_LOAD){
                                int position = messageList.size() - currentPage * 10 + 10;
                                linearLayoutManager.scrollToPositionWithOffset(position,0);
                            }
                            groupMessageAdapter.setData(messageList);
                            if (type == FIRST_LOAD) {
                                recyclerView.setAdapter(groupMessageAdapter);
                            }


                        } else {
                            pagingPb.setVisibility(View.GONE);
                            CustomAlert.showToast(GroupChatActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable t) {
                        CustomAlert.showToast(GroupChatActivity.this, CustomAlert.WARNING, t.getMessage());
                    }
                });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<Uri> docPaths = new ArrayList<>();
                docPaths.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                // 1 file
                if (docPaths.size() == 1) {
                    Uri uri = docPaths.get(0);
                    String realPath = RealPathUtil.getRealPath(this, uri);
                    File file = new File(realPath);
                    int file_size = Integer.parseInt(String.valueOf(file.length() / 102400));
                    if (file_size <= 20) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        MultipartBody.Part mPartImage = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                        uploadFile(mPartImage);
                    } else {
                        CustomAlert.showToast(this, CustomAlert.WARNING, "File phải nhỏ hơn 20BM");
                    }

                }
                // multi file
                else {
                    int count = 0;
                    for (Uri uri : docPaths) {
                        String realPath = RealPathUtil.getRealPath(this, uri);
                        File file = new File(realPath);
                        float file_size = Float.parseFloat(String.valueOf(file.length())) / 1024000;
                        if (file_size <= 20) {
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                            MultipartBody.Part mPartImage = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                            uploadFile(mPartImage);
                        } else {
                            count++;
                        }

                    }
                    if (count > 0) {
                        CustomAlert.showToast(this, CustomAlert.WARNING, "Có " + count + " file lớn hơn 20MB");
                    }
                }


            }
        }
    }

    private void uploadFile(MultipartBody.Part file) {
        RetrofitService.getInstance.uploadFileChat(file, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            uploadImage(file);
                        } else if (response.code() == 200) {
                            String nameFile = response.body();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("content", nameFile);
                                jsonObject.put("type", MyConstant.FILE_TYPE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            SocketClient.getInstance().emit(MyConstant.PRIVATE_MESSAGE, chatId, myId, jsonObject);
                            SocketClient.getInstance().emit(MyConstant.RENDER_FILE_REQUEST, chatId);
                        } else {
                            CustomAlert.showToast(GroupChatActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRunning = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        SocketClient.getInstance().emit(STOP_TEXTING_EVENT, chatId, myId);
    }
}