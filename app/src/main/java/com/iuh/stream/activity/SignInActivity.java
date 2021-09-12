package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.iuh.stream.R;

public class SignInActivity extends AppCompatActivity {
    // views
    private Button singInBtn, createAccountBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        addControls();
        addEvent();
    }

    private void addEvent() {
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start Register activity
                startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
            }
        });

        singInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start Main activity
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
            }
        });
    }

    private void addControls() {
        // init view
        createAccountBtn = findViewById(R.id.create_account_btn);
        singInBtn = findViewById(R.id.sign_in_sign_in_btn);
    }
}