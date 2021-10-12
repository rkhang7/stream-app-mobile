package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.iuh.stream.R;
import com.iuh.stream.fragment.ChatFragment;
import com.iuh.stream.fragment.ContactFragment;
import com.iuh.stream.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    // views
    private FrameLayout containerFrameLayout;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();
        addEvents();
    }

    private void addEvents() {
        // handle bottom navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.chat_menu:
                        fragment = new ChatFragment();
                        break;
                    case R.id.contacts_menu:
                        fragment = new ContactFragment();
                        break;
                    case R.id.person_menu:
                        fragment = new ProfileFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();

                return true;
            }
        });
    }

    private void addControls() {
        // init views
        containerFrameLayout = findViewById(R.id.container);
        bottomNavigationView = findViewById(R.id.navigation);

        // set default fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new ChatFragment())
                .commit();

        BadgeDrawable orCreateBadge = bottomNavigationView.getOrCreateBadge(R.id.chat_menu);
        orCreateBadge.setVisible(true);
        orCreateBadge.setNumber(99);
    }
}