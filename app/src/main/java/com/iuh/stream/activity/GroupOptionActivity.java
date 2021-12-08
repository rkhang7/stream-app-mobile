package com.iuh.stream.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.iuh.stream.R;
import com.iuh.stream.models.chatlist.Group;
import com.iuh.stream.utils.MyConstant;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupOptionActivity extends AppCompatActivity {
    // views
    private CircleImageView groupImageIv;
    private TextView groupNameTv;
    private FlexboxLayout addMemberLayout;


    private ActionBar actionBar;
    private Bundle bundle;
    private Group group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_option);

        addControls();
        addEvents();
    }

    private void addEvents() {
        addMemberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddMemberActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void addControls() {
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // intit views
        groupImageIv = findViewById(R.id.group_image_iv);
        groupNameTv = findViewById(R.id.group_name_tv);
        addMemberLayout = findViewById(R.id.add_members_layout);


        bundle = getIntent().getExtras();
        group = (Group) bundle.getSerializable(MyConstant.GROUP_KEY);
        groupNameTv.setText(group.getName());


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // allow back previous activity
        return super.onSupportNavigateUp();
    }
}