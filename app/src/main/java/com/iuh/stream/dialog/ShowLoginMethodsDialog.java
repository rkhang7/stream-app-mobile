package com.iuh.stream.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.iuh.stream.R;
import com.iuh.stream.activity.MainActivity;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ShowLoginMethodsDialog extends Dialog {
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String phoneNumber84;
    private TextInputLayout layoutPhone;
    private final FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressBar pbPhone;
    private EditText edtOtpCode;
    public static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    public static ProgressBar signInGoogleProgressBar;

    public ShowLoginMethodsDialog(Activity activity) {
        super(activity);
        setOwnerActivity(activity);
        setContentView(R.layout.show_login_method_dialog);

        mAuth = FirebaseAuth.getInstance();
        signInGoogleProgressBar = findViewById(R.id.sign_in_google_pb);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            dismiss();
        });

        findViewById(R.id.btnLoginWithGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogleProgressBar.setVisibility(View.VISIBLE);
                configGoogleSignIn(activity);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                activity.startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });



        loginPhonePart();




    }

    private void configGoogleSignIn(Activity activity) {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString((R.string.default_web_client_id)))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getOwnerActivity(), gso);
    }

    private void loginPhonePart() {
        LinearLayout lnByPhone = findViewById(R.id.byPhone);
        LinearLayout lnEnterPhone = findViewById(R.id.lnEnterPhone);
        LinearLayout lnVerifyPhone = findViewById(R.id.lnVerifyPhone);
        ConstraintLayout csMethods = findViewById(R.id.methods);

        TextInputEditText edtPhone = findViewById(R.id.phoneInput);
        layoutPhone = findViewById(R.id.phone_number_input);
        pbPhone = findViewById(R.id.pbPhone);
        LinearLayout lnCountdown = findViewById(R.id.count_down_layout);
        LinearLayout lnResend = findViewById(R.id.resend_layout);
        TextView tvCountdown = findViewById(R.id.count_down_tv);
        TextView tvPhoneNumber = findViewById(R.id.phone_number_tv);

        edtOtpCode = findViewById(R.id.edtOtpCode);

        findViewById(R.id.btnLoginWithPhone).setOnClickListener(v -> {
            lnByPhone.setVisibility(View.VISIBLE);
            csMethods.setVisibility(View.GONE);
        });

        findViewById(R.id.btnLoginByPhone).setOnClickListener(v -> {
            String phoneNumber = Objects.requireNonNull(edtPhone.getText()).toString().trim();
            if (checkValidPhoneNumber(phoneNumber)) {

                pbPhone.setVisibility(View.VISIBLE);

                // convert phone number to +84 phone number
                phoneNumber84 = phoneNumber.replaceFirst("0", "+84");

                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber84)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(getOwnerActivity())
                        .setCallbacks(mCallback)
                        .build();

                //Verify the phone number, result can get in mCallback
                PhoneAuthProvider.verifyPhoneNumber(options);

            }
        });

        //When user enter confirm otp/ login by thí phone
        findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            String code = edtOtpCode.getText().toString().trim();

            if(code.length() == 6) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

                pbPhone.setVisibility(View.VISIBLE);
                signInWithPhoneAuthCredential(credential);
            } else {
                Toast.makeText(getOwnerActivity(), "Mã xác nhận gồm 6 chữ số!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.resend_btn).setOnClickListener(v -> {
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phoneNumber84)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(getOwnerActivity())
                    .setCallbacks(mCallback)
                    .setForceResendingToken(mResendToken)
                    .build();

            //Verify the phone number, result can get in mCallback
            PhoneAuthProvider.verifyPhoneNumber(options);

            pbPhone.setVisibility(View.VISIBLE);
            lnCountdown.setVisibility(View.VISIBLE);
            lnResend.setVisibility(View.GONE);
        });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d("AA", "onVerificationCompleted: ");
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pbPhone.setVisibility(View.INVISIBLE);
                Log.e("CE", "onVerificationFailed: ", e);
                Toast.makeText(getOwnerActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

                //Move to verify
                lnEnterPhone.setVisibility(View.GONE);
                lnVerifyPhone.setVisibility(View.VISIBLE);
                pbPhone.setVisibility(View.GONE);
                tvPhoneNumber.setText(phoneNumber84);

                int seconds = 60;
                new CountDownTimer(seconds * 1000,1000){
                    @Override
                    public void onTick(long l) {
                        tvCountdown.setText(String.valueOf(l / 1000));
                    }

                    @Override
                    public void onFinish() {
                        lnCountdown.setVisibility(View.GONE);
                        lnResend.setVisibility(View.VISIBLE);
                    }
                }.start();
            }

        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(getOwnerActivity(), task -> {
                    pbPhone.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                            //User's not exist
                            Toast.makeText(getOwnerActivity(), "Số điện thoại chưa được đăng ký, vui lòng thử lại!", Toast.LENGTH_LONG).show();
                            dismiss();
                        } else {
                            Toast.makeText(getOwnerActivity(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getOwnerActivity(), MainActivity.class);
                            getOwnerActivity().startActivity(intent);
                        }

                    } else {
                        // Sign in failed, display a message and update the UI

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(getOwnerActivity(), "Mã không hợp lệ, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> pbPhone.setVisibility(View.INVISIBLE));

    }

    private boolean checkValidPhoneNumber(String phoneNumber) {

        if (TextUtils.isEmpty(phoneNumber)) {
            layoutPhone.setHelperText("Số điện thoại không được rỗng");
            layoutPhone.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            return false;
        }

        if (phoneNumber.charAt(0) != '0' || phoneNumber.length() < 10) {
            layoutPhone.setHelperText("Số điện thoại phải bắt đầu bằng 0 và gồm 10 số");
            layoutPhone.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            return false;
        }

        layoutPhone.setHelperText("");

        return true;
    }



}
