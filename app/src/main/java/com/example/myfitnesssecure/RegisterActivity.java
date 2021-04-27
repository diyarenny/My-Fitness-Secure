package com.example.myfitnesssecure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    //variables
    private CircularImageView regUserPhoto;
    private EditText regName;
    private EditText regEmail;
    private EditText regPassword;
    private EditText regPassword2;
    private ProgressBar regProgressBar;
    private Button reg_login_btn;
    private Button regBtn;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //setting id's
        reg_login_btn = (Button) findViewById(R.id.reg_login_btn);
        regUserPhoto = (CircularImageView) findViewById(R.id.regUserPhoto);
        regBtn = (Button) findViewById(R.id.regBtn);
        regName = (EditText) findViewById(R.id.regName);
        regEmail = (EditText) findViewById(R.id.regEmail);
        regPassword = (EditText) findViewById(R.id.regPassword);
        regPassword2 = (EditText) findViewById(R.id.regPassword2);
        regProgressBar = (ProgressBar) findViewById(R.id.regProgressBar);
        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        regProgressBar.setVisibility(View.INVISIBLE);

        //register already have an account
        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtn.setVisibility(View.INVISIBLE);
                regProgressBar.setVisibility(View.VISIBLE);

                final String name = regName.getText().toString().trim();
                final String email = regEmail.getText().toString().trim();
                final String password = regPassword.getText().toString().trim();
                final String passord2 = regPassword2.getText().toString().trim();

                if (name.isEmpty()) {
                    regName.setError("Name is Required");
                    regName.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    regEmail.setError("Email is Required");
                    regEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    regEmail.setError("Please provide a valid email");
                    regEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    regPassword.setError("Password is Required");
                    regPassword.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    regPassword.setError("Min password length should be 6 characters");
                    regPassword.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(passord2)) {
                    regPassword2.setError("Confirm Password is Required");
                    regPassword2.requestFocus();
                    return;
                }
                if (!passord2.equals(password)) {
                    regPassword2.setError("Confirm password must match Password");
                    regPassword2.requestFocus();
                    return;
                }

                regBtn.setVisibility(View.VISIBLE);
                regProgressBar.setVisibility(View.INVISIBLE);
                
                // everything is ok and all fields are filled now we can start creating user account
                // CreateUserAccount method will try to create the user if the email is valid
                CreateUserAccount(email,name,password);

            }
        });

        //Circle Image View - Profile Picture
        regUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {

                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }

        });
    }

    private void CreateUserAccount(String email, String name, String password) {
        //creates a new user into the database with an email and password
        mAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    User user = new User(name, email, password);

                    //stores to database
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                //startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                                // after we created user account we need to update user's profile picture and name
                                uploadPicture(name ,pickedImgUri,mAuth.getCurrentUser());
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Failed to register, Try again!", Toast.LENGTH_LONG).show();
                                regBtn.setVisibility(View.VISIBLE);
                                regProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Failed to register, Try again!", Toast.LENGTH_LONG).show();
                    regProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    public void uploadPicture(String name, Uri pickedImgUri, FirebaseUser currentUser) {
        //StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        //final StorageReference profileRef = storageReference.child(Objects.requireNonNull(pickedImgUri.getLastPathSegment()));
        final String randomKey = UUID.randomUUID().toString();
        StorageReference profileRef = storageReference.child("users_photos" + randomKey);

        profileRef.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image uploaded succesfully and we can get our image url
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // uri contain user image url
                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();


                        currentUser.updateProfile(profleUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            // user info updated successfully
                                            Toast.makeText(RegisterActivity.this, "Register Complete", Toast.LENGTH_SHORT).show();
                                            updateUI();
                                        }
                                    }
                                });
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(RegisterActivity.this, "Failed to Upload Picture", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI() {
        Intent intentActivity = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(intentActivity);
        finish();
    }

    private void openGallery() {
        //open gallery intent and wait for user to pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(RegisterActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else
            openGallery();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            regUserPhoto.setImageURI(pickedImgUri);
        }

    }

    /*
    //checks if the user already has an account, if yes then user send to the main activity
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
     */
}
