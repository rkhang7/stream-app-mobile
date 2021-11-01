package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.iuh.stream.R;

import java.util.List;

public class StartActivity extends AppCompatActivity {
    // views
    private Button signInBtn,registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance());
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());

        FirebaseAuth.getInstance().setLanguageCode("vi");

        addControls();
        addEvents();

    }

    private void addEvents() {
        signInBtn.setOnClickListener(v -> {
            // start Sign In activity
            startActivity(new Intent(StartActivity.this, SignInActivity.class));
        });

        registerBtn.setOnClickListener(v -> {
            // start Register activity
            startActivity(new Intent(StartActivity.this, RegisterActivity.class));
        });
    }

    private void addControls() {
        // init views
        signInBtn = findViewById(R.id.sign_in_btn);
        registerBtn = findViewById(R.id.register_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            UserInfo userInfo = mAuth.getCurrentUser().getProviderData().get(1);
            if(!userInfo.getProviderId().equals("password") || mAuth.getCurrentUser().isEmailVerified()) {
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                finish();
            }
        }
    }
}