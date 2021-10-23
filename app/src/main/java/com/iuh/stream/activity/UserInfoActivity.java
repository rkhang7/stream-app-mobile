package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iuh.stream.R;
import com.iuh.stream.fragment.ProfileFragment;
import com.iuh.stream.models.User;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {
    // views
    private CircleImageView avatarIv;
    private TextView nameTv;
    private EditText nameEt, dobEt, genderEt, emailEt, phoneNumberEt;
    private ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        addControls();
        addEvents();
    }

    private void addEvents() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    private void addControls() {
        avatarIv = findViewById(R.id.user_info_avatar_iv);
        nameTv = findViewById(R.id.user_info_name_tv);
        nameEt = findViewById(R.id.user_info_name_et);
        dobEt = findViewById(R.id.user_info_dob_et);
        genderEt = findViewById(R.id.user_info_gender_et);
        emailEt = findViewById(R.id.user_info_email_et);
        phoneNumberEt = findViewById(R.id.user_info_phone_et);
        backBtn = findViewById(R.id.user_info_back_btn);
        loadUserInfo();
    }

    private void loadUserInfo() {
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra(ProfileFragment.USER_KEY);
        if(user != null){
            Glide.with(this).load(user.getImageURL()).into(avatarIv);
            nameTv.setText(user.getFirstName() + " " + user.getLastName());
            nameEt.setText(user.getFirstName() + " " + user.getLastName());
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            dobEt.setText(simpleDateFormat.format(user.getDateOfBirth()));

            genderEt.setText(user.getGender());
            emailEt.setText(user.getEmail());
            phoneNumberEt.setText(user.getPhoneNumber());

        }
    }
}