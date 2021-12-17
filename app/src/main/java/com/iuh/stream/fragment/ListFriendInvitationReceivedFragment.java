package com.iuh.stream.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.iuh.stream.R;
import com.iuh.stream.adapter.InvitationReceivedAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListFriendInvitationReceivedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private User user;
    private View view;
    private List<String> listInvitationReceivedId;
    private List<User> listInvitationReceivedUser;
    private InvitationReceivedAdapter invitationReceivedAdapter;
    private ShimmerRecyclerView shimmerRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int LOAD = 1;
    private static final int REFRESH = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_list_friend_invitation_recived, container, false);
        addControls();
        return view;
    }

    private void addControls() {
        swipeRefreshLayout = view.findViewById(R.id.friend_invitation_received_srl);
        swipeRefreshLayout.setColorSchemeResources(R.color.main);
        swipeRefreshLayout.setOnRefreshListener(this);
        listInvitationReceivedId = new ArrayList<>();
        listInvitationReceivedUser = new ArrayList<>();
        invitationReceivedAdapter = new InvitationReceivedAdapter(getContext());
        invitationReceivedAdapter.setData(listInvitationReceivedUser);
        shimmerRecyclerView = view.findViewById(R.id.list_received_rvc);
        shimmerRecyclerView.setAdapter(invitationReceivedAdapter);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmerRecyclerView.setDemoLayoutReference(R.layout.chat_list_item_demo);
        shimmerRecyclerView.showShimmerAdapter();
        getCurrentUser(LOAD);
    }

    private void getCurrentUser(int type) {
        RetrofitService.getInstance.getMeInfo(DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            getCurrentUser(type);
                        }
                        else{
                            user = response.body();
                            if (user != null) {
                                swipeRefreshLayout.setRefreshing(false);
                                listInvitationReceivedId = user.getFriendRequests();
                                shimmerRecyclerView.hideShimmerAdapter();
                            }
                            for(String id: listInvitationReceivedId){
                                getListInvitationReceivedUser(id, type);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        CustomAlert.showToast(getActivity(), CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    private void getListInvitationReceivedUser(String id, int type) {
        listInvitationReceivedUser.clear();
        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            getListInvitationReceivedUser(id, type);
                        }
                        else{
                            user = response.body();
                            listInvitationReceivedUser.add(user);
                        }

                        invitationReceivedAdapter.setData(listInvitationReceivedUser);
                        shimmerRecyclerView.setAdapter(invitationReceivedAdapter);

                        if(type == REFRESH){
                            swipeRefreshLayout.setRefreshing(false);
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        CustomAlert.showToast(getActivity(), CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    @Override
    public void onRefresh() {
        getCurrentUser(REFRESH);
    }
}