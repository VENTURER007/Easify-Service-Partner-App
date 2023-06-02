package com.example.easifyservicepartnerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button forgotPasswordBtn, loginBtn, signupView;
    private ImageView googleAuth;
    private TextInputLayout email, password;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        forgotPasswordBtn = findViewById(R.id.forgotPassword);
        loginBtn = findViewById(R.id.loginBtn);
        signupView = findViewById(R.id.signupView);
        googleAuth = findViewById(R.id.googleAuth);
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress1);

        mAuth = FirebaseAuth.getInstance();

        signupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignupActivity();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String reg_email = email.getEditText().getText().toString().trim();
                String reg_password = password.getEditText().getText().toString().trim();

                if (validateInput(reg_email, reg_password)) {
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(reg_email, reg_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                                        goToMainActivity();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            this.email.setError("Email is required.");
            return false;
        }
        if (password.isEmpty()) {
            this.password.setError("Password is required.");
            return false;
        }
        return true;
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToSignupActivity() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }
}
