package com.example.easifyservicepartnerapp;
import static android.content.ContentValues.TAG;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.HashMap;
public class SignupActivity extends AppCompatActivity {
    Button SignupBtn;
    TextInputLayout reg_name, reg_email, reg_PhoneNo, reg_password;

    Connection connection;
    ProgressBar pb;
    FirebaseAuth mAuth;
    FirebaseDatabase database; // add the Firebase Realtime Database reference
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent home_view = new Intent(SignupActivity.this, LoginActivity.class );
            startActivity(home_view);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        pb = findViewById(R.id.progress2);
        reg_name = findViewById(R.id.reg_name);
        reg_email = findViewById(R.id.reg_email);
        reg_PhoneNo = findViewById(R.id.reg_phoneNo);
        reg_password = findViewById(R.id.reg_password);
        SignupBtn = findViewById(R.id.SignupBtn);
        database = FirebaseDatabase.getInstance(); // initialize the Firebase Realtime Database
        SignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                String name = reg_name.getEditText().getText().toString().trim();
                String email = reg_email.getEditText().getText().toString().trim();
                String phoneNo = reg_PhoneNo.getEditText().getText().toString().trim();
                String password = reg_password.getEditText().getText().toString().trim();
                Toast.makeText(SignupActivity.this, name, Toast.LENGTH_SHORT).show();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNo) || TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this,"Please fill the form completly!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pb.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    UserModel usermodel = new UserModel(name,email,phoneNo);
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    DatabaseReference ref = database.getReference("service_partners");
                                    ref.child( user.getUid()).setValue(usermodel);
                                    SharedPreferences pref = getSharedPreferences("user",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("name",name);
                                    editor.putString("email",email);
                                    editor.putString("phoneNo",phoneNo);
                                    editor.apply();
                                    Log.d(TAG, "Data added to Realtime Database for user: " + user.getUid());
                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });






            }
        });

    }
}


