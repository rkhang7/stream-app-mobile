package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
import com.iuh.stream.adapter.MemberAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.models.chatlist.Group;
import com.iuh.stream.models.request.RenameGroupRequest;
import com.iuh.stream.models.response.UpdateUserResponse;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupOptionActivity extends AppCompatActivity {
    // views
    private CircleImageView groupImageIv;
    private TextView groupNameTv;
    private FlexboxLayout addMemberLayout, viewMembersLayout, leaveGroupBtn, collectionsLayout;
    private NestedScrollView nestedScrollView;
    private ActionBar actionBar;
    private Bundle bundle;
    private Group group;
    private List<User> memberList;
    private MemberAdapter memberAdapter;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private String chatId;
    private ImageButton editGroupNameBtn;
    private EditText groupNameEt;
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
        viewMembersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nestedScrollView.getVisibility() == View.GONE){
                    nestedScrollView.setVisibility(View.VISIBLE);
                }
                else if(nestedScrollView.getVisibility() == View.VISIBLE){
                    nestedScrollView.setVisibility(View.GONE);
                }
            }
        });

        collectionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CollectionsActivity.class);
                intent.putExtra(MyConstant.CHAT_ID, bundle.getSerializable(MyConstant.GROUP_CHAT_ID));
                startActivity(intent);
            }
        });

        leaveGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmDialog();
            }
        });

        editGroupNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopup();
            }
        });
    }

    private void openPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_group_name_popup);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = Gravity.CENTER;

        window.setAttributes(attributes);

        dialog.setCancelable(true);

        String oldGroupName= group.getName();
        // init views
        groupNameEt = dialog.findViewById(R.id.change_group_name_et);
        Button cancelBtn = dialog.findViewById(R.id.cancel_btn);
        Button updateBtn = dialog.findViewById(R.id.update_btn);
        groupNameEt.setText(oldGroupName);




        // set up view
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newGroupName = groupNameEt.getText().toString();
                if(TextUtils.isEmpty(newGroupName)){
                    groupNameEt.setError("Tên nhóm không được rỗng");
                }
                else{
                    RenameGroupRequest renameGroupRequest = new RenameGroupRequest(chatId, newGroupName);
                    renameGroup(renameGroupRequest, dialog);

                }
            }
        });





        dialog.show();


    }

    private void renameGroup(RenameGroupRequest renameGroupRequest, Dialog dialog) {
        RetrofitService.getInstance.renameGroup(renameGroupRequest, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            renameGroup(renameGroupRequest, dialog);
                        }
                        else if(response.code() == 200){
                            groupNameTv.setText(renameGroupRequest.getName());
                            GroupChatActivity.groupNameTv.setText(renameGroupRequest.getName());
                            dialog.cancel();

                        }
                        else{
                            CustomAlert.showToast(GroupOptionActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));
                        }


                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        CustomAlert.showToast(GroupOptionActivity.this, CustomAlert.WARNING, t.getMessage());

                    }
                });


    }

    private void openConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupOptionActivity.this);
        builder.setTitle("Rời khỏi nhóm ?");
        builder.setIcon(R.drawable.icons8_warning_64px);
        builder.setMessage("Bạn có muốn rời khỏi nhóm này ?");
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                leaveGroupBtn(chatId);
            }
        });

        builder.create().show();
    }

    private void leaveGroupBtn(String chatId) {
        RetrofitService.getInstance.leaveGroup(chatId, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            leaveGroupBtn(chatId);
                        }
                        else if(response.code() == 200){
                            CustomAlert.showToast(GroupOptionActivity.this, CustomAlert.INFO, "Rời nhóm thành công");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            CustomAlert.showToast(GroupOptionActivity.this, CustomAlert.WARNING, getString(R.string.error_notification));

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        CustomAlert.showToast(GroupOptionActivity.this, CustomAlert.WARNING, t.getMessage());
                        Log.e("TAG", "onFailure: " +t.getMessage() );
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
        viewMembersLayout = findViewById(R.id.view_members_layout);
        collectionsLayout = findViewById(R.id.collections_layout);
        leaveGroupBtn = findViewById(R.id.leave_group_layout);
        nestedScrollView = findViewById(R.id.nsv);
        editGroupNameBtn = findViewById(R.id.edit_group_name_btn);

        mAuth = FirebaseAuth.getInstance();

        bundle = getIntent().getExtras();
        chatId = bundle.getString(MyConstant.GROUP_CHAT_ID);
        group = (Group) bundle.getSerializable(MyConstant.GROUP_KEY);
        groupNameTv.setText(group.getName());

        memberList = (List<User>) bundle.getSerializable(MyConstant.LIST_MEMBER_KEY);
        memberAdapter = new MemberAdapter(this, memberList, group.getAdmins(), chatId);
        recyclerView = findViewById(R.id.member_group_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(memberAdapter);

        for (int i = 0;i < memberList.size(); i++){
            if(checkIsAdmin(memberList.get(i).get_id())){
                swap(0, i);
            }
        }

    }
    private boolean checkIsAdmin(String id){
        for (String s: group.getAdmins()){
            if(id.equals(s)){
                return true;
            }
        }
        return false;
    }

    public void swap(int firstPosition, int secondPosition)
    {
        Collections.swap(memberList, firstPosition, secondPosition);
        memberAdapter.notifyItemMoved(firstPosition, secondPosition);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // allow back previous activity
        return super.onSupportNavigateUp();
    }
}