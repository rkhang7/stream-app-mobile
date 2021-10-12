package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iuh.stream.R;
import com.iuh.stream.models.User;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {
    private User user;

    // views
    private CircleImageView avtIv;
    private TextView nameTv, dobTv, genderTv, phoneNumberTv, emailTv;
    private LinearLayout emailLayout, phoneNumberLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        addControls();
    }

    private void addControls() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = (User) getIntent().getSerializableExtra(AddFriendActivity.USER_KEY);
        Log.e("TAG" , "addControls: "+ user);

        // init views
        avtIv = findViewById(R.id.avt_iv);
        Glide.with(this).load(user.getImageURL()).into(avtIv);

        nameTv = findViewById(R.id.name_tv);
        nameTv.setText(user.getFirstName() + " " +  user.getLastName());

        dobTv = findViewById(R.id.dob_tv);
        // datetime format
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        dobTv.setText(simpleDateFormat.format(user.getDateOfBirth()));

        genderTv = findViewById(R.id.gender_tv);
        genderTv.setText(user.getGender());

        phoneNumberTv = findViewById(R.id.phone_number_tv);
        emailTv = findViewById(R.id.email_tv);
        phoneNumberLayout = findViewById(R.id.phone_layout);
        emailLayout = findViewById(R.id.email_layout);
        if(user.getPhoneNumber() != null){
            emailLayout.setVisibility(View.GONE);
            phoneNumberLayout.setVisibility(View.VISIBLE);
            phoneNumberTv.setText(user.getPhoneNumber());
        }

        else if(!user.getEmail().equals("null")){
            emailLayout.setVisibility(View.VISIBLE);
            phoneNumberLayout.setVisibility(View.GONE);
            emailTv.setText(user.getEmail());
        }





    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}