package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;

public class StartActivity extends AppCompatActivity {
    // views
    private Button signInBtn,registerBtn;

    // firebase
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

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
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }
}