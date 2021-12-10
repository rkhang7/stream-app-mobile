package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.iuh.stream.R;
import com.iuh.stream.models.User;
import com.iuh.stream.models.chat.Message;
import com.iuh.stream.utils.MyConstant;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalOptionActivity extends AppCompatActivity {
    private CircleImageView avatarIv;
    private TextView nameTv;
    private FlexboxLayout collectionsLayout;
    private User user;
    private List<Message> messageList;
    private Bundle bundle;
    private String chatId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_option);
        
        addControls();
        addEvents();
    }

    private void addEvents() {
        collectionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CollectionsActivity.class);
                intent.putExtra(MyConstant.CHAT_ID, chatId);
                startActivity(intent);
            }
        });
    }

    private void addControls() {
        // init views
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        avatarIv = findViewById(R.id.personal_image_iv);
        nameTv = findViewById(R.id.personal_name_tv);
        collectionsLayout = findViewById(R.id.collections_layout);

        bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable(MyConstant.USER_KEY);
        chatId = bundle.getString(MyConstant.CHAT_ID);
        Picasso.get().load(user.getImageURL()).into(avatarIv);
        nameTv.setText(user.getFirstName() + " " + user.getLastName());
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // allow back previous activity
        return super.onSupportNavigateUp();
    }
}