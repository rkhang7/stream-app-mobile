package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iuh.stream.R;
import com.iuh.stream.adapter.FriendsAdapter;
import com.iuh.stream.api.UserListAsyncResponse;
import com.iuh.stream.api.UserUtil;
import com.iuh.stream.models.User;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private EditText searchEt;
    private List<User> listFriend;
    private List<String> listFriendId;
    private FriendsAdapter friendsAdapter;
    private RecyclerView recyclerView;
    private User user;
    private TextView notFoundTv;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
                if (charSequence.length() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    notFoundTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    notFoundTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString().trim())) {
                    recyclerView.setVisibility(View.GONE);
                    notFoundTv.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    listFriend.clear();
                    filterFriends(editable.toString());
                }

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
                        if (user.getFirstName().toLowerCase().contains(key.toLowerCase())
                                || user.getLastName().toLowerCase().contains(key.toLowerCase())
                                || user.getEmail().equals(key.toLowerCase()))
                        {
                            filterFriends.add(user);
                        }
                    }
                    if (user.getPhoneNumber() != null) {
                        if (user.getFirstName().toLowerCase().contains(key.toLowerCase())
                                || user.getLastName().toLowerCase().contains(key.toLowerCase())
                                || user.getPhoneNumber().equals(key.toLowerCase())
                        ) {
                            filterFriends.add(user);
                        }
                    }
                }
                if (filterFriends.size() > 0) {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    notFoundTv.setVisibility(View.GONE);
                    friendsAdapter.setData(filterFriends);
                } else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    notFoundTv.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void addControls() {
        backBtn = findViewById(R.id.back_btn);
        searchEt = findViewById(R.id.search_et);
        notFoundTv = findViewById(R.id.not_found_tv);
        listFriend = new ArrayList<>();
        progressBar = findViewById(R.id.search_friend_pb);
        recyclerView = findViewById(R.id.search_rcv);
        friendsAdapter = new FriendsAdapter(this);
        friendsAdapter.setData(listFriend);
        recyclerView.setAdapter(friendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchEt.requestFocus();
    }

}