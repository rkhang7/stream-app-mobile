package com.iuh.stream.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iuh.stream.R;
import com.iuh.stream.adapter.InvitationSentAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFriendInvitationSentFragment extends Fragment {

    private User user;
    private View view;
    private List<User> listInvitationSentUser;
    private List<String> listInvitationSentId;
    private InvitationSentAdapter invitationSentAdapter;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_friend_invitation_sent, container, false);
        addControls();
        return view;
    }

    private void addControls() {
        listInvitationSentId = new ArrayList<>();
        listInvitationSentUser = new ArrayList<>();
        invitationSentAdapter = new InvitationSentAdapter(getContext());
        invitationSentAdapter.setData(listInvitationSentUser);
        recyclerView = view.findViewById(R.id.list_sent_rcv);
        recyclerView.setAdapter(invitationSentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       getCurrentUser();
    }

    private void getCurrentUser() {
        RetrofitService.getInstance.getMeInfo(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(Constants.REFRESH_TOKEN);
                            getCurrentUser();
                        }
                        else if(response.code() == 200){
                            user = response.body();
                            listInvitationSentId = user.getFriendInvitations();
                            for(String id: listInvitationSentId){
                                getListInvitationSentUser(id);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        CustomAlert.showToast(getActivity(), CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    private void getListInvitationSentUser(String id) {
        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            getListInvitationSentUser(id);
                        }
                        else if(response.code() == 404){
                            CustomAlert.showToast(getActivity(), CustomAlert.WARNING, "Not found User");
                        }
                        else if(response.code() == 200){
                            user = response.body();
                            listInvitationSentUser.add(user);
                        }
                        invitationSentAdapter.setData(listInvitationSentUser);
                        recyclerView.setAdapter(invitationSentAdapter);
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        CustomAlert.showToast(getActivity(), CustomAlert.WARNING, t.getMessage());
                    }
                });
    }
}