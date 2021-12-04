package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
import com.iuh.stream.adapter.ChatListAdapter;
import com.iuh.stream.api.PersonalChatListCallBack;
import com.iuh.stream.interfaces.PersonalChatListAsyncResponse;
import com.iuh.stream.models.User;
import com.iuh.stream.models.chatlist.Chats;

import java.util.ArrayList;
import java.util.List;

public class SearchConversationActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private EditText searchEt;
    private RecyclerView recyclerView;
    private TextView notFoundTv;
    private ProgressBar progressBar;
    private List<Chats> chatsList;
    private ChatListAdapter chatListAdapter;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_conversation);

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
                    chatsList.clear();
                    filterFriends(editable.toString());
                }

            }
        });
    }

    private void filterFriends(String key) {
        new PersonalChatListCallBack().getPersonalChatList(new PersonalChatListAsyncResponse() {
            @Override
            public void processFinnish(List<Chats> chatsList) {
                List<Chats> filterChats = new ArrayList<>();
                for(Chats chats : chatsList){
                    List<User> userList = chats.getUsers();
                    for(User user: userList){
                        if(!user.get_id().equals(mAuth.getCurrentUser().getUid())){
                            if (user.getFirstName().toLowerCase().contains(key.toLowerCase())
                                    || user.getLastName().toLowerCase().contains(key.toLowerCase()))
                            {
                                filterChats.add(chats);
                            }
                        }
                    }
                }

                if (filterChats.size() > 0) {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    notFoundTv.setVisibility(View.GONE);
                    chatListAdapter.setData(filterChats);
                } else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    notFoundTv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addControls() {
        mAuth = FirebaseAuth.getInstance();
        backBtn = findViewById(R.id.back_btn);
        searchEt = findViewById(R.id.search_conversation_et);

        recyclerView = findViewById(R.id.search_conversation_rcv);
        notFoundTv = findViewById(R.id.not_found_tv);
        progressBar = findViewById(R.id.search_conversation_pb);

        chatsList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(this);
        chatListAdapter.setData(chatsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatListAdapter);
    }
}