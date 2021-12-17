package com.iuh.stream.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.iuh.stream.R;
import com.iuh.stream.adapter.InvitationSentAdapter;
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

public class ListFriendInvitationSentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private User user;
    private View view;
    private List<User> listInvitationSentUser;
    private List<String> listInvitationSentId;
    private InvitationSentAdapter invitationSentAdapter;
    private ShimmerRecyclerView shimmerRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int LOAD = 1;
    private static final int REFRESH = 2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_friend_invitation_sent, container, false);
        addControls();
        return view;
    }

    private void addControls() {
        swipeRefreshLayout = view.findViewById(R.id.friend_invitation_sent_srl);
        swipeRefreshLayout.setColorSchemeResources(R.color.main);
        swipeRefreshLayout.setOnRefreshListener(this);
        listInvitationSentId = new ArrayList<>();
        listInvitationSentUser = new ArrayList<>();
        invitationSentAdapter = new InvitationSentAdapter(getContext());
        invitationSentAdapter.setData(listInvitationSentUser);
        shimmerRecyclerView = view.findViewById(R.id.list_sent_rcv);
        shimmerRecyclerView.setAdapter(invitationSentAdapter);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmerRecyclerView.setDemoLayoutReference(R.layout.chat_list_item_demo);
        shimmerRecyclerView.showShimmerAdapter();
       getCurrentUser(LOAD);
    }

    private void getCurrentUser(int type) {
        RetrofitService.getInstance.getMeInfo(DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            getCurrentUser(type);
                        }
                        else if(response.code() == 200){
                            user = response.body();
                            if (user != null) {
                                swipeRefreshLayout.setRefreshing(false);
                                listInvitationSentId = user.getFriendInvitations();
                                shimmerRecyclerView.hideShimmerAdapter();
                            }
                            for(String id: listInvitationSentId){
                                getListInvitationSentUser(id, type);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        CustomAlert.showToast(getActivity(), CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    private void getListInvitationSentUser(String id, int type) {
        listInvitationSentUser.clear();
        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            getListInvitationSentUser(id,type);
                        }
                        else if(response.code() == 404){
                            CustomAlert.showToast(getActivity(), CustomAlert.WARNING, "Not found User");
                        }
                        else if(response.code() == 200){
                            user = response.body();
                            listInvitationSentUser.add(user);
                        }
                        invitationSentAdapter.setData(listInvitationSentUser);
                        shimmerRecyclerView.setAdapter(invitationSentAdapter);
                        if(type == REFRESH){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        CustomAlert.showToast(getActivity(), CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    @Override
    public void onRefresh() {
        getCurrentUser(REFRESH);
    }
}