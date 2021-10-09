package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.iuh.stream.R;
import com.iuh.stream.adapter.MyFragmentAdapter;
import com.iuh.stream.fragment.GroupContactFragment;
import com.iuh.stream.fragment.ListFriendInvitationReceivedFragment;
import com.iuh.stream.fragment.ListFriendInvitationSentFragment;
import com.iuh.stream.fragment.PersonalContactFragment;

public class FriendInvitationActivity extends AppCompatActivity {
    // views
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private MyFragmentAdapter myFragmentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invitation);
        addControls();
    }

    private void addControls() {
        // set title
        getSupportActionBar().setTitle("Lời mời kết bạn");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.pager);
        myFragmentAdapter = new MyFragmentAdapter(this);
        myFragmentAdapter.addFragment(new ListFriendInvitationReceivedFragment());
        myFragmentAdapter.addFragment(new ListFriendInvitationSentFragment());
        viewPager2.setAdapter(myFragmentAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("ĐÃ NHẬN");
                        break;
                    case 1:
                        tab.setText("ĐÃ GỬI");
                        break;
                }
            }
        }).attach();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // allow back previous activity
        return super.onSupportNavigateUp();
    }
}