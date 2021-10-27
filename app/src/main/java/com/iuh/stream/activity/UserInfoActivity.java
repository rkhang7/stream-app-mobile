package com.iuh.stream.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.iuh.stream.R;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.fragment.ProfileFragment;
import com.iuh.stream.models.User;
import com.iuh.stream.models.responce.UpdateUserResponse;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    // permission constants
    private static final int CAMERA_CODE = 6;
    private static final int GALLERY_CODE = 7;
    // uri
    private Uri avatarUri;

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


        avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        openChangeAvatarPopup();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        CustomAlert.showToast(UserInfoActivity.this, CustomAlert.WARNING, "Permission Denied\n");
                    }
                };

                TedPermission.create()
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();

            }
        });

    }

    private void openChangeAvatarPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_avatar_popup);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = Gravity.CENTER;

        window.setAttributes(attributes);

        dialog.setCancelable(true);

        Button viewAvatarBtn = dialog.findViewById(R.id.view_avatar_btn);
        Button imageFromCamera = dialog.findViewById(R.id.image_camera_btn);
        Button imageFromGallery = dialog.findViewById(R.id.image_gallery_btn);

        imageFromCamera.setOnClickListener(v -> openCamera());

        imageFromGallery.setOnClickListener(v -> {
            openGallery();
        });

        dialog.show();


    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        values.put(MediaStore.Images.Media.TITLE, "Temp Desc");
        avatarUri = getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, avatarUri);
        startActivityForResult(intent, CAMERA_CODE);

    }

    private void openPopup(int type) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_info_popup);

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
                if (TextUtils.isEmpty(s.toString())) {
                    updateBtn.setEnabled(false);
                } else {
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
                                if (response.code() == 403) {
                                    Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
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

                                                }
                                            });
                                }
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
//        Log.e("TAG", "addControls: " + user.toString() );

//        Log.e(Constants.TAG, "addControls: " + DataLocalManager.getStringValue(Constants.ACCESS_TOKEN));
//        Log.e(Constants.TAG, "addControls: " + user.get_id());
    }

    private void loadUserInfo() {
        RetrofitService.getInstance.getMeInfo(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            loadUserInfo();
                        } else {
                            user = response.body();
                            if (user != null) {
                                Picasso.get().load(user.getImageURL()).into(avatarIv);
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
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            avatarUri = data.getData();
            final InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(avatarUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                String encodedImage = "data:image/jpeg;base64," + encodeImage(selectedImage);
                uploadImageToAws(encodedImage);
            } catch (FileNotFoundException e) {
                CustomAlert.showToast(UserInfoActivity.this, CustomAlert.WARNING, e.getMessage());
            }

        }
        // for camera
        else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = avatarUri;
            final InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(uri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                String encodedImage = "data:image/jpeg;base64," + encodeImage(selectedImage);
                uploadImageToAws(encodedImage);
            } catch (FileNotFoundException e) {
                CustomAlert.showToast(UserInfoActivity.this, CustomAlert.WARNING, e.getMessage());
            }

        }


    }

    private void uploadImageToAws(String encodedImage) {
        RetrofitService.getInstance.updateAvatar(encodedImage, DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<UpdateUserResponse>() {
                    @Override
                    public void onResponse(Call<UpdateUserResponse> call, Response<UpdateUserResponse> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            uploadImageToAws(encodedImage);
                        } else {
                            finish();
                            startActivity(getIntent());
                        }

                    }

                    @Override
                    public void onFailure(Call<UpdateUserResponse> call, Throwable t) {
                        CustomAlert.showToast(UserInfoActivity.this, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    private String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagefile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }
}