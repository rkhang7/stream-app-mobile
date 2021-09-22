package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iuh.stream.R;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SignInPhoneNumberActivity extends AppCompatActivity {
    // views
    private Button sendOtpBtn;
    private EditText phoneNumberEt;
    private TextInputLayout phoneNumberInput;
    private ProgressBar progressBar;


    // firebase
    private FirebaseAuth mAuth;

    // phone number set to sendTP;
    private String phoneNumber84;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_phone_number);

        addControls();
        addEvents();
    }

    private void addEvents() {

        sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneNumberEt.getText().toString();
                if (checkValidPhoneNumber(phoneNumber)) {
                    progressBar.setVisibility(View.VISIBLE);

                    // convert phone number to +84 phone number
                    phoneNumber84 = phoneNumber.replaceFirst("0", "+84");
                    checkForPhoneNumber(phoneNumber84);
                }
            }
        });

    }

    private void checkForPhoneNumber(String phoneNumber84) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Phones");
        databaseReference.orderByChild("phone").equalTo(phoneNumber84).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Số điện thoại chưa được đăng ký", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber84,
                            60,
                            TimeUnit.SECONDS,
                            SignInPhoneNumberActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    signInWithPhoneAuthCredential(phoneAuthCredential, phoneNumber84);
                                }


                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(SignInPhoneNumberActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(SignInPhoneNumberActivity.this, VerifyActivity.class);
                                    intent.putExtra("phone_number", phoneNumber84);
                                    intent.putExtra("verificationId", s);
                                    startActivity(intent);
                                }

                            }
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential, String phoneNumber84) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressBar.setVisibility(View.INVISIBLE);

                            FirebaseUser firebaseUser = task.getResult().getUser();


                            // start verify activity
                            Intent intent = new Intent(SignInPhoneNumberActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);


                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });

    }

    private void addControls() {
        // set title for action bar
        getSupportActionBar().setTitle("Đăng nhập bằng số điện thoại");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // init views
        sendOtpBtn = findViewById(R.id.send_otp_btn);
        phoneNumberEt = findViewById(R.id.sign_in_phone_number_et);
        phoneNumberInput = findViewById(R.id.sign_in_phone_number_input);
        progressBar = findViewById(R.id.sign_in_phone_number_pb);


        // init firebase
        mAuth = FirebaseAuth.getInstance();


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
}