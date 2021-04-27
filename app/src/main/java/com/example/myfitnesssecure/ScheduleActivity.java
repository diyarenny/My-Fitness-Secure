package com.example.myfitnesssecure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {

    //variables
    private Button btnAddNew;
    private TextView titlepage, subtitlepage, endpage;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference reference;
    private RecyclerView reminders;
    private ArrayList<MyReminders> myReminders;
    private ReminderAdapter reminderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        //assignng id's
        btnAddNew = (Button) findViewById(R.id.btnAddNew);
        titlepage = findViewById(R.id.titlepage);
        subtitlepage = findViewById(R.id.subtitlepage);
        endpage = findViewById(R.id.endpage);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //button add new task
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScheduleActivity.this, NewTaskAct.class));
            }
        });

        // working with data
        reminders = findViewById(R.id.reminders);
        reminders.setLayoutManager(new LinearLayoutManager(this));
        myReminders = new ArrayList<MyReminders>();

        //get data from firebase
        reference = FirebaseDatabase.getInstance().getReference().child("Reminders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // set code to retrieve data and replace layout
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    MyReminders p = dataSnapshot1.getValue(MyReminders.class);
                    myReminders.add(p);
                }
                reminderAdapter = new ReminderAdapter(ScheduleActivity.this, myReminders);
                reminders.setAdapter(reminderAdapter);
                reminderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // set code to show an error
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}