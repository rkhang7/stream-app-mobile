package com.iuh.stream.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.iuh.stream.R;
import com.iuh.stream.activity.FriendInvitationActivity;
import com.iuh.stream.activity.PhoneFriendsActivity;
import com.iuh.stream.adapter.FriendsAdapter;
import com.iuh.stream.api.UserListAsyncResponse;
import com.iuh.stream.api.UserUtil;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.Util;

import java.util.ArrayList;
import java.util.List;


public class PersonalContactFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    // views
    private View view;
    private Button friendFromContactBtn;
    private Button friendInvitationBtn;
    private List<String> listFriendId ;
    private List<User> listFriendUser;
    private FriendsAdapter friendsAdapter;
    public static ShimmerRecyclerView shimmerRecyclerView;
    private User user;
    private static final int UPDATE = 1;
    private static final int LOAD = 2;

    private SwipeRefreshLayout swipeRefreshLayout;

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
        friendFromContactBtn.setOnClickListener(view -> {
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

        });

        friendInvitationBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), FriendInvitationActivity.class);
            startActivity(intent);
        });
    }

    private void addControls() {
        // init views
        friendFromContactBtn = view.findViewById(R.id.friend_from_contact_btn);
        friendInvitationBtn = view.findViewById(R.id.friend_invitation_btn);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main));

        listFriendId = new ArrayList<>();
        listFriendUser = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(getContext());
        friendsAdapter.setData(listFriendUser);
        shimmerRecyclerView = view.findViewById(R.id.personal_contacts_rcv);
        shimmerRecyclerView.setAdapter(friendsAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmerRecyclerView.addItemDecoration(itemDecoration);
        shimmerRecyclerView.setDemoLayoutReference(R.layout.chat_list_item_demo);
        shimmerRecyclerView.showShimmerAdapter();
        swipeRefreshLayout.setOnRefreshListener(this);


        loadContact(LOAD);
    }

    private void loadContact(int type) {
        new UserUtil().getListFriend(new UserListAsyncResponse() {
            @Override
            public void processFinnish(List<User> friendArrayList) {
                listFriendUser = Util.sortListFriend(friendArrayList);
                shimmerRecyclerView.hideShimmerAdapter();
                friendsAdapter.setData(listFriendUser);
                shimmerRecyclerView.setAdapter(friendsAdapter);
                if(type == UPDATE){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        loadContact(UPDATE);
    }
}