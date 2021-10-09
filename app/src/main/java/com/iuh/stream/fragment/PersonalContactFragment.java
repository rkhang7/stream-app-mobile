package com.iuh.stream.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.iuh.stream.R;
import com.iuh.stream.activity.FriendInvitationActivity;
import com.iuh.stream.activity.PhoneFriendsActivity;

import java.util.List;


public class PersonalContactFragment extends Fragment {
    // views
    private View view;
    private Button friendFromContactBtn;
    private Button friendInvitationBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view  = inflater.inflate(R.layout.fragment_personal_contact, container, false);

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
    }
}