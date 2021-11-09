package com.iuh.stream.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iuh.stream.R;
import com.iuh.stream.activity.StartActivity;
import com.iuh.stream.activity.UserInfoActivity;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;
import com.victor.loading.newton.NewtonCradleLoading;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {
    public static final String USER_KEY = ProfileFragment.class.getName();
    // views
    private CircleImageView avatar;
    private TextView nameTv, phoneTv, emailTv;
    private View view;
    private NewtonCradleLoading newtonCradleLoading;
    private FlexboxLayout deleteUserLayout, signInMethodLayout;
    private LinearLayout passwordLayout, phoneLayout, googleLayout;
    private Button changePasswordBtn;
    private TextInputLayout oldPasswordLayout, newPasswordLayout, confirmNewPasswordLayout;
    private TextInputEditText oldPasswordEt, newPasswordEt, confirmNewPasswordLEt;

    // firebase
    private FirebaseAuth mAuth;
    private ImageButton logoutBtn;
    private User user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_profile, container, false);
        addControls();
        addEvents();
        return view;
    }

    private void addEvents() {
        view.findViewById(R.id.showInfo).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserInfoActivity.class);
            intent.putExtra(USER_KEY, user);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Đăng xuất?");
            builder.setIcon(R.drawable.icons8_warning_64px);
            builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    signOut();
                }
            });

            builder.create().show();
        });

        // delete user
        deleteUserLayout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setIcon(R.drawable.icons8_error_60);
            builder.setTitle("Xóa tài khoản sẽ: ");
            builder.setMessage("Không thể khôi phục dữ liệu khi xóa tài khoản");
            builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String accessToken = DataLocalManager.getStringValue(Constants.ACCESS_TOKEN);
                    deleteMe(accessToken);
                }
            });

            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        });

        signInMethodLayout.setOnClickListener(v -> {
            String method = mAuth.getCurrentUser().getProviderData().get(1).getProviderId();
            switch (method) {
                case "password":
                    if (passwordLayout.getVisibility() == View.GONE) {
                        passwordLayout.setVisibility(View.VISIBLE);
                    } else {
                        passwordLayout.setVisibility(View.GONE);
                    }

                    break;
                case "phone":
                    if (phoneLayout.getVisibility() == View.GONE) {
                        String phone = mAuth.getCurrentUser().getPhoneNumber();
                        phoneTv.setText(String.format("%s%s", getString(R.string.phone_number), phone));
                        phoneLayout.setVisibility(View.VISIBLE);

                    } else {
                        phoneLayout.setVisibility(View.GONE);
                    }
                    break;
                case "google.com":
                    if (googleLayout.getVisibility() == View.GONE) {
                        String email = mAuth.getCurrentUser().getEmail();
                        emailTv.setText(String.format("Tài khoản Google: %s", email));
                        googleLayout.setVisibility(View.VISIBLE);
                    } else {
                        googleLayout.setVisibility(View.GONE);
                    }
                    break;
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = Objects.requireNonNull(oldPasswordEt.getText()).toString().trim();
                String newPassword = Objects.requireNonNull(newPasswordEt.getText()).toString().trim();
                String confirmNewPassword =confirmNewPasswordLEt.getText().toString().trim();
                changePassword(oldPassword, newPassword, confirmNewPassword);
            }
        });


    }

    private void changePassword(String oldPassword, String newPassword, String confirmNewPassword) {
        if(oldPassword.equals(DataLocalManager.getStringValue(Constants.PASSWORD))){
            oldPasswordLayout.setHelperText("");
            if(newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$")){
                newPasswordLayout.setHelperText("");
                if(!oldPassword.equals(newPassword)){
                    newPasswordLayout.setHelperText("");
                    if(newPassword.equals(confirmNewPassword)){
                        confirmNewPasswordLayout.setHelperText("");

                        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
                        String email = null;
                        if (user != null) {
                            email = user.getEmail();
                        }
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(email, oldPassword);
                        if (user != null) {
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    CustomAlert.showToast(getActivity(), CustomAlert.INFO, "Đổi mật khẩu thành công");
                                                    //save password
                                                    DataLocalManager.putStringValue(Constants.PASSWORD, newPassword);
                                                    passwordLayout.setVisibility(View.GONE);
                                                    oldPasswordEt.setText("");
                                                    newPasswordEt.setText("");
                                                    confirmNewPasswordLEt.setText("");
                                                }
                                            }
                                        }).addOnFailureListener(e -> CustomAlert.showToast(getActivity(), CustomAlert.WARNING, getString(R.string.error_notification)));
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    CustomAlert.showToast(getActivity(), CustomAlert.WARNING, getString(R.string.error_notification));

                                }
                            });
                        }
                    }
                    else{
                        confirmNewPasswordLayout.setHelperText("Mật khẩu không khớp");
                        confirmNewPasswordLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED));
                    }
                }
                else{
                    newPasswordLayout.setHelperText("Mật khẩu mới không được trùng với mật khẩu cũ");
                    newPasswordLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED));
                    return;
                }
            }
            else {
                newPasswordLayout.setHelperText("Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường và 1 số, tối thiểu 6 ký tự");
                newPasswordLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED));
                return;
            }

        }
        else{
            oldPasswordLayout.setHelperText("Mật khẩu cũ không đúng");
            oldPasswordLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            return;
        }
    }

    private void deleteMe(String accessToken) {
        RetrofitService.getInstance.deleteMe(accessToken)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN));
                            deleteMe(accessToken);
                        }
                        else if(response.code() == 401){
                            CustomAlert.showToast(getActivity(), CustomAlert.WARNING, getString(R.string.error_notification));
                        }
                        else if(response.code() == 200){
                            signOut();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        CustomAlert.showToast(getActivity(), CustomAlert.WARNING, t.getMessage());
                    }
                });
    }
    private void signOut(){
        //Sign out firebase account
        FirebaseAuth.getInstance().signOut();
        //SignOut google account
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString((R.string.default_client_id)))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mGoogleSignInClient.signOut();

        //Go to Start activity
        SocketClient.getInstance().disconnect();
        Intent intent = new Intent(getActivity(), StartActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    private void addControls() {
        // init views
        avatar = view.findViewById(R.id.profile_avatar_iv);
        nameTv = view.findViewById(R.id.profile_name_tv);
        phoneTv = view.findViewById(R.id.phone_number_tv);
        emailTv = view.findViewById(R.id.email_tv);
        newtonCradleLoading = view.findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.setLoadingColor(R.color.main);
        deleteUserLayout = view.findViewById(R.id.delete_user_layout);
        signInMethodLayout = view.findViewById(R.id.sign_in_method_layout);
        passwordLayout = view.findViewById(R.id.password_layout);
        phoneLayout = view.findViewById(R.id.phone_layout);
        googleLayout = view.findViewById(R.id.google_layout);

        changePasswordBtn = view.findViewById(R.id.change_password_btn);
        oldPasswordLayout = view.findViewById(R.id.old_password_layout);
        newPasswordLayout = view.findViewById(R.id.new_password_layout);
        confirmNewPasswordLayout = view.findViewById(R.id.confirm_new_password_layout);
        oldPasswordEt = view.findViewById(R.id.old_password_et);
        newPasswordEt = view.findViewById(R.id.new_password_et);
        confirmNewPasswordLEt = view.findViewById(R.id.confirm_new_password_et);

        getUserInfo();
        mAuth = FirebaseAuth.getInstance();
        logoutBtn = view.findViewById(R.id.profile_logout_btn);
    }

    private void getUserInfo() {
        RetrofitService.getInstance.getMeInfo(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.code() == 403){
                    newtonCradleLoading.setVisibility(View.VISIBLE);
                    newtonCradleLoading.start();
                    String REFRESH_TOKEN = DataLocalManager.getStringValue(Constants.REFRESH_TOKEN);
                    Util.refreshToken(REFRESH_TOKEN);
                    getUserInfo();
                }
                else {
                    newtonCradleLoading.setVisibility(View.GONE);
                    user = response.body();
                    if(user != null){
                        // set info
                        Picasso.get().load(user.getImageURL()).into(avatar);
                        nameTv.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(ProfileFragment.this.getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}