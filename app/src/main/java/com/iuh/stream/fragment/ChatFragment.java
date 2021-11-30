package com.iuh.stream.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.iuh.stream.R;
import com.iuh.stream.activity.AddGroupActivity;
import com.iuh.stream.activity.SearchConversationActivity;
import com.iuh.stream.adapter.ChatListAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.models.chat.Line;
import com.iuh.stream.models.chatlist.ChatList;
import com.iuh.stream.models.chatlist.PersonalChat;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatFragment extends Fragment{
    private LinearLayout searchLayout;
    private TextView searchTv, notFoundTv;
    private ImageButton addGroupBtn;
    private View view;
    private ChatListAdapter chatListAdapter;
    private ShimmerRecyclerView shimmerRecyclerView;

    private ChatList chatList;
    private List<PersonalChat> personalChatList;
    private static final int LOAD = 1;
    private static final int REFRESH = 2;


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

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), SearchConversationActivity.class));
            }
        });
        searchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), SearchConversationActivity.class));
            }
        });
    }

    private void addControls() {
        searchLayout = view.findViewById(R.id.search_conversation_layout);
        searchTv = view.findViewById(R.id.search_conversation_tv);
        notFoundTv = view.findViewById(R.id.not_found_tv);
        addGroupBtn = view.findViewById(R.id.add_group_btn);



        personalChatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(getContext());
        chatListAdapter.setData(personalChatList);
        shimmerRecyclerView = view.findViewById(R.id.list_chat_rcv);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmerRecyclerView.setAdapter(chatListAdapter);
        shimmerRecyclerView.setDemoLayoutReference(R.layout.chat_list_item_demo);
        shimmerRecyclerView.showShimmerAdapter();


        getChatList(DataLocalManager.getStringValue(DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN)));

        // người khác nhắn tin, server trả về
        SocketClient.getInstance().on(MyConstant.PRIVATE_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getChatList(DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
                    }
                });
            }
        });
        SocketClient.getInstance().on(MyConstant.MESSAGE_SENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getChatList(DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
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
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            getChatList(DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
                        }
                         else if(response.code() == 200){
                            chatList = response.body();
                             shimmerRecyclerView.hideShimmerAdapter();
                             chatListAdapter.setData(chatList.getPersonalChats());
                             shimmerRecyclerView.setAdapter(chatListAdapter);
                        }

                    }

                    @Override
                    public void onFailure(Call<ChatList> call, Throwable t) {

                    }
                });
    }

}