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
    private Button signInPhoneNumberBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        addControls();
        addEvents();
    }

    private void addEvents() {
        signInPhoneNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start SignInPhoneNumberActivity

                startActivity(new Intent(SignInActivity.this, SignInPhoneNumberActivity.class));
            }
        });
    }

    private void addControls() {
        // set title
        getSupportActionBar().setTitle("Chọn phương thức đăng nhập");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // init views
        signInPhoneNumberBtn =  findViewById(R.id.sign_in_phone_number_btn);
    }




}