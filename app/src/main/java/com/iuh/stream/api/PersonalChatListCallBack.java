package com.iuh.stream.api;

import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.interfaces.PersonalChatListAsyncResponse;
import com.iuh.stream.models.chatlist.ChatList;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalChatListCallBack {
    private ChatList chatList;
    public void getPersonalChatList(PersonalChatListAsyncResponse callback){
        getChatList(DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN), callback);
    }

    private void getChatList(String accessToken, PersonalChatListAsyncResponse callback) {
        RetrofitService.getInstance.getChatList(accessToken)
                .enqueue(new Callback<ChatList>() {
                    @Override
                    public void onResponse(Call<ChatList> call, Response<ChatList> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            getChatList(DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN), callback);
                        }
                        if(response.code() == 200){
                            chatList = response.body();
                            callback.processFinnish(chatList.getPersonalChats());
                        }

                    }

                    @Override
                    public void onFailure(Call<ChatList> call, Throwable t) {

                    }
                });
    }
}
