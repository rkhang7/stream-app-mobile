package com.iuh.stream.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.iuh.stream.R;
import com.iuh.stream.activity.FriendInvitationActivity;
import com.iuh.stream.activity.PhoneFriendsActivity;
import com.iuh.stream.adapter.FriendsAdapter;
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
import retrofit2.Retrofit;


public class PersonalContactFragment extends Fragment {
    // views
    private View view;
    private Button friendFromContactBtn;
    private Button friendInvitationBtn;
    private List<String> listFriendId;
    private List<User> listFriendUser;
    private FriendsAdapter friendsAdapter;
    private RecyclerView recyclerView;
    private User user;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_personal_contact, container, false);

        addControls();
        addEvents();

        return view;
    }

    private void addEvents() {
        friendFromContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionlistener = new PermissionListener() {

                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(getContext(), PhoneFriendsActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                };

                TedPermission.create()
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("Nếu bạn không cấp quyền, bạn sẽ không thể sử dụng dịch vụ này\n\nVui lòng cấp quyền tại [Cài đặt] -> [Quyền hạn]")
                        .setPermissions(Manifest.permission.READ_CONTACTS)
                        .check();

            }
        });

        friendInvitationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FriendInvitationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addControls() {
        // init views
        friendFromContactBtn = view.findViewById(R.id.friend_from_contact_btn);
        friendInvitationBtn = view.findViewById(R.id.friend_invitation_btn);

        mAuth = FirebaseAuth.getInstance();
        listFriendId = new ArrayList<>();
        listFriendUser = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(getContext());
        friendsAdapter.setData(listFriendUser);
        recyclerView = view.findViewById(R.id.personal_contacts_rcv);
        recyclerView.setAdapter(friendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadContacts();
    }

    private void loadContacts() {
        RetrofitService.getInstance.getMeInfo(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            loadContacts();
                        } else {
                            user = response.body();
                            listFriendId = user.getContacts();
                            for (String id : listFriendId) {
                                getListFriendUser(id);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }

    private void getListFriendUser(String id) {
        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            getListFriendUser(id);
                        } else {
                            user = response.body();
                            listFriendUser.add(user);
                        }
                        friendsAdapter.setData(listFriendUser);
                        Log.e("TAG", "onResponse: " + listFriendUser );
                        recyclerView.setAdapter(friendsAdapter);

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        CustomAlert.showToast(getActivity(), CustomAlert.WARNING, t.getMessage());
                    }
                });
    }
}