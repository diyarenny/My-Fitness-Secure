package com.example.myfitnesssecure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

public class LoginActivity extends AppCompatActivity {

    //variables
    private EditText login_email;
    private EditText login_password;
    private Button loginBtn;
    private ProgressBar login_progress;
    private FirebaseAuth mAuth;
    private Button login_reg_btn;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //assigning id's
        login_email = (EditText) findViewById(R.id.login_email);
        login_password = (EditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        login_progress = (ProgressBar) findViewById(R.id.login_progress);
        login_reg_btn = (Button) findViewById(R.id.login_reg_btn);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        mAuth = FirebaseAuth.getInstance();

        //forgot password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
            }
        });

        //login register new account btn
        login_reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        //login button
        login_progress.setVisibility(View.INVISIBLE);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_progress.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.INVISIBLE);

                final String email = login_email.getText().toString().trim();
                final String password = login_password.getText().toString().trim();


                if (email.isEmpty()) {
                    login_email.setError("Email is Required");
                    login_email.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    login_email.setError("Please provide a valid email");
                    login_email.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    login_password.setError("Password is Required");
                    login_password.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    login_password.setError("Min password length should be 6 characters");
                    login_password.requestFocus();
                    return;
                }

                loginBtn.setVisibility(View.VISIBLE);
                login_progress.setVisibility(View.INVISIBLE);
                signIn(email,password);

            }
        });
    }

    private void signIn(String email, String password) {
        //authenticates the user
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    login_progress.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()){
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                    else{
                        //verifies email
                        user.sendEmailVerification();
                        Toast.makeText(LoginActivity.this, "Check your email to verify account", Toast.LENGTH_SHORT).show();
                        loginBtn.setVisibility(View.VISIBLE);
                        login_progress.setVisibility(View.INVISIBLE);
                    }

                }
                else{
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, "Login Failed" + errorMessage, Toast.LENGTH_SHORT).show();
                    login_progress.setVisibility(View.INVISIBLE);
                }
            }
        });

    }


    //checks if the user already has an account, if yes then user send to the main activity
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

}