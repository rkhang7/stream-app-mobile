package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.iuh.stream.R;
import com.iuh.stream.dialog.DatePickerDialog;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    private LinearLayout byPhone;
    private LinearLayout byEmail;

    // firebase
    private FirebaseAuth mAuth;

    // phone number set to sendTP;
    private String phoneNumber84;
    private TextInputLayout phoneNumberInput;
    private ProgressBar pbPhone;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    //Verify comps
    private EditText edtOtpCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // views
        TabLayout registerMethodTab = findViewById(R.id.registerMethodTab);
        byPhone = findViewById(R.id.byPhone);
        byEmail = findViewById(R.id.byEmail);
        EditText edtBirthdate = findViewById(R.id.txtBirthdate);

        findViewById(R.id.btnToLogin).setOnClickListener(v -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        });

        registerMethodTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    byPhone.setVisibility(View.VISIBLE);
                } else {
                    byEmail.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    byPhone.setVisibility(View.GONE);
                } else {
                    byEmail.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    byPhone.setVisibility(View.VISIBLE);
                } else {
                    byEmail.setVisibility(View.VISIBLE);
                }
            }
        });

        addByPhone();
        addByEmail();

        findViewById(R.id.btnPickDate).setOnClickListener(v -> new DatePickerDialog(this, edtBirthdate).show());
    }

    private void addByPhone() {
        TextInputEditText edtPhone = findViewById(R.id.phoneInput);
        pbPhone = findViewById(R.id.pbPhoneRegister);
        phoneNumberInput = findViewById(R.id.phone_number_input);

        edtOtpCode = findViewById(R.id.edtOtpCode);

        LinearLayout lnEnterPhone = findViewById(R.id.lnEnterPhone);
        LinearLayout lnVerifyPhone = findViewById(R.id.lnVerifyPhone);
        LinearLayout lnCountdown = findViewById(R.id.count_down_layout);
        LinearLayout lnResend = findViewById(R.id.resend_layout);
        TextView tvCountdown = findViewById(R.id.count_down_tv);
        TextView tvPhoneNumber = findViewById(R.id.phone_number_tv);

        findViewById(R.id.btnRegisterByPhone).setOnClickListener(v -> {
            String phoneNumber = Objects.requireNonNull(edtPhone.getText()).toString().trim();
            if (checkValidPhoneNumber(phoneNumber)) {

                pbPhone.setVisibility(View.VISIBLE);

                // convert phone number to +84 phone number
                phoneNumber84 = phoneNumber.replaceFirst("0", "+84");

                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber84)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallback)
                        .build();

                //Verify the phone number, result can get in mCallback
                PhoneAuthProvider.verifyPhoneNumber(options);

            }
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
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

                //Move to verify
                lnEnterPhone.setVisibility(View.GONE);
                lnVerifyPhone.setVisibility(View.VISIBLE);
                pbPhone.setVisibility(View.GONE);

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

        //When user enter confirm otp/ login by thí phone
        findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            String code = edtOtpCode.getText().toString().trim();

            if(code.length() == 6) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

                pbPhone.setVisibility(View.VISIBLE);
                signInWithPhoneAuthCredential(credential);
            } else {
                Toast.makeText(this, "Mã xác nhận gồm 6 chữ số!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.resend_btn).setOnClickListener(v -> {
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phoneNumber84)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(mCallback)
                    .setForceResendingToken(mResendToken)
                    .build();

            //Verify the phone number, result can get in mCallback
            PhoneAuthProvider.verifyPhoneNumber(options);

            pbPhone.setVisibility(View.VISIBLE);
            lnCountdown.setVisibility(View.VISIBLE);
            lnResend.setVisibility(View.GONE);
        });
    }

    private boolean checkValidPhoneNumber(String phoneNumber) {

        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberInput.setHelperText("Số điện thoại không được rỗng");
            phoneNumberInput.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            return false;
        }

        if (phoneNumber.charAt(0) != '0' || phoneNumber.length() < 10) {
            phoneNumberInput.setHelperText("Số điện thoại phải bắt đầu bằng 0 và gồm 10 số");
            phoneNumberInput.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            return false;
        }

        phoneNumberInput.setHelperText("");

        return true;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, task -> {
                    pbPhone.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        if(!task.getResult().getAdditionalUserInfo().isNewUser()) {
                            //User already exist => Register fail
                            Toast.makeText(this, "Số điện thoại đã được sử dụng, vui lòng dùng số khác!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        }

                    } else {
                        // Sign in failed, display a message and update the UI

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(this, "Mã không hợp lệ, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> pbPhone.setVisibility(View.INVISIBLE));

    }

    private void addByEmail() {
        TextInputLayout layoutEmail = findViewById(R.id.email);
        TextInputLayout layoutPassword = findViewById(R.id.pass);
        TextInputLayout layoutRePassword = findViewById(R.id.rePass);
        TextInputEditText edtEmail = findViewById(R.id.txtEmail);
        TextInputEditText edtPassword = findViewById(R.id.txtPassword);
        TextInputEditText edtRePassword = findViewById(R.id.txtRePassword);
        ProgressBar pbEmailRegister = findViewById(R.id.pbEmailRegister);

        findViewById(R.id.btnRegisterByEmail).setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String rePassword = edtRePassword.getText().toString().trim();

            layoutEmail.setHelperText("");
            layoutPassword.setHelperText("");
            layoutRePassword.setHelperText("");

            boolean flag = true;

            if(!email.matches("^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$")) {
                layoutEmail.setHelperText("Email không hợp lệ!");
                flag = false;
            }
            if(password.length() < 6) {
                layoutPassword.setHelperText("Mật khẩu không hợp lệ!");
                flag = false;
            }
            if(!password.equals(rePassword)) {
                layoutRePassword.setHelperText("Mật khẩu không khớp!");
                flag = false;
            }

            if (!flag)
                return;
            else  {
                pbEmailRegister.setVisibility(View.VISIBLE);
                //Register account
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            pbEmailRegister.setVisibility(View.GONE);
                            if(task.isSuccessful()) {
                                Toast.makeText(this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, SignInActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, "Không thể tạo tài khoản, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            pbEmailRegister.setVisibility(View.GONE);
                            Log.e("CE", "create by email: ", e);
                            Toast.makeText(this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                        });

            }
        });
    }
}