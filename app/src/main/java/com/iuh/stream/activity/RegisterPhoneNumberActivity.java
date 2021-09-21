package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.iuh.stream.R;

public class RegisterPhoneNumberActivity extends AppCompatActivity {
    // views
    private Button nextBtn;
    private EditText phoneNumberEt;
    private TextInputLayout phoneNumberInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone_number);

        addControls();
        addEvents();
    }

    private void addEvents() {
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start verify activity
                startActivity(new Intent(RegisterPhoneNumberActivity.this, VerifyActivity.class));
            }
        });
    }

    private void addControls() {

        getSupportActionBar().setTitle("Đăng kí bằng số điện thoại");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // init views
        nextBtn = findViewById(R.id.register_phone_number_next_btn);
        phoneNumberEt = findViewById(R.id.register_phone_number_et);
        phoneNumberInput = findViewById(R.id.phone_number_input);

    }


}