package com.iuh.stream.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.flexbox.FlexboxLayout;
import com.iuh.stream.R;
import com.iuh.stream.activity.CreateGroupActivity;
import com.iuh.stream.adapter.ChatListAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.models.chatlist.ChatList;
import com.iuh.stream.models.chatlist.Chats;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GroupContactFragment extends Fragment {
    private View view;
    private FlexboxLayout createGroupLayout;
    private TextView numberGroupChatTv;
    private ChatListAdapter chatListAdapter;
    private ShimmerRecyclerView shimmerRecyclerView;

    private ChatList chatList;
    private List<Chats> chatsList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_group_contact, container, false);

        addControls();
        addEvents();
        return view;
    }

    private void addEvents() {
        createGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateGroupActivity.class));
            }
        });
    }

    private void addControls() {
        createGroupLayout = view.findViewById(R.id.create_group_layout);
        numberGroupChatTv = view.findViewById(R.id.number_group_chat_tv);

        chatsList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(getContext());
        chatListAdapter.setData(chatsList);
        shimmerRecyclerView = view.findViewById(R.id.group_chat_rcv);
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

        // mình nhắn tin, server trả về
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

        // người khác tạo
        SocketClient.getInstance().on(MyConstant.CREATE_GROUP, new Emitter.Listener() {
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

        SocketClient.getInstance().on(MyConstant.GROUP_NOTIFICATION, new Emitter.Listener() {
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


                            List<Chats> chatsListTemp = new ArrayList<>();
                            for(Chats chats: chatList.getChats()){
                                if(chats.getGroup() != null){
                                    chatsListTemp.add(chats);
                                }
                            }

                            numberGroupChatTv.setText("Nhóm đã tham gia (" + chatsListTemp.size() + ")");

                            chatListAdapter.setData(chatsListTemp);
                            shimmerRecyclerView.setAdapter(chatListAdapter);
                        }

                    }

                    @Override
                    public void onFailure(Call<ChatList> call, Throwable t) {

                    }
                });
    }
}