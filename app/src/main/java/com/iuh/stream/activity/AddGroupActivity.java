package com.iuh.stream.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.iuh.stream.R;
import com.iuh.stream.adapter.FriendCheckBoxAdapter;
import com.iuh.stream.api.UserListAsyncResponse;
import com.iuh.stream.api.UserUtil;
import com.iuh.stream.interfaces.FriendListener;
import com.iuh.stream.models.Contact;
import com.iuh.stream.models.User;

import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends AppCompatActivity implements FriendListener {
    private ImageButton backBtn;
    private FriendCheckBoxAdapter friendCheckBoxAdapter;
    private RecyclerView recyclerView;
    private TextView countTv, notFoundTv;
    private EditText searchEt;
    private Button createGroupBtn;
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
        createGroupBtn = findViewById(R.id.create_group_btn);
        notFoundTv = findViewById(R.id.add_friend_not_found_tv);
        searchEt = findViewById(R.id.add_group_search_tv);
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
        countTv.setText("Đã chọn: " + list.size());
        if(list.size() > 1){
            createGroupBtn.setVisibility(View.VISIBLE);
        }
        else{
            createGroupBtn.setVisibility(View.GONE);
        }
    }
}