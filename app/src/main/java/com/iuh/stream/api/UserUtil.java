package com.iuh.stream.api;

import androidx.annotation.NonNull;

import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.fragment.PersonalContactFragment;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserUtil {
    private User user;
    private List<User> listUser = new ArrayList<>();
    private List<String> listUserId;

    public List<User> getListFriend(UserListAsyncResponse callback){

        getMeInfo(callback);

        return listUser;
    }


    private void getMeInfo(UserListAsyncResponse callback) {
        RetrofitService.getInstance.getMeInfo(DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            getMeInfo(callback);
                        } else {
                            user = response.body();
                            if (user != null) {
                                listUserId = user.getContacts();
                                if(PersonalContactFragment.shimmerRecyclerView != null){
                                    PersonalContactFragment.shimmerRecyclerView.hideShimmerAdapter();
                                }

                            }
                            if (listUserId.size() > 0) {
                                for (String id : listUserId) {
                                    getListFriendUser(id, callback);
                                }
                            }


                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

                    }
                });
    }

    private void getListFriendUser(String id, UserListAsyncResponse callback) {
        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            getListFriendUser(id, callback);
                        } else {
                            user = response.body();
                            listUser.add(user);
                        }

                        callback.processFinnish(listUser);
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

                    }
                });
    }

}
