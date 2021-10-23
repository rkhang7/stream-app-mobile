package com.iuh.stream.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
import com.iuh.stream.activity.StartActivity;
import com.iuh.stream.activity.UserInfoActivity;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.models.User;
import com.iuh.stream.models.jwt.TokenResponse;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.Util;
import com.victor.loading.newton.NewtonCradleLoading;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {
    public static final String USER_KEY = ProfileFragment.class.getName();
    // views
    private CircleImageView avatar;
    private TextView nameTv;
    private View view;
    private NewtonCradleLoading newtonCradleLoading;

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

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        Intent intent = new Intent(getActivity(), StartActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }
                });

                builder.create().show();
            }
        });
    }

    private void addControls() {
        avatar = view.findViewById(R.id.profile_avatar_iv);
        nameTv = view.findViewById(R.id.profile_name_tv);
        newtonCradleLoading = view.findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.setLoadingColor(R.color.main);

        getUserInfo();
        mAuth = FirebaseAuth.getInstance();
        logoutBtn = view.findViewById(R.id.profile_logout_btn);
    }

    private void getUserInfo() {
        RetrofitService.getInstance.getMeInfo(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
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
                        Glide.with(getContext()).load(user.getImageURL()).into(avatar);
                        nameTv.setText(user.getFirstName() + " " + user.getFirstName());
                    }
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}