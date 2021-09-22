package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iuh.stream.R;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {
    // views
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private TextView countDownTv, phoneNumberTv;
    private LinearLayout countDownLayout;
    private LinearLayout resendLayout;
    private Button resendBtn, confirmBtn;
    private ProgressBar progressBar;


    // firebase
    private String verificationID = "";
    private  PhoneAuthProvider.ForceResendingToken mForceResendingToken;
    private FirebaseAuth mAuth;



    // number of seconds count down
    private int seconds = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        addControls();
        addEvents();
    }

    private void addEvents() {
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                handleResend();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleVerifyOTP();
            }
        });
    }

    private void handleResend() {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                getIntent().getStringExtra("phone_number"),
                60L,
                TimeUnit.SECONDS,
                VerifyActivity.this,

                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressBar.setVisibility(View.INVISIBLE);
                        verificationID = newVerificationId;
                        mForceResendingToken = forceResendingToken;

                        countDownLayout.setVisibility(View.VISIBLE);
                        resendLayout.setVisibility(View.GONE);
                        handleCountDownTimer();
                    }
                }, mForceResendingToken
        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBar.setVisibility(View.INVISIBLE);

                            FirebaseUser user = task.getResult().getUser();

                            startActivity(new Intent(VerifyActivity.this, MainActivity.class));
                            finish();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void handleVerifyOTP() {
        progressBar.setVisibility(View.VISIBLE);

        String code = inputCode1.getText().toString() +
                inputCode2.getText().toString() +
                inputCode3.getText().toString() +
                inputCode4.getText().toString() +
                inputCode5.getText().toString() +
                inputCode6.getText().toString();

        if(code.length() == 6){
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);

            mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        // save to data
                        progressBar.setVisibility(View.INVISIBLE);

                        boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                        if(isNewUser){
                            FirebaseUser firebaseUser = task.getResult().getUser();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Phones")
                                    .child(firebaseUser.getUid());

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("phone", String.format(getIntent().getStringExtra("phone_number")));

                            ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);


                                }
                            });

                        }
                    }else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(VerifyActivity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(e instanceof  FirebaseAuthInvalidCredentialsException){
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(VerifyActivity.this, "Mã xác thực không chính xác", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }



                }
            });
        }

        else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(VerifyActivity.this, "Mã xác thực gồm 6 số", Toast.LENGTH_LONG).show();
            return;
        }


    }

    private void addControls() {
        // set title
        getSupportActionBar().setTitle("Nhập vào mã xác thực");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // init views
        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);
        countDownTv = findViewById(R.id.count_down_tv);
        countDownLayout = findViewById(R.id.count_down_layout);
        resendLayout = findViewById(R.id.resend_layout);
        resendBtn = findViewById(R.id.resend_btn);
        phoneNumberTv = findViewById(R.id.phone_number_tv);
        confirmBtn = findViewById(R.id.confirm_btn);
        progressBar = findViewById(R.id.verify_pb);

        // init firebase
        mAuth = FirebaseAuth.getInstance();

        // get verificationId
        verificationID = getIntent().getStringExtra("verificationId");

        // set text phone number
        phoneNumberTv.setText(getIntent().getStringExtra("phone_number").replaceFirst("0", "(+84) "));


        setupOtpInput();

        handleCountDownTimer();




    }

    private void handleCountDownTimer() {
        seconds = 60;
        new CountDownTimer(seconds * 1000,1000){
            @Override
            public void onTick(long l) {
                countDownTv.setText(String.valueOf(l / 1000));
            }

            @Override
            public void onFinish() {
                countDownLayout.setVisibility(View.GONE);
                resendLayout.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void setupOtpInput() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty()){
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty()){
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty()){
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty()){
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty()){
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // allow back previous activity
        return super.onSupportNavigateUp();

    }
}