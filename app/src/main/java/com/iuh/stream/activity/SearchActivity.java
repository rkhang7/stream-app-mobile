package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.iuh.stream.R;
import com.iuh.stream.adapter.FriendsAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.Contact;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private EditText searchEt;
    private List<User> listFriend;
    private List<String> listFriendId ;
    private FriendsAdapter friendsAdapter;
    private RecyclerView recyclerView;
    private User user;
    private TextView notFoundTv;
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

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(TextUtils.isEmpty(editable.toString().trim())){
                    recyclerView.setVisibility(View.GONE);
                    notFoundTv.setVisibility(View.GONE);
                }
                else{
                    listFriend.clear();
                    filterFriends(editable.toString());
                }

            }
        });
    }

    private void filterFriends(String key) {
        getMeInfo(key);
    }


    private void addControls() {
        backBtn = findViewById(R.id.back_btn);
        searchEt = findViewById(R.id.search_et);
        notFoundTv = findViewById(R.id.not_found_tv);
        listFriend = new ArrayList<>();
        recyclerView = findViewById(R.id.search_rcv);
        friendsAdapter =  new FriendsAdapter(this);
        friendsAdapter.setData(listFriend);
        recyclerView.setAdapter(friendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchEt.requestFocus();
    }

    private void getMeInfo(String key){
        RetrofitService.getInstance.getMeInfo(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            getMeInfo(key);
                        } else {
                            user = response.body();
                            if (user != null) {
                                listFriendId = user.getContacts();
                            }
                            if(listFriendId.size() > 0){
                                for (String id : listFriendId) {
                                    getListFriendUser(id, key);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

                    }
                });
    }

    private void getListFriendUser(String id, String key) {

        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            getListFriendUser(id, key);
                        } else {
                            user = response.body();
                            listFriend.add(user);
                        }

                        List<User> filterFriends = new ArrayList<>();
                        for(User user : listFriend){
                            if(user.getFirstName().toLowerCase().contains(key)
                                    || user.getLastName().toLowerCase().contains(key)){
                                filterFriends.add(user);
                            }
                        }

                        if(filterFriends.size() > 0){
                            recyclerView.setVisibility(View.VISIBLE);
                            notFoundTv.setVisibility(View.GONE);
                            friendsAdapter.setData(filterFriends );
                        }
                        else{
                            recyclerView.setVisibility(View.GONE);
                            notFoundTv.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        CustomAlert.showToast(SearchActivity.this, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }
}