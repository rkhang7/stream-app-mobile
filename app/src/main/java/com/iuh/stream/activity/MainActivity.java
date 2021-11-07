package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
import com.iuh.stream.fragment.ChatFragment;
import com.iuh.stream.fragment.ContactFragment;
import com.iuh.stream.fragment.ProfileFragment;
import com.iuh.stream.fragment.SettingFragment;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    // views
    private FrameLayout containerFrameLayout;
    private BottomNavigationView bottomNavigationView;
    private ChatFragment chatFragment;
    private ProfileFragment profileFragment;
    private ContactFragment contactFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
        contactFragment = new ContactFragment();
        settingFragment = new SettingFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, settingFragment)
                .add(R.id.container, profileFragment)
                .add(R.id.container, contactFragment)
                .add(R.id.container, chatFragment)
                .hide(settingFragment)
                .hide(profileFragment)
                .hide(contactFragment)
                .show(chatFragment)
                .commit();


        SocketClient.getInstance().connect();
        addEvents();
    }

    private void addEvents() {
        bottomNavigationView = findViewById(R.id.navigation);
        // handle bottom navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.chat_menu:
                        fragment = chatFragment;
                        break;
                    case R.id.contacts_menu:
                        fragment = contactFragment;
                        break;
                    case R.id.person_menu:
                        fragment = profileFragment;
                        break;
                    case R.id.setting_menu:
                        fragment = settingFragment;
                        break;

                }

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                for(Fragment frag : getSupportFragmentManager().getFragments()) {
                    if(!frag.equals(fragment))
                        fragmentTransaction.hide(frag);
                }

                fragmentTransaction.show(fragment)
                        .commit();

                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketClient.getInstance().disconnect();
    }
}