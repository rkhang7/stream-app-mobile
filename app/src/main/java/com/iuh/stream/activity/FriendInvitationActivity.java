package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.iuh.stream.R;
import com.iuh.stream.adapter.MyFragmentAdapter;
import com.iuh.stream.fragment.ListFriendInvitationReceivedFragment;
import com.iuh.stream.fragment.ListFriendInvitationSentFragment;

import java.util.Objects;


public class FriendInvitationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invitation);
        addControls();
    }

    private void addControls() {
        // set title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Lời mời kết bạn");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // views
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.pager);
        MyFragmentAdapter myFragmentAdapter = new MyFragmentAdapter(this);
        myFragmentAdapter.addFragment(new ListFriendInvitationReceivedFragment());
        myFragmentAdapter.addFragment(new ListFriendInvitationSentFragment());
        viewPager2.setAdapter(myFragmentAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("ĐÃ NHẬN");
                    break;
                case 1:
                    tab.setText("ĐÃ GỬI");
                    break;
            }
        }).attach();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // allow back previous activity
        return super.onSupportNavigateUp();
    }
}