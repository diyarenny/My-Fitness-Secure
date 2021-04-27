package com.example.myfitnesssecure;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder>{

    Context context;
    ArrayList<MyReminders> myReminders;

    public ReminderAdapter(Context c, ArrayList<MyReminders> p) {
        context = c;
        myReminders = p;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.each_reminder,viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        myViewHolder.title.setText(myReminders.get(i).getTitle());
        myViewHolder.desc.setText(myReminders.get(i).getDesc());
        myViewHolder.date.setText(myReminders.get(i).getDate());

        //passes data to edit task activity
        final String getTitle = myReminders.get(i).getTitle();
        final String getDesc = myReminders.get(i).getDesc();
        final String getDate = myReminders.get(i).getDate();
        final String getKey = myReminders.get(i).getKey();

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aa = new Intent(context,EditTaskDesk.class);
                aa.putExtra("title", getTitle);
                aa.putExtra("desc", getDesc);
                aa.putExtra("date", getDate);
                aa.putExtra("key", getKey);
                context.startActivity(aa);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myReminders.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc, date, key;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titledoes);
            desc = (TextView) itemView.findViewById(R.id.descdoes);
            date = (TextView) itemView.findViewById(R.id.datedoes);
        }
    }

}