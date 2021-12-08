package com.iuh.stream.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.iuh.stream.R;
import com.iuh.stream.adapter.MemberAdapter;
import com.iuh.stream.models.User;
import com.iuh.stream.models.chatlist.Group;
import com.iuh.stream.utils.MyConstant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupOptionActivity extends AppCompatActivity {
    // views
    private CircleImageView groupImageIv;
    private TextView groupNameTv;
    private FlexboxLayout addMemberLayout, viewMembersLayout;
    private NestedScrollView nestedScrollView;


    private ActionBar actionBar;
    private Bundle bundle;
    private Group group;
    private List<User> memberList;
    private MemberAdapter memberAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_option);

        addControls();
        addEvents();
    }

    private void addEvents() {
        addMemberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddMemberActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        viewMembersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nestedScrollView.getVisibility() == View.GONE){
                    nestedScrollView.setVisibility(View.VISIBLE);
                }
                else if(nestedScrollView.getVisibility() == View.VISIBLE){
                    nestedScrollView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void addControls() {
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // intit views
        groupImageIv = findViewById(R.id.group_image_iv);
        groupNameTv = findViewById(R.id.group_name_tv);
        addMemberLayout = findViewById(R.id.add_members_layout);
        viewMembersLayout = findViewById(R.id.view_members_layout);
        nestedScrollView = findViewById(R.id.nsv);



        bundle = getIntent().getExtras();
        group = (Group) bundle.getSerializable(MyConstant.GROUP_KEY);
        groupNameTv.setText(group.getName());

        memberList = (List<User>) bundle.getSerializable(MyConstant.LIST_MEMBER_KEY);
        memberAdapter = new MemberAdapter(this, memberList, group.getAdmins());
        recyclerView = findViewById(R.id.member_group_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(memberAdapter);

        for (int i = 0;i < memberList.size(); i++){
            if(checkIsAdmin(memberList.get(i).get_id())){
                swap(0, i);
            }
        }

    }
    private boolean checkIsAdmin(String id){
        for (String s: group.getAdmins()){
            if(id.equals(s)){
                return true;
            }
        }
        return false;
    }

    public void swap(int firstPosition, int secondPosition)
    {
        Collections.swap(memberList, firstPosition, secondPosition);
        memberAdapter.notifyItemMoved(firstPosition, secondPosition);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // allow back previous activity
        return super.onSupportNavigateUp();
    }
}