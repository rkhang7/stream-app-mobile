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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.iuh.stream.R;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.dialog.ResetPasswordDialog;
import com.iuh.stream.models.jwt.IdToken;
import com.iuh.stream.models.jwt.Token;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.MyConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout layoutPhone;
    private String phoneNumber84, password;
    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private TextInputLayout layoutOtp;
    private LinearProgressIndicator pgPhone;
    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 100;
    private CountDownTimer countDownTimer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        LinearLayout lnEmail = findViewById(R.id.byEmail);
        LinearLayout lnPhone = findViewById(R.id.byPhone);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().setBackgroundTintList(getResources().getColorStateList(R.color.white_smoke));
                ((TextView) tab.getCustomView().findViewById(R.id.tvText)).setTextColor(getResources().getColor(R.color.secondary));
                lnEmail.setVisibility(lnEmail.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
                lnPhone.setVisibility(lnPhone.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().setBackgroundTintList(getResources().getColorStateList(R.color.main));
                ((TextView) tab.getCustomView().findViewById(R.id.tvText)).setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tab.getCustomView().setBackgroundTintList(getResources().getColorStateList(R.color.white_smoke));
                ((TextView) tab.getCustomView().findViewById(R.id.tvText)).setTextColor(getResources().getColor(R.color.secondary));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        configGoogleSignIn();

        findViewById(R.id.toRegister).setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        loginByEmailPart();
        loginPhonePart();

        //Google login
        findViewById(R.id.btnLoginWithGoogle).setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void loginByEmailPart() {
        TextInputLayout layoutEmail = findViewById(R.id.email);
        TextInputLayout layoutPassword = findViewById(R.id.password);
        TextInputEditText edtEmail = findViewById(R.id.txtEmail);
        TextInputEditText edtPassword = findViewById(R.id.txtPassword);
        LinearProgressIndicator pgEmail = findViewById(R.id.pgEmail);

        findViewById(R.id.forgotPassword).setOnClickListener(v -> {
            new ResetPasswordDialog(this).show();
        });

        findViewById(R.id.btnLoginEmail).setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            password = edtPassword.getText().toString().trim();

            layoutEmail.setError("");
            layoutPassword.setError("");

            boolean flag = true;

            if (!email.matches("^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$")) {
                layoutEmail.setError("Email không hợp lệ!");
                flag = false;
            }
            if (password.length() < 6) {
                layoutPassword.setError("Mật khẩu không hợp lệ!");
                flag = false;
            }

            if (flag) {
                pgEmail.setVisibility(View.VISIBLE);
                //Login
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            pgEmail.setVisibility(View.INVISIBLE);
                            if (task.isSuccessful()) {
                                if(!task.getResult().getUser().isEmailVerified()) {
                                    task.getResult().getUser().sendEmailVerification()
                                            .addOnSuccessListener(unused -> {
                                                CustomAlert.showToast(this, CustomAlert.WARNING, "Email chưa được xác thực! Vui lòng kiểm tra hộp thư để xác thực email.");
                                            })
                                            .addOnFailureListener(e -> {
                                                String message = "Lỗi kết nối mạng!";
                                                if(e instanceof FirebaseTooManyRequestsException) {
                                                    message = "Tạm khóa tài khoản! Có vẻ như có ai đó đang cố truy cập vào tài khoản này!";

                                                }
                                                CustomAlert.showToast(this, CustomAlert.WARNING, message);
                                            });

                                } else {
                                    handleGetToken();
                                }

                            } else {
                                String message = "Không thể đăng nhập!";

                                if(task.getException() instanceof FirebaseAuthException) {
                                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                    switch (e.getErrorCode()) {
                                        case "ERROR_WRONG_PASSWORD":
                                        case "ERROR_USER_NOT_FOUND":
                                            message = "Email hoặc mật khẩu không đúng!";
                                            break;
                                        case "ERROR_TOO_MANY_REQUESTS":
                                            message = "Không thể xác thực! Có vẻ như có bạn đang cố truy cập vào tài khoản này.";
                                            break;
                                        case "ERROR_NETWORK_REQUEST_FAILED":
                                            message = "Lỗi kết nối mạng!";
                                            break;

                                        default:
                                            message = "Không thể đăng nhập!";
                                            break;
                                    }
                                }

                                CustomAlert.showToast(SignInActivity.this, CustomAlert.WARNING, message);
                                Log.d(MyConstant.TAG, "loginByEmailPart: \n" + task.getException());
                            }
                        });
            }
        });
    }

    private void handleGetToken() {
        FirebaseUser user = mAuth.getCurrentUser();
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    IdToken idToken = new IdToken(task.getResult().getToken());
                    RetrofitService.getInstance.getToken(idToken).enqueue(new Callback<Token>() {
                        @Override
                        public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                            if(response.isSuccessful()) {
                                Token token = response.body();
                                saveTokenToDataLocal(token);
                                // save password to change later
                                DataLocalManager.putStringValue(MyConstant.PASSWORD, password);
                                CustomAlert.showToast(SignInActivity.this, CustomAlert.INFO, "Đăng nhập thành công!");
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                CustomAlert.showToast(SignInActivity.this, CustomAlert.WARNING, "Tài khoản không tồn tại!");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                            CustomAlert.showToast(SignInActivity.this, CustomAlert.WARNING, "Có lỗi đã xảy ra!");
                            Log.d(MyConstant.TAG, "handleGetToken: \n" + t);
                        }
                    });

                }
            }
        });
    }

    private void saveTokenToDataLocal(Token token) {
        DataLocalManager.putStringValue(MyConstant.ACCESS_TOKEN, token.getAccessToken());
        DataLocalManager.putStringValue(MyConstant.REFRESH_TOKEN, token.getRefreshToken());
    }

    private void loginPhonePart() {
        TextInputEditText edtPhone = findViewById(R.id.txtPhone);
        layoutPhone = findViewById(R.id.phone);
        layoutOtp = findViewById(R.id.otp);
        pgPhone = findViewById(R.id.pgPhone);

        TextInputEditText edtOtpCode = findViewById(R.id.txtOtp);

        Button btnGetOtp = findViewById(R.id.btnGetCode);
        Button btnLoginPhone = findViewById(R.id.btnLoginPhone);
        btnLoginPhone.setEnabled(false);

        btnGetOtp.setOnClickListener(v -> {
            String phoneNumber = Objects.requireNonNull(edtPhone.getText()).toString().trim();
            if (checkValidPhoneNumber(phoneNumber)) {

                pgPhone.setVisibility(View.VISIBLE);
                btnGetOtp.setEnabled(false);

                // convert phone number to +84 phone number
                phoneNumber84 = phoneNumber.replaceFirst("0", "+84");

                PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber84)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallback);

                if (mResendToken != null)
                    builder.setForceResendingToken(mResendToken);

                PhoneAuthOptions options = builder.build();

                //Verify the phone number, result can get in mCallback
                PhoneAuthProvider.verifyPhoneNumber(options);

            }
        });

        //When user enter confirm otp/ login by thí phone
        btnLoginPhone.setOnClickListener(v -> {
            String code = Objects.requireNonNull(edtOtpCode.getText()).toString().trim();

            if (code.length() == 6) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

                pgPhone.setVisibility(View.VISIBLE);
                signInWithPhoneAuthCredential(credential);
            } else {
                layoutOtp.setError("Mã xác nhận gồm 6 chữ số!");
                layoutOtp.setHelperText("");
            }
        });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pgPhone.setVisibility(View.INVISIBLE);
                btnGetOtp.setEnabled(true);
                Log.d(MyConstant.TAG, "onVerificationFailed: \n" + e);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

                pgPhone.setVisibility(View.INVISIBLE);
                btnGetOtp.setEnabled(true);
                btnLoginPhone.setEnabled(true);

                layoutPhone.setError("");

                int seconds = 120;
                CustomAlert.showToast(SignInActivity.this, CustomAlert.INFO, "Mã xác nhận đã được gửi đến số điện thoại của bạn");


                if(countDownTimer != null) {
                    countDownTimer.cancel();
                }

                countDownTimer =  new CountDownTimer(seconds * 1000, 1000) {
                    @Override
                    public void onTick(long l) {
                        layoutPhone.setHelperText("Mã hết hạn trong: " + l / 1000);
                    }

                    @Override
                    public void onFinish() {
                        layoutPhone.setHelperText("Không nhận được mã? Nhấn gửi lại");
                        btnGetOtp.setText("Gửi lại");
                    }
                }.start();
            }

        };
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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, task -> {
                    pgPhone.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                            //User's not exist
                            CustomAlert.showToast(SignInActivity.this, CustomAlert.WARNING, "Số điện thoại chưa được đăng ký, vui lòng thử lại!");
                        } else {
                            handleGetToken();
                        }

                    } else {
                        // Sign in failed, display a message and update the UI

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            CustomAlert.showToast(SignInActivity.this, CustomAlert.WARNING, "Mã không hợp lệ, vui lòng thử lại!");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    pgPhone.setVisibility(View.INVISIBLE);
                    Log.d(MyConstant.TAG, "signInWithPhoneAuthCredential: \n" + e);
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.d(MyConstant.TAG, "onActivityResult: \n" + e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // if user signing in first time then get and show user info from gg account
                        if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                            FirebaseUser user = task.getResult().getUser();
                            saveUserToDatabase(user);
                        }
                        // Sign in success, update UI with the signed-in user's information
                        handleGetToken();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d(MyConstant.TAG, "firebaseAuthWithGoogle: \n" + task.getException());
                        CustomAlert.showToast(SignInActivity.this, CustomAlert.WARNING, "Lỗi đăng nhập!");
                    }
                });
    }

    private void saveUserToDatabase(FirebaseUser user) {
        String uid = user.getUid();
        String[] s = Objects.requireNonNull(user.getDisplayName()).split(" ");
        List<String> displayName = Arrays.asList(s);

        String firstName = displayName.get(0);
        StringBuilder lastName = new StringBuilder();
        for (int i = 1; i < displayName.size(); i++) {
            lastName.append(displayName.get(i));
        }

        String gender = null;
        String imageURL = Objects.requireNonNull(user.getPhotoUrl()).toString();
        String phoneNumber = user.getPhoneNumber();
        String email = user.getEmail();
        List<String> contacts = new ArrayList<>();


        User mUser = new User(uid, firstName, lastName.toString(), gender, imageURL, phoneNumber, email, true, contacts);

        RetrofitService.getInstance.saveUser(mUser, uid).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User resUser = response.body();
                if (resUser == null) {
                    Log.d(MyConstant.TAG, "SaveUser: \n" + "Không thể lưu user!");
                    CustomAlert.showToast(SignInActivity.this, CustomAlert.WARNING, "Không thể kết nối đến server!");
                }

            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.d(MyConstant.TAG, "saveUserToDatabase: \n" + t);
                CustomAlert.showToast(SignInActivity.this, CustomAlert.WARNING, "Không thể kết nối đến server!");
                Toast.makeText(SignInActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void configGoogleSignIn() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString((R.string.default_client_id)))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(SignInActivity.this, gso);
    }
}