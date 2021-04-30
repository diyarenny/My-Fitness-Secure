package com.example.myfitnesssecure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class NewTaskAct extends AppCompatActivity {

    //variables
    TextView titlepage, addtitle, adddesc, adddate;
    EditText title, desc, date;
    Button btnSaveTask, btnCancel;
    DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    Integer num = new Random().nextInt();
    String key = Integer.toString(num);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        //assigning id's
        titlepage = findViewById(R.id.titlepage);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        addtitle = findViewById(R.id.addtitle);
        adddesc = findViewById(R.id.adddesc);
        adddate = findViewById(R.id.adddate);

        title = findViewById(R.id.titledoes);
        desc = findViewById(R.id.descdoes);
        date = findViewById(R.id.datedoes);

        btnSaveTask = findViewById(R.id.btnSaveTask);
        btnCancel = findViewById(R.id.btnCancel);

        //save reminder to database
        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // insert data to database
                reference = FirebaseDatabase.getInstance().getReference().child("Reminders").
                        child("Workout Reminders" + num);

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //saves data to firebase
                        dataSnapshot.getRef().child("title").setValue(title.getText().toString());
                        dataSnapshot.getRef().child("desc").setValue(desc.getText().toString());
                        dataSnapshot.getRef().child("date").setValue(date.getText().toString());
                        dataSnapshot.getRef().child("key").setValue(key);

                        Intent a = new Intent(NewTaskAct.this,ScheduleActivity.class);
                        startActivity(a);
                        postNotification();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void postNotification(){
        String message = "New Reminder added by " + currentUser.getDisplayName();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(NewTaskAct.this)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle("New Notification")
                .setContentText(message)
                .setAutoCancel(true);
        Intent intent = new Intent(NewTaskAct.this, NotificationReminders.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getActivity(NewTaskAct.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent i = new Intent(NewTaskAct.this, ScheduleActivity.class);
        startActivity(i);
    }
}