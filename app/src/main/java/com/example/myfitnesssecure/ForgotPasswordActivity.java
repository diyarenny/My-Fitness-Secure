package com.example.myfitnesssecure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    //variables
    private EditText reset_email;
    private Button resetBtn;
    private ProgressBar reset_progress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        reset_email = (EditText) findViewById(R.id.reset_email);
        resetBtn = (Button) findViewById(R.id.resetBtn);
        reset_progress = (ProgressBar) findViewById(R.id.reset_progress);
        mAuth = FirebaseAuth.getInstance();

        reset_progress.setVisibility(View.INVISIBLE);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = reset_email.getText().toString().trim();

                if(email.isEmpty()){
                    reset_email.setError("Email is required");
                    reset_email.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    reset_email.setError("Please provide a valid email");
                    reset_email.requestFocus();
                    return;
                }
                reset_progress.setVisibility(View.VISIBLE);
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset password", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                        else{
                            Toast.makeText(ForgotPasswordActivity.this, "Try again! Something wrong happened!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        });
    }
}