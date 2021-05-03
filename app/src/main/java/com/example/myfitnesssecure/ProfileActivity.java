package com.example.myfitnesssecure;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
    //variables
    public static final String TAG = "TAG";
    private double BMR = 0.0;
    private long _weight = 0;
    private long _height = 0;
    private long _age = 0;
    private String _gender = "";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("UsersPersonalDetails");
    private static final int PReqCode = 3 ;
    private static final int REQUESCODE = 3 ;
    private String userId;
    private CircularImageView profileImage;
    private TextView name;
    public Uri imageUri;
    //if the profile picture has changed
    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        profileImage = (CircularImageView) findViewById(R.id.profileImage);
        name = (TextView) findViewById(R.id.name);
        Button btnEditProfile = (Button) findViewById(R.id.btnEditProfile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        //glide user profile picture
        Glide.with(ProfileActivity.this).load(currentUser.getPhotoUrl()).into(profileImage);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.popup_profile, null);

                Button btnCancel = (Button) mView.findViewById(R.id.btnCancelProfile);
                Button btnSubmit = (Button) mView.findViewById(R.id.btnSubmitProfile);
                final EditText etName = (EditText) mView.findViewById(R.id.etName);
                final EditText etHeight = (EditText) mView.findViewById(R.id.etHeight);
                final EditText etWeight = (EditText) mView.findViewById(R.id.etWeight);
                final EditText etAge = (EditText) mView.findViewById(R.id.etAge);
                final EditText etGender = (EditText) mView.findViewById(R.id.etGender);

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!etName.getText().toString().equals("")){
                            String name = etName.getText().toString();
                            writeName(name);
                            dialog.dismiss();
                        }
                        if (!etHeight.getText().toString().equals("")){
                            String height = etHeight.getText().toString();
                            writeHeight(Integer.parseInt(height));
                            _height = Integer.parseInt(height);
                            calcBMR();
                            writeCalGoal((int)(BMR*1.2));
                        }
                        if (!etWeight.getText().toString().equals("")){
                            String weight = etWeight.getText().toString();
                            writeWeight(Integer.parseInt(weight));
                            _weight = Integer.parseInt(weight);
                            calcBMR();
                            writeCalGoal((int)(BMR*1.2));
                        }
                        if (!etAge.getText().toString().equals("")){
                            String age = etAge.getText().toString();
                            writeAge(Integer.parseInt(age));
                            _age = Integer.parseInt(age);
                            calcBMR();
                            writeCalGoal((int)(BMR*1.2));
                        }
                        if (!etGender.getText().toString().equals("")){
                            String gender = etGender.getText().toString();
                            writeGender(gender);
                            _gender = gender;
                            calcBMR();
                            writeCalGoal((int)(BMR*1.2));
                        }

                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        loadData();

        //set up profile picture
        //When user clicks on to add a profiler
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
            }

        });
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(ProfileActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else
            openGallery();

    }

    private void openGallery() {
        //open gallery intent and wait for user to pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }

    }

    //calculates the BMR according to the formulas from:
    // (https://www.everydayhealth.com/weight/boost-weight-loss-by-knowing-your-bmr.aspx)
    // assumes weight in pounds, height in inches, and age in years
    public void calcBMR(){

        if (_gender.toLowerCase().equals("male"))
            BMR = 66 + (6.23 * _weight) + (12.7 * _height) - (6.8 * _age);
        else
            BMR = 655 + (4.35 * _weight) + (4.7 * _height) - (4.7 * _age);

    }

    //loads data from the db into the profile strings displayed in the app
    public void loadData(){
        TextView name = (TextView) findViewById(R.id.name);
        TextView age = (TextView) findViewById(R.id.age);
        TextView gender = (TextView) findViewById(R.id.gender);
        TextView bmr = (TextView) findViewById(R.id.bmr);
        TextView weight = (TextView) findViewById(R.id.weight);
        TextView height = (TextView) findViewById(R.id.height);

        root.child("userPersonalDetails").child(userId)()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()){

                            Map<String,Object> data = document.getData();

                            //gets data from database
                            assert data != null;
                            _weight = (long)data.get("weight");
                            _height = (long)data.get("height");
                            _age = (long)data.get("age");
                            _gender = (String)data.get("gender");
                            calcBMR();

                            //update the viewable text with the values from database
                            name.setText((String)data.get("name"));
                            age.setText(String.format("%d yrs.", _age));
                            gender.setText((String)_gender);
                            height.setText(String.format("%d in.", _height));
                            weight.setText(String.format("%d lbs.", _weight));
                            bmr.setText(String.format("%.1f", BMR));

                            System.out.println("Successfully loaded data for profile view from Database");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });

    }

    //write daily calorie goal to database
    public void writeCalGoal(int calorie_goal) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("calorie_goal", calorie_goal);

        root.child(currentUser.getUid()).setValue(userMap);
    }

    //write inputted name
    public void writeName(String name) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);

        root.child(currentUser.getUid()).setValue(userMap);

    }

    //write inputted height
    public void writeHeight(int height) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("height", height);

        root.child(currentUser.getUid()).setValue(userMap);
    }


    //write inputted weight
    public void writeWeight(int weight) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("weight", weight);

        root.child(currentUser.getUid()).setValue(userMap);
    }

    //write inputted age
    public void writeAge(int age) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("age", age);

        root.child(currentUser.getUid()).setValue(userMap);
    }

    //write inputted gender
    public void writeGender(String gender) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("gender", gender);

        root.child(currentUser.getUid()).setValue(userMap);
    }


}
