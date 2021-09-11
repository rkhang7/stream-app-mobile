package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.iuh.stream.R;

public class RegisterActivity extends AppCompatActivity {
    // views
    private Button signInBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        addControl();
        addEvents();
    }

    private void addEvents() {
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start sign in activity
                startActivity(new Intent(RegisterActivity.this, SignInActivity.class));
            }
        });
    }

    private void addControl() {
        // init views
        signInBtn = findViewById(R.id.register_sign_in_btn);
    }
}