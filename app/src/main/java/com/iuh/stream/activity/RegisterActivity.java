package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.iuh.stream.R;

public class RegisterActivity extends AppCompatActivity {
    // views
    private Button registerWithPhoneNumberBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        addControls();
        addEvents();

    }

    private void addEvents() {
        registerWithPhoneNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start RegisterPhoneNumberActivity
                startActivity(new Intent(RegisterActivity.this, RegisterPhoneNumberActivity.class));
            }
        });
    }

    private void addControls() {
        // inti views
        registerWithPhoneNumberBtn = findViewById(R.id.register_with_phone_number_btn);
    }


}