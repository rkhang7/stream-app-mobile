package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.iuh.stream.R;
import com.iuh.stream.adapter.MyFragmentAdapter;
import com.iuh.stream.fragment.FilesFragment;
import com.iuh.stream.fragment.ImagesFragment;
import com.iuh.stream.utils.MyConstant;

import java.util.Objects;

public class CollectionsActivity extends AppCompatActivity {

    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);
        addControls();
    }

    private void addControls() {
        // get chatId
        chatId = getIntent().getStringExtra(MyConstant.CHAT_ID);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // init views
        TabLayout tabLayout = findViewById(R.id.collections_tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.collections_pager2);
        MyFragmentAdapter myFragmentAdapter = new MyFragmentAdapter(this);
        myFragmentAdapter.addFragment(ImagesFragment.newInstance(chatId));
        myFragmentAdapter.addFragment(FilesFragment.newInstance(chatId));
        viewPager2.setAdapter(myFragmentAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Ảnh");
                    break;
                case 1:
                    tab.setText("File");
                    break;
            }
        }).attach();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // allow back previous activity
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}