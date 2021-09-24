package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
import com.iuh.stream.dialog.ResetPasswordDialog;
import com.iuh.stream.dialog.ShowLoginMethodsDialog;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnShowMoreLogin).setOnClickListener(v -> new ShowLoginMethodsDialog(this).show());

        findViewById(R.id.btnToRegister).setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        loginByEmailPart();
    }

    private void loginByEmailPart() {
        TextInputLayout layoutEmail = findViewById(R.id.email);
        TextInputLayout layoutPassword = findViewById(R.id.password);
        TextInputEditText edtEmail = findViewById(R.id.txtEmail);
        TextInputEditText edtPassword = findViewById(R.id.txtPassword);
        ProgressBar pbLoginEmail = findViewById(R.id.pbLoginEmail);

        findViewById(R.id.btnForgotPassword).setOnClickListener(v -> {
            new ResetPasswordDialog(this).show();
        });

        findViewById(R.id.btnLoginEmail).setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            layoutEmail.setHelperText("");
            layoutPassword.setHelperText("");

            boolean flag = true;

            if(!email.matches("^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$")) {
                layoutEmail.setHelperText("Email không hợp lệ!");
                flag = false;
            }
            if(password.length() < 6) {
                layoutPassword.setHelperText("Mật khẩu không hợp lệ!");
                flag = false;
            }

            if (flag){
                pbLoginEmail.setVisibility(View.VISIBLE);
                //Login
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            pbLoginEmail.setVisibility(View.GONE);
                            if(task.isSuccessful()) {
                                Intent intent = new Intent(this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            pbLoginEmail.setVisibility(View.GONE);
                            Toast.makeText(this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}