package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iuh.stream.R;
import com.iuh.stream.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {
    private User user;

    // views
    private CircleImageView avtIv;
    private TextView nameTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        addControls();
    }

    private void addControls() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = (User) getIntent().getSerializableExtra(AddFriendActivity.USER_KEY);

        // init views
        avtIv = findViewById(R.id.avt_iv);
        Glide.with(this).load(user.getImageURL()).into(avtIv);

        nameTv = findViewById(R.id.name_tv);
        nameTv.setText(user.getFirstName() + " " +  user.getLastName());

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}