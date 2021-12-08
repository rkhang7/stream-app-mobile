package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.iuh.stream.models.request.AddMemberRequest;
import com.iuh.stream.models.response.CreateGroupResponse;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMemberActivity extends AppCompatActivity implements FriendListener {
    private ImageButton backBtn;
    private FriendCheckBoxAdapter friendCheckBoxAdapter;
    private RecyclerView recyclerView;
    private TextView countTv, notFoundTv;
    private EditText searchEt;
    private Button addMemberBtn;
    private List<String> memberChooseList;
    private Bundle bundle;
    private String chatId;

    private List<User> memberInGroup;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

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


        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "onClick: " + memberChooseList.toString() );
                AddMemberRequest addMemberRequest = new AddMemberRequest();
                addMemberRequest.setNewMembers(memberChooseList);
                addMemberRequest.setId(chatId);
                addMembers(addMemberRequest);
            }
        });

    }

    private void addMembers(AddMemberRequest addMemberRequest) {
        RetrofitService.getInstance.addMembers(addMemberRequest, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            addMembers(addMemberRequest);
                        }
                        else if(response.code() == 200){
                            CustomAlert.showToast(AddMemberActivity.this, CustomAlert.INFO, "Thêm thành công");
                            finish();

                        }
                        else {
                            CustomAlert.showToast(AddMemberActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        CustomAlert.showToast(AddMemberActivity.this, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }


    private void filterFriends(String key) {
        new UserUtil().getListFriend(new UserListAsyncResponse() {
            @Override
            public void processFinnish(List<User> friendArrayList) {
                List<User> filterFriends = new ArrayList<>();
                for (User user : friendArrayList) {
                    if (user.getEmail() != null) {
                        if (user.getFirstName().toLowerCase().contains(key)
                                || user.getLastName().toLowerCase().contains(key)
                                || user.getEmail().equals(key))
                        {
                            filterFriends.add(user);
                        }
                    }
                    if (user.getPhoneNumber() != null) {
                        if (user.getFirstName().toLowerCase().contains(key)
                                || user.getLastName().toLowerCase().contains(key)
                                || user.getPhoneNumber().equals(key)
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
        memberChooseList = new ArrayList<>();
        addMemberBtn = findViewById(R.id.addMemberBtn);
        notFoundTv = findViewById(R.id.add_friend_not_found_tv);
        searchEt = findViewById(R.id.add_group_search_tv);
        countTv = findViewById(R.id.count_tv);
        backBtn = findViewById(R.id.back_btn);
        friendCheckBoxAdapter = new FriendCheckBoxAdapter(this, this);
        friendCheckBoxAdapter.setData(new ArrayList<User>());
        recyclerView  =findViewById(R.id.friend_rvc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(friendCheckBoxAdapter);

        bundle = getIntent().getExtras();
        memberInGroup = (List<User>) bundle.getSerializable(MyConstant.LIST_MEMBER_KEY);
        group = (Group) bundle.getSerializable(MyConstant.GROUP_KEY);
        chatId = bundle.getString(MyConstant.GROUP_CHAT_ID);

        loadFriend();
    }

    private void loadFriend() {
        new UserUtil().getListFriend(new UserListAsyncResponse() {
            @Override
            public void processFinnish(List<User> friendArrayList) {
                List<User> tempUser = new ArrayList<>();
                for(User user: friendArrayList){
                   if(!checkUserExistInList(user.get_id(), memberInGroup)){
                       tempUser.add(user);
                   }
                }
                friendCheckBoxAdapter.setData(tempUser);
                recyclerView.setAdapter(friendCheckBoxAdapter);
            }
        });
    }

    private boolean checkUserExistInList(String id, List<User> userList){
        for (User user: userList){
            if(user.get_id().equals(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFriendChange(List<String> list) {
        memberChooseList = list;
        countTv.setText("Đã chọn: " + list.size());
        if(list.size() > 0){
            addMemberBtn.setVisibility(View.VISIBLE);
        }
        else{
            addMemberBtn.setVisibility(View.GONE);
        }
    }
}