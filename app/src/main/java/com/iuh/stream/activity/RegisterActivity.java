package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.iuh.stream.R;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.jwt.IdToken;
import com.iuh.stream.models.jwt.Token;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    // firebase
    private FirebaseAuth mAuth;

    // phone number set to sendTP;
    private String phoneNumber84;
    private LinearProgressIndicator pgPhone;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    //Verify comps
    private TextInputLayout layoutPhone;
    private TextInputLayout layoutOtp;
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtBirthDate;
    private RadioGroup radGroupGender;
    private TextInputLayout layoutFirstName;
    private TextInputLayout layoutBirthDate;
    private final long OTP_LIFE_TIME = 120L;
    private static final int EMAIL_TYPE = 1;
    private static final int PHONE_NUMBER_TYPE = 2;
    private CountDownTimer countDownTimer;


    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();


        // views
        EditText edtBirthdate = findViewById(R.id.txtBirthDate);

        findViewById(R.id.toLogin).setOnClickListener(v -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        });

        layoutFirstName = findViewById(R.id.fName);
        layoutBirthDate = findViewById(R.id.birthdate);

        edtFirstName = findViewById(R.id.txtFirstName);
        edtLastName = findViewById(R.id.txtLastName);
        edtBirthDate = findViewById(R.id.txtBirthDate);
        radGroupGender = findViewById(R.id.gender);

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

        addByPhone();
        addByEmail();

        long today = MaterialDatePicker.todayInUtcMilliseconds();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        calendar.setTimeInMillis(today);
        calendar.roll(Calendar.YEAR, -5); //back to 5 years
        long endDate = calendar.getTime().getTime();

        CalendarConstraints constraintsBuilder = new CalendarConstraints.Builder()
                .setEnd(endDate)
                .setOpenAt(endDate)
                .build();


        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setSelection(calendar.getTime().getTime())
                .setCalendarConstraints(constraintsBuilder)
                .build();

        picker.addOnDismissListener(dialog -> {
            if (picker.getSelection() != null) {
                edtBirthdate.setText(
                        new SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi"))
                                .format(new Date(picker.getSelection()))
                );
            }
        });

        edtBirthdate.setOnClickListener(v ->
                picker.show(getSupportFragmentManager(), "tag"));
    }


    private boolean checkValidInput() {
        String fName = edtFirstName.getText().toString().trim();
//        String lName = edtLastName.getText().toString().trim();
        String birthDate = edtBirthDate.getText().toString().trim();

        boolean flag = true;

        if (fName.isEmpty()) {
            layoutFirstName.setError("Tên chưa hợp lệ");
            flag = false;
        } else {
            layoutFirstName.setError("");
        }
        if (birthDate.isEmpty()) {
            layoutBirthDate.setError("Chưa chọn ngày sinh");
            flag = false;
        } else {
            layoutBirthDate.setError("");
        }



        return flag;
    }


    private void addByPhone() {
        TextInputEditText edtPhone = findViewById(R.id.txtPhone);
        layoutPhone = findViewById(R.id.phone);
        layoutOtp = findViewById(R.id.otp);
        pgPhone = findViewById(R.id.pgPhone);

        TextInputEditText edtOtpCode = findViewById(R.id.txtOtp);

        Button btnGetOtp = findViewById(R.id.btnGetCode);
        Button btnLoginPhone = findViewById(R.id.btnRegisterPhone);
        btnLoginPhone.setEnabled(false);

        btnGetOtp.setOnClickListener(v -> {
            String phoneNumber = Objects.requireNonNull(edtPhone.getText()).toString().trim();
            if (checkValidInput() && checkValidPhoneNumber(phoneNumber)) {

                pgPhone.setVisibility(View.VISIBLE);
                btnGetOtp.setEnabled(false);

                // convert phone number to +84 phone number
                phoneNumber84 = phoneNumber.replaceFirst("0", "+84");

                PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber84)
                        .setTimeout(OTP_LIFE_TIME, TimeUnit.SECONDS)
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
                Log.d("AA", "onVerificationCompleted: ");
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pgPhone.setVisibility(View.INVISIBLE);
                btnGetOtp.setEnabled(true);
                Log.e("CE", "onVerificationFailed: ", e);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

                pgPhone.setVisibility(View.INVISIBLE);
                btnGetOtp.setEnabled(true);
                btnLoginPhone.setEnabled(true);

                CustomAlert.showToast(RegisterActivity.this, CustomAlert.INFO, "Mã xác nhận đã được gửi đến số điện thoại của bạn");


                layoutPhone.setError("");

                if(countDownTimer != null) {
                    countDownTimer.cancel();
                }

                countDownTimer =  new CountDownTimer(OTP_LIFE_TIME * 1000, 1000) {
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
            layoutPhone.setError("Số điện thoại không được rỗng");
            return false;
        }

        if (phoneNumber.charAt(0) != '0' || phoneNumber.length() < 10) {
            layoutPhone.setError("Số điện thoại phải bắt đầu bằng 0 và gồm 10 số");
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
                        if (task.getResult().getAdditionalUserInfo() != null && !task.getResult().getAdditionalUserInfo().isNewUser()) {
                            //User already exist => Register fail
                            CustomAlert.showToast(this, CustomAlert.WARNING, "Số điện thoại đã được sử dụng, vui lòng dùng số khác!");
                        } else {
                            saveUserToDatabase(Objects.requireNonNull(task.getResult().getUser()).getUid(), PHONE_NUMBER_TYPE);
                        }

                    } else {
                        // Sign in failed, display a message and update the UI

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            CustomAlert.showToast(this, CustomAlert.WARNING, "Mã không hợp lệ, vui lòng thử lại!");
                        }
                    }
                })
                .addOnFailureListener(e -> pgPhone.setVisibility(View.INVISIBLE));

    }

    private void addByEmail() {
        TextInputLayout layoutEmail = findViewById(R.id.email);
        TextInputLayout layoutPassword = findViewById(R.id.password);
        TextInputLayout layoutRePassword = findViewById(R.id.rePassword);
        TextInputEditText edtEmail = findViewById(R.id.txtEmail);
        TextInputEditText edtPassword = findViewById(R.id.txtPassword);
        TextInputEditText edtRePassword = findViewById(R.id.txtRePassword);
        ProgressBar pbEmailRegister = findViewById(R.id.pgEmail);

        findViewById(R.id.btnRegisterEmail).setOnClickListener(v -> {
            String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(edtPassword.getText()).toString().trim();
            String rePassword = Objects.requireNonNull(edtRePassword.getText()).toString().trim();

            layoutEmail.setHelperText("");
            layoutPassword.setHelperText("");
            layoutRePassword.setHelperText("");

            boolean flag = true;

            flag = checkValidInput();

            if (!email.matches("^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$")) {
                layoutEmail.setError("Email không hợp lệ!");
                flag = false;
            }
            if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$")) {
                layoutPassword.setError("Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường và 1 số, tối thiểu 6 ký tự");
                flag = false;
            }
            if (!password.equals(rePassword)) {
                layoutRePassword.setError("Mật khẩu không khớp!");
                flag = false;
            }

            if (flag) {
                layoutEmail.setHelperText("");
                layoutPassword.setHelperText("");
                layoutRePassword.setHelperText("");

                pbEmailRegister.setVisibility(View.VISIBLE);
                //Register account
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            pbEmailRegister.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                String uid = Objects.requireNonNull(task.getResult().getUser()).getUid();
                                saveUserToDatabase(uid, EMAIL_TYPE);

                                task.getResult().getUser().sendEmailVerification()
                                        .addOnSuccessListener(unused -> CustomAlert.showToast(RegisterActivity.this, CustomAlert.INFO, "Vui lòng kiểm tra email để xác nhận đăng kí tài khoản!"))
                                        .addOnFailureListener(e -> CustomAlert.showToast(RegisterActivity.this, CustomAlert.WARNING, "Lỗi kết nối mạng!"));

                            }
                        })
                        .addOnFailureListener(e -> {
                            pbEmailRegister.setVisibility(View.GONE);
                            Log.e(Constants.TAG, "create by email: ", e);

                            String message = "Tạo tài khoản thất bại!";
                            if(e instanceof FirebaseAuthException) {
                                FirebaseAuthException fae = (FirebaseAuthException) e;
                                switch (fae.getErrorCode()) {
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        message = "Email đã được sử dụng!";
                                        break;
                                    case "ERROR_WEAK_PASSWORD":
                                        message = "Mật khẩu quá yếu! Vui lòng sử dụng mật khẩu mạnh hơn.";
                                        break;
                                }
                            }
                            CustomAlert.showToast(this, CustomAlert.WARNING, message);
                        });

            }
        });
    }

    private void saveUserToDatabase(String uid, int type) {
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();

        // get gender
        radGroupGender = findViewById(R.id.gender);
        int radioId = radGroupGender.getCheckedRadioButtonId();
        RadioButton rdGender = findViewById(radioId);
        String gender = rdGender.getText().toString();

        // get dob
        EditText edtBirthdate = findViewById(R.id.txtBirthDate);
        Date dob = null;
        try {
            dob = new SimpleDateFormat("dd/MM/yyyy").parse(edtBirthdate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String email = null;
        String phoneNumber = null;

        if (type == EMAIL_TYPE) {
            EditText txtEmail = findViewById(R.id.txtEmail);
            email = txtEmail.getText().toString();
        } else if (type == PHONE_NUMBER_TYPE) {
            EditText txtPhoneNumber = findViewById(R.id.txtPhone);
            phoneNumber = txtPhoneNumber.getText().toString();
        }



        User user = new User(uid, firstName, lastName, gender,dob, phoneNumber, email, true);

        RetrofitService.getInstance.saveUser(user, uid).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User resUser = response.body();
                if(resUser == null){
                    Log.d(Constants.TAG, "Save user: \n" + "Null");
                    CustomAlert.showToast(RegisterActivity.this, CustomAlert.WARNING, "Không thể tạo tài khoản, vui lòng thử lại!");
                }
                else{
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    if(mAuth.getCurrentUser() != null){
                        UserInfo userInfo = mAuth.getCurrentUser().getProviderData().get(1);
                        if(!userInfo.getProviderId().equals("password") || mAuth.getCurrentUser().isEmailVerified()) {
                            handleGetToken();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                CustomAlert.showToast(RegisterActivity.this, CustomAlert.WARNING, "Không thể tạo tài khoản, vui lòng thử lại!");
            }
        });

    }

    private void handleGetToken() {
        FirebaseUser user = mAuth.getCurrentUser();
        Log.e("TAG", "handleGetToken: " + user.getUid() );
        user.getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                IdToken idToken = new IdToken(task.getResult().getToken());
                RetrofitService.getInstance.getToken(idToken).enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                        if(response.isSuccessful()){
                            Token token = response.body();
                            if (token != null) {
                                saveTokenToDataLocal(token);
                            }
                            CustomAlert.showToast(RegisterActivity.this, CustomAlert.INFO, "Đăng kí thành công!");
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Log.e("TAG", "onResponse: " + "Errr" );
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                        CustomAlert.showToast(RegisterActivity.this, CustomAlert.WARNING, t.getMessage());
                    }
                });

            } else {
                CustomAlert.showToast(RegisterActivity.this, CustomAlert.WARNING, "Lỗi kết nối!");

            }
        });
    }
    private void saveTokenToDataLocal(Token token) {
        DataLocalManager.putStringValue(Constants.ACCESS_TOKEN,token.getAccessToken());
        DataLocalManager.putStringValue(Constants.REFRESH_TOKEN,token.getRefreshToken());
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // hide keyboard
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}