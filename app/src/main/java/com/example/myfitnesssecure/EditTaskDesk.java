package com.example.myfitnesssecure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditTaskDesk extends AppCompatActivity {

    //variables
    private EditText titleDoes, descDoes, dateDoes;
    private Button btnSaveUpdate, btnDelete;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task_desk);

        titleDoes = findViewById(R.id.titledoes);
        descDoes = findViewById(R.id.descdoes);
        dateDoes = findViewById(R.id.datedoes);
        btnSaveUpdate = findViewById(R.id.btnSaveUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //get values from previous page
        titleDoes.setText(getIntent().getStringExtra("title"));
        descDoes.setText(getIntent().getStringExtra("desc"));
        dateDoes.setText(getIntent().getStringExtra("date"));

        final String keyRem = getIntent().getStringExtra("key");
        reference = FirebaseDatabase.getInstance().getReference().child("Reminders").
               child("Workout Reminders" + keyRem);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent e = new Intent(EditTaskDesk.this,ScheduleActivity.class);
                            startActivity(e);
                        }
                        else {
                            Toast.makeText(EditTaskDesk.this,"Failed to Delete Reminder", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        //Save Button
        btnSaveUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //saves data to firebase
                        dataSnapshot.getRef().child("title").setValue(titleDoes.getText().toString());
                        dataSnapshot.getRef().child("desc").setValue(descDoes.getText().toString());
                        dataSnapshot.getRef().child("date").setValue(dateDoes.getText().toString());
                        dataSnapshot.getRef().child("key").setValue(keyRem);

                        Intent a = new Intent(EditTaskDesk.this,ScheduleActivity.class);
                        startActivity(a);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}