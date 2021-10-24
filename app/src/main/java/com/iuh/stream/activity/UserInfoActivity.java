package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.iuh.stream.R;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.fragment.ProfileFragment;
import com.iuh.stream.models.User;
import com.iuh.stream.models.responce.UpdateUserResponse;
import com.iuh.stream.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoActivity extends AppCompatActivity {
    // views
    private CircleImageView avatarIv;
    private TextView nameTv;
    private EditText firstNameEt, lastNameEt, dobEt, genderEt, emailEt, phoneNumberEt, dobUpdateEt, nameUpdateEt;
    private ImageButton backBtn;
    private ImageButton editFirstNameBtn, editLastNameBtn, editGenderBtn, editDobBtn;
    private FlexboxLayout emailLayout, phoneNumberLayout;
    private RadioGroup radioGroup;
    private RadioButton maleBtn, femaleBtn;
    private User user;
    private static final int FIRST_NAME_TYPE = 0;
    private static final int LAST_NAME_TYPE = 1;
    private static final int GENDER_TYPE = 2;
    private static final int DOB_TYPE = 3;
    private LinearProgressIndicator linearProgressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        addControls();
        addEvents();
    }

    private void addEvents() {
        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        editFirstNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopup(FIRST_NAME_TYPE);
            }
        });

        editLastNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopup(LAST_NAME_TYPE);
            }
        });

        editGenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopup(GENDER_TYPE);
            }
        });

        editDobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopup(DOB_TYPE);
            }
        });


    }

    private void openPopup(int type) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_layout);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = Gravity.CENTER;

        window.setAttributes(attributes);

        dialog.setCancelable(false);

        dialog.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // init views

        TextView titleTv = dialog.findViewById(R.id.title_tv);
        nameUpdateEt = dialog.findViewById(R.id.content_et);
        dobUpdateEt = dialog.findViewById(R.id.dob_update_et);
        Button updateBtn = dialog.findViewById(R.id.update_btn);
        linearProgressIndicator = dialog.findViewById(R.id.linearProgressIndicator);
        radioGroup = dialog.findViewById(R.id.layout_gender);

        nameUpdateEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(s.toString())){
                    updateBtn.setEnabled(false);
                }
                else{
                    updateBtn.setEnabled(true);
                }
            }
        });




        // set up view
        if (type == LAST_NAME_TYPE) {
            nameUpdateEt.setText(user.getLastName());
            nameUpdateEt.requestFocus();
            nameUpdateEt.setSelection(user.getLastName().length());
            titleTv.setText("Vui lòng nhập tên của bạn");
            radioGroup.setVisibility(View.GONE);
            dobUpdateEt.setVisibility(View.GONE);
        } else if (type == FIRST_NAME_TYPE) {
            nameUpdateEt.setText(user.getFirstName());
            nameUpdateEt.requestFocus();
            nameUpdateEt.setSelection(user.getFirstName().length());
            radioGroup.setVisibility(View.GONE);
            dobUpdateEt.setVisibility(View.GONE);
        } else if (type == GENDER_TYPE) {
            maleBtn = dialog.findViewById(R.id.rdMale);
            femaleBtn = dialog.findViewById(R.id.rdFemale);
            titleTv.setText("Vui lòng chọn giới tính của bạn");
            nameUpdateEt.setVisibility(View.GONE);
            dobUpdateEt.setVisibility(View.GONE);
            if (user.getGender().equals("Nam")) {
                maleBtn.setChecked(true);
            } else {
                femaleBtn.setChecked(true);
            }

        } else {
            titleTv.setText("Vui lòng chọn ngày sinh của bạn");
            dobUpdateEt.setVisibility(View.VISIBLE);
            nameUpdateEt.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            dobUpdateEt.setText(simpleDateFormat.format(user.getDateOfBirth()));

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

            picker.addOnDismissListener(ddialog -> {
                if (picker.getSelection() != null) {
                    dobUpdateEt.setText(
                            new SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi"))
                                    .format(new Date(picker.getSelection()))
                    );
                }
            });

            dobUpdateEt.setOnClickListener(v ->
                    picker.show(getSupportFragmentManager(), "tag"));

        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearProgressIndicator.setVisibility(View.VISIBLE);
                if (type == FIRST_NAME_TYPE || type == LAST_NAME_TYPE) {
                    String name = nameUpdateEt.getText().toString().trim();
                        if (type == FIRST_NAME_TYPE) {
                            user.setFirstName(name);
                        } else if (type == LAST_NAME_TYPE) {
                            user.setLastName(name);
                        }

                } else if (type == GENDER_TYPE) {
                    int radioId = radioGroup.getCheckedRadioButtonId();
                    RadioButton rdGender = dialog.findViewById(radioId);
                    String gender = rdGender.getText().toString();
                    user.setGender(gender);
                } else {
                    Date dob = null;
                    try {
                        dob = new SimpleDateFormat("dd/MM/yyyy").parse(dobUpdateEt.getText().toString());
                        user.setDateOfBirth(dob);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                RetrofitService.getInstance.updateUser(user, DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                        .enqueue(new Callback<UpdateUserResponse>() {
                            @Override
                            public void onResponse(Call<UpdateUserResponse> call, Response<UpdateUserResponse> response) {
                                if (response.isSuccessful()) {
                                    linearProgressIndicator.setVisibility(View.GONE);
                                    dialog.dismiss();
                                    finish();
                                    startActivity(getIntent());
                                }
                            }

                            @Override
                            public void onFailure(Call<UpdateUserResponse> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });


        dialog.show();


    }


    private void addControls() {
        // init views
        avatarIv = findViewById(R.id.user_info_avatar_iv);
        nameTv = findViewById(R.id.user_info_name_tv);
        firstNameEt = findViewById(R.id.user_info_first_name_et);
        lastNameEt = findViewById(R.id.user_info_last_name_et);
        dobEt = findViewById(R.id.user_info_dob_et);
        genderEt = findViewById(R.id.user_info_gender_et);
        emailEt = findViewById(R.id.user_info_email_et);
        phoneNumberEt = findViewById(R.id.user_info_phone_et);
        backBtn = findViewById(R.id.user_info_back_btn);
        editFirstNameBtn = findViewById(R.id.edit_first_name_btn);
        editLastNameBtn = findViewById(R.id.edit_last_name_btn);
        editGenderBtn = findViewById(R.id.edit_gender_btn);
        editDobBtn = findViewById(R.id.edit_dob_btn);
        phoneNumberLayout = findViewById(R.id.phone_layout);
        emailLayout = findViewById(R.id.email_layout);

        loadUserInfo();

        Log.e(Constants.TAG, "addControls: " + DataLocalManager.getStringValue(Constants.ACCESS_TOKEN));
        Log.e(Constants.TAG, "addControls: " + user.get_id());
    }

    private void loadUserInfo() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(ProfileFragment.USER_KEY);
        if (user != null) {
            Glide.with(this).load(user.getImageURL()).into(avatarIv);
            nameTv.setText(user.getFirstName() + " " + user.getLastName());
            firstNameEt.setText(user.getFirstName());
            lastNameEt.setText(user.getLastName());
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            dobEt.setText(simpleDateFormat.format(user.getDateOfBirth()));

            genderEt.setText(user.getGender());
            emailEt.setText(user.getEmail());
            phoneNumberEt.setText(user.getPhoneNumber());

            if (user.getPhoneNumber() != null) {
                emailLayout.setVisibility(View.GONE);
                phoneNumberLayout.setVisibility(View.VISIBLE);
                phoneNumberEt.setText(user.getPhoneNumber());
            } else if (!user.getEmail().equals("null")) {
                emailLayout.setVisibility(View.VISIBLE);
                phoneNumberLayout.setVisibility(View.GONE);
                emailEt.setText(user.getEmail());
            }

        }
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