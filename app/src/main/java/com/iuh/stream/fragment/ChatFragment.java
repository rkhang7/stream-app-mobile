package com.iuh.stream.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.iuh.stream.R;
import com.iuh.stream.activity.AddGroupActivity;
import com.iuh.stream.activity.SearchActivity;
import com.iuh.stream.adapter.ChatListAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.models.chatlist.ChatList;
import com.iuh.stream.models.chatlist.PersonalChat;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatFragment extends Fragment {
    private LinearLayout searchLayout;
    private TextView searchTv;
    private ImageButton addGroupBtn;
    private View view;
    private ChatListAdapter chatListAdapter;
    private RecyclerView recyclerView;

    private ChatList chatList;
    private List<PersonalChat> personalChatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        addControls();
        addEvents();

        return view;
    }

    private void addEvents() {
        addGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddGroupActivity.class));
            }
        });
    }

    private void addControls() {
        searchLayout = view.findViewById(R.id.search_layout);
        searchTv = view.findViewById(R.id.search_tv);
        addGroupBtn = view.findViewById(R.id.add_group_btn);

        personalChatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(getContext());
        chatListAdapter.setData(personalChatList);
        recyclerView = view.findViewById(R.id.list_chat_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatListAdapter);

        getChatList(DataLocalManager.getStringValue(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN)));

        SocketClient.getInstance().on(Constants.PRIVATE_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getChatList(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN));
                    }
                });
            }
        });
        SocketClient.getInstance().on(Constants.MESSAGE_SENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getChatList(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN));
                    }
                });
            }
        });
    }



    private void getChatList(String accessToken) {
        RetrofitService.getInstance.getChatList(accessToken)
                .enqueue(new Callback<ChatList>() {
                    @Override
                    public void onResponse(Call<ChatList> call, Response<ChatList> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            getChatList(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN));
                        }
                         if(response.code() == 200){
                            chatList = response.body();
                             Log.e("TAG", "onResponse: " + chatList );
                             chatListAdapter.setData(chatList.getPersonalChats());
                             recyclerView.setAdapter(chatListAdapter);
                        }

                    }

                    @Override
                    public void onFailure(Call<ChatList> call, Throwable t) {

                    }
                });
    }

}