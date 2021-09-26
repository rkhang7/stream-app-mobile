package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.iuh.stream.R;
import com.iuh.stream.dialog.ResetPasswordDialog;
import com.iuh.stream.dialog.ShowLoginMethodsDialog;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ShowLoginMethodsDialog showLoginMethodsDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



        mAuth = FirebaseAuth.getInstance();
        showLoginMethodsDialog = new ShowLoginMethodsDialog(this);

        findViewById(R.id.btnShowMoreLogin).setOnClickListener(v ->
                showLoginMethodsDialog.show());


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

            if (!email.matches("^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$")) {
                layoutEmail.setHelperText("Email không hợp lệ!");
                flag = false;
            }
            if (password.length() < 6) {
                layoutPassword.setHelperText("Mật khẩu không hợp lệ!");
                flag = false;
            }

            if (flag) {
                pbLoginEmail.setVisibility(View.VISIBLE);
                //Login
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            pbLoginEmail.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == ShowLoginMethodsDialog.RC_SIGN_IN) {



            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);


            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ShowLoginMethodsDialog.signInGoogleProgressBar.setVisibility(View.GONE);
                            // if user signing in first time then get and show user info from gg account
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                // save data to database

                            }
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();
                        } else {
                            ShowLoginMethodsDialog.signInGoogleProgressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "signInWithCredential:failure" + task.getException(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ShowLoginMethodsDialog.signInGoogleProgressBar.setVisibility(View.GONE);
                Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( showLoginMethodsDialog!=null && showLoginMethodsDialog.isShowing() ){
            showLoginMethodsDialog.cancel();
        }
    }
}