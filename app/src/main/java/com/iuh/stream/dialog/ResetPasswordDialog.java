package com.iuh.stream.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;

import java.util.Objects;

public class ResetPasswordDialog extends Dialog {
    private final ProgressBar pbResetPass;
    private final FirebaseAuth mAuth;

    public ResetPasswordDialog(@NonNull Activity activity) {
        super(activity);
        setOwnerActivity(activity);
        setContentView(R.layout.dialog_reset_password);

        mAuth = FirebaseAuth.getInstance();

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        TextInputLayout layoutEmail = findViewById(R.id.email);
        TextInputEditText edtEmail = findViewById(R.id.txtEmail);
        pbResetPass = findViewById(R.id.pbResetPass);

        findViewById(R.id.btnBack).setOnClickListener(v -> dismiss());

        findViewById(R.id.btnReset).setOnClickListener(v -> {
            String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();

            layoutEmail.setHelperText("");

            if(!email.matches("^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$")) {
                layoutEmail.setHelperText("Email không hợp lệ!");
            } else {
                pbResetPass.setVisibility(View.VISIBLE);
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            pbResetPass.setVisibility(View.GONE);
                            if(task.isSuccessful()) {
                                Toast.makeText(getOwnerActivity(), "Đã gửi email đến" + email + ". Vui lòng truy cập liên kết trong email để đổi mật khẩu!", Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else {
                                Toast.makeText(getOwnerActivity(), "Email không hợp lệ hoặc chưa được đăng ký!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            pbResetPass.setVisibility(View.GONE);
                            Log.e("CE", "ResetPasswordDialog: ", e);
                            Toast.makeText(getOwnerActivity(), "Lỗi kết nối!", Toast.LENGTH_LONG).show();
                        });
            }
        });
    }

}
