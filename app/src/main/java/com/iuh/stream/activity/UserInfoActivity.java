package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.iuh.stream.R;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.fragment.ProfileFragment;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.Constants;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {
    // views
    private CircleImageView avatarIv;
    private TextView nameTv;
    private EditText firstNameEt,lastNameEt, dobEt, genderEt, emailEt, phoneNumberEt;
    private ImageButton backBtn;
    private ImageButton editFirstNameBtn, editLastNameBtn;
    private FlexboxLayout emailLayout, phoneNumberLayout;
    private User user;
    private TextWatcher textWatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        addControls();
        addEvents();
    }

    private void addEvents() {
        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

    }

    private void addControls() {
        // init views
        avatarIv = findViewById(R.id.user_info_avatar_iv);
        nameTv = findViewById(R.id.user_info_name_tv);
        firstNameEt = findViewById(R.id.user_info_first_name_et);
        lastNameEt = findViewById(R.id.user_info_last_name_et);
        dobEt = findViewById(R.id.user_info_dob_et);
        genderEt = findViewById(R.id.user_info_gender_et);
        emailEt = findViewById(R.id.user_info_email_et);
        phoneNumberEt = findViewById(R.id.user_info_phone_et);
        backBtn = findViewById(R.id.user_info_back_btn);
        editFirstNameBtn = findViewById(R.id.edit_first_name_btn);
        editLastNameBtn = findViewById(R.id.edit_last_name_btn);
        phoneNumberLayout = findViewById(R.id.phone_layout);
        emailLayout = findViewById(R.id.email_layout);
        loadUserInfo();

        Log.e(Constants.TAG, "addControls: " + DataLocalManager.getStringValue(Constants.ACCESS_TOKEN));
        Log.e(Constants.TAG, "addControls: " + user.get_id());
    }

    private void loadUserInfo() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(ProfileFragment.USER_KEY);
        if(user != null){
            Glide.with(this).load(user.getImageURL()).into(avatarIv);
            nameTv.setText(user.getFirstName() + " " + user.getLastName());
            firstNameEt.setText(user.getFirstName());
            lastNameEt.setText(user.getLastName());
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            dobEt.setText(simpleDateFormat.format(user.getDateOfBirth()));

            genderEt.setText(user.getGender());
            emailEt.setText(user.getEmail());
            phoneNumberEt.setText(user.getPhoneNumber());

            if(user.getPhoneNumber() != null){
                emailLayout.setVisibility(View.GONE);
                phoneNumberLayout.setVisibility(View.VISIBLE);
                phoneNumberEt.setText(user.getPhoneNumber());
            }

            else if(!user.getEmail().equals("null")){
                emailLayout.setVisibility(View.VISIBLE);
                phoneNumberLayout.setVisibility(View.GONE);
                emailEt.setText(user.getEmail());
            }

        }
    }
}