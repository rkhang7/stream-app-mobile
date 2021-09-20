package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.iuh.stream.R;

public class RegisterPhoneNumberActivity extends AppCompatActivity {
    // views
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone_number);

        addControls();
    }

    private void addControls() {
        // init views
        getSupportActionBar().setTitle("Đăng kí bằng số điện thoại");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


}