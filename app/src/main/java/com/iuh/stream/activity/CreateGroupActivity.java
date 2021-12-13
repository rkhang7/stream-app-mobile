package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iuh.stream.R;
import com.iuh.stream.adapter.FriendCheckBoxAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.api.UserListAsyncResponse;
import com.iuh.stream.api.UserUtil;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.interfaces.FriendListener;
import com.iuh.stream.models.User;
import com.iuh.stream.models.chatlist.Group;
import com.iuh.stream.models.response.CreateGroupResponse;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroupActivity extends AppCompatActivity implements FriendListener {
    private ImageButton backBtn;
    private FriendCheckBoxAdapter friendCheckBoxAdapter;
    private RecyclerView recyclerView;
    private TextView countTv, notFoundTv;
    private EditText searchEt, groupNameEt;
    private Button createGroupBtn;
    private List<String> memberList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        
        addControls();
        addEvents();
    }

    private void addEvents() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterFriends(editable.toString());
            }
        });


        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameEt.getText().toString().toString();
                if(TextUtils.isEmpty(groupName)){
                    groupNameEt.setError("Không được rỗng");
                }
                else{
                    createGroupChat(memberList, groupName);
                }
            }
        });

    }

    private void createGroupChat(List<String> memberList, String groupName) {
        RetrofitService.getInstance.createGroup(memberList, groupName, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<CreateGroupResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CreateGroupResponse> call, @NonNull Response<CreateGroupResponse> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            createGroupChat(memberList, groupName);
                        }
                        else if(response.code() == 400){
                            CustomAlert.showToast(CreateGroupActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));
                        }
                        else if(response.code() == 200){
                            CreateGroupResponse createGroupResponse = response.body();
                            Group group = createGroupResponse.getGroup();
                            Intent intent = new Intent(CreateGroupActivity.this, GroupChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(MyConstant.GROUP_KEY, group);
                            bundle.putInt(MyConstant.NUMBER_MEMBER, createGroupResponse.getUsers().size());
                            bundle.putString(MyConstant.GROUP_CHAT_ID, createGroupResponse.getId());
                            bundle.putSerializable(MyConstant.LIST_MEMBER_KEY, (Serializable) createGroupResponse.getUsers());
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CreateGroupResponse> call, @NonNull Throwable t) {
                        CustomAlert.showToast(CreateGroupActivity.this, CustomAlert.WARNING, t.getMessage());

                    }
                });
    }

    private void filterFriends(String key) {
        new UserUtil().getListFriend(new UserListAsyncResponse() {
            @Override
            public void processFinnish(List<User> friendArrayList) {
                List<User> filterFriends = new ArrayList<>();
                for (User user : friendArrayList) {
                    String fullName = user.getFirstName().toLowerCase() + " " +
                            user.getLastName().toLowerCase();
                    if (user.getEmail() != null) {
                        if (user.getFirstName().toLowerCase().contains(key.toLowerCase())
                                || user.getLastName().toLowerCase().contains(key.toLowerCase())
                                || user.getEmail().equals(key.toLowerCase())
                                || fullName.contains(key.toLowerCase()))
                        {
                            filterFriends.add(user);
                        }
                    }
                    if (user.getPhoneNumber() != null) {
                        if (user.getFirstName().toLowerCase().contains(key.toLowerCase())
                                || user.getLastName().toLowerCase().contains(key.toLowerCase())
                                || user.getPhoneNumber().equals(key.toLowerCase())
                                || fullName.contains(key.toLowerCase()
                        )
                        ) {
                            filterFriends.add(user);
                        }
                    }
                }

                if (filterFriends.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    friendCheckBoxAdapter.setData(filterFriends);
                    notFoundTv.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    notFoundTv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addControls() {
        memberList = new ArrayList<>();
        createGroupBtn = findViewById(R.id.create_group_btn);
        notFoundTv = findViewById(R.id.add_friend_not_found_tv);
        searchEt = findViewById(R.id.add_group_search_tv);
        groupNameEt = findViewById(R.id.group_name_et);
        countTv = findViewById(R.id.count_tv);
        backBtn = findViewById(R.id.back_btn);
        friendCheckBoxAdapter = new FriendCheckBoxAdapter(this, this);
        friendCheckBoxAdapter.setData(new ArrayList<User>());
        recyclerView  =findViewById(R.id.friend_rvc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(friendCheckBoxAdapter);

        loadFriend();
    }

    private void loadFriend() {
        new UserUtil().getListFriend(new UserListAsyncResponse() {
            @Override
            public void processFinnish(List<User> friendArrayList) {
                friendCheckBoxAdapter.setData(friendArrayList);
                recyclerView.setAdapter(friendCheckBoxAdapter);
            }
        });
    }

    @Override
    public void onFriendChange(List<String> list) {
        memberList = list;
        countTv.setText("Đã chọn: " + list.size());
        if(list.size() > 1){
            createGroupBtn.setVisibility(View.VISIBLE);
        }
        else{
            createGroupBtn.setVisibility(View.GONE);
        }
    }
}