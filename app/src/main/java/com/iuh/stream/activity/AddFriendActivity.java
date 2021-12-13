package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.iuh.stream.R;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.Util;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendActivity extends AppCompatActivity {
    //views
    private Button addFriendFromContactsBtn;
    private Button searchBtn;
    private EditText editText;
    private TextView phoneErrorTv;
    private TextView emailErrorTv;
    public static final String USER_KEY = AddFriendActivity.class.getName();

    // firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        addControls();
        addEvents();
    }

    private void addEvents() {
        addFriendFromContactsBtn.setOnClickListener(view -> {
            PermissionListener permissionlistener = new PermissionListener() {

                @Override
                public void onPermissionGranted() {
                    Intent intent = new Intent(AddFriendActivity.this, PhoneFriendsActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {

                }
            };

            TedPermission.create()
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("Nếu bạn không cấp quyền, bạn sẽ không thể sử dụng dịch vụ này\n\nVui lòng cấp quyền tại [Cài đặt] -> [Quyền hạn]")
                    .setPermissions(Manifest.permission.READ_CONTACTS)
                    .check();
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchBtn.setEnabled(!TextUtils.isEmpty(editable.toString()));
            }
        });

        searchBtn.setOnClickListener(view -> {
            String keyword = editText.getText().toString();
            if(Patterns.EMAIL_ADDRESS.matcher(keyword).matches()){
                phoneErrorTv.setVisibility(View.GONE);
                emailErrorTv.setVisibility(View.GONE);
                String ACCESS_TOKEN = DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN);
                findUserByEmail(keyword, ACCESS_TOKEN);
            }

            else if(Patterns.PHONE.matcher(keyword).matches()){
                if(keyword.length() == 10 && keyword.charAt(0) == '0'){
                    phoneErrorTv.setVisibility(View.GONE);
                    String ACCESS_TOKEN = DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN);
                    findUserByPhoneNumber(keyword, ACCESS_TOKEN);
                }
                else{
                    phoneErrorTv.setVisibility(View.VISIBLE);
                }
                emailErrorTv.setVisibility(View.GONE);
            }

            else{
                phoneErrorTv.setVisibility(View.GONE);
                emailErrorTv.setVisibility(View.VISIBLE);
            }
        });

    }

    private void findUserByPhoneNumber(String keyword, String accessToken) {
        RetrofitService.getInstance.getUserByPhoneNumber(keyword, accessToken).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.code() == 403){
                    Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                    findUserByPhoneNumber(keyword, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
                }
                else {
                    User user = response.body();
                    if(user != null){
                        if(!Objects.requireNonNull(mAuth.getCurrentUser()).getUid().equals(user.get_id())){
                            Intent intent = new Intent(AddFriendActivity.this, FriendProfileActivity.class);
                            intent.putExtra(USER_KEY, user);
                            startActivity(intent);
                        }
                        else{
                            CustomAlert.showToast(AddFriendActivity.this, CustomAlert.INFO, "Đây là số điện thoại của bạn");
                        }
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendActivity.this);
                        builder.setTitle("Thông báo");
                        builder.setMessage("Số điện thoại này chưa kích hoạt Stream.");
                        builder.setNegativeButton("Đóng", (dialogInterface, i) -> dialogInterface.cancel());
                        builder.create().show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

            }
        });
    }


    private void findUserByEmail(String keyword, String accessToken) {
        RetrofitService.getInstance.getUserByEmail(keyword, accessToken).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.code() == 403){

                    Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                    findUserByPhoneNumber(keyword, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
                }
                else {
                    User user = response.body();
                    if(user != null){
                        if(!Objects.requireNonNull(mAuth.getCurrentUser()).getUid().equals(user.get_id())){
                            Intent intent = new Intent(AddFriendActivity.this, FriendProfileActivity.class);
                            intent.putExtra(USER_KEY, user);
                            startActivity(intent);
                        }
                        else{
                            CustomAlert.showToast(AddFriendActivity.this, CustomAlert.INFO, "Đây là email của bạn");
                        }

                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendActivity.this);
                        builder.setTitle("Thông báo");
                        builder.setMessage("Email này chưa kích hoạt Stream.");
                        builder.setNegativeButton("Đóng", (dialogInterface, i) -> dialogInterface.cancel());

                        builder.create().show();
                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addControls() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // init views
        addFriendFromContactsBtn = findViewById(R.id.add_friend_from_contacts_btn);
        searchBtn = findViewById(R.id.search_users_btn);
        searchBtn.setEnabled(false);
        editText = findViewById(R.id.phone_or_email_et);
        emailErrorTv = findViewById(R.id.email_error_tv);
        phoneErrorTv = findViewById(R.id.phone_error_tv);

        // init firebase
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    // hide keyboard
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}