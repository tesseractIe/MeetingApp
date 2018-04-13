package com.example.tesseract.meetingapp.Activity.Main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;
import com.example.tesseract.meetingapp.Activity.Auth.LoginActivity;
import com.example.tesseract.meetingapp.Models.Meeting;
import com.example.tesseract.meetingapp.Models.UserMeetingStatus;
import com.example.tesseract.meetingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeetingsActivity extends AppCompatActivity {

    private WeekView mWeekView;
    private List<Meeting> myMeetings = new ArrayList<>();
    private UserMeetingStatus selectedMeeting;
    private List<String> uids = new ArrayList<>();

    @OnClick(R.id.add_meeting)
    public void addMeeting() {
        startAddMeetingActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mettings);
        ButterKnife.bind(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Confirm", (dialog, id) -> {
            if(selectedMeeting!=null){
                for (int i = 0;i<myMeetings.size();i++) {
                    for(int i2 = 0;i2<myMeetings.get(i).getMeetingUsers().size();i2++){
                        if(selectedMeeting.equals(myMeetings.get(i).getMeetingUsers().get(i2))){
                            FirebaseDatabase.getInstance().getReference().child("meetings").child(uids.get(i)).child("meetingUsers").child(String.valueOf(i2)).child("meetingStatus").setValue(2);
                        }
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, id) -> {
            if(selectedMeeting!=null){
                for (int i = 0;i<myMeetings.size();i++) {
                    for(int i2 = 0;i2<myMeetings.get(i).getMeetingUsers().size();i2++){
                        if(selectedMeeting.equals(myMeetings.get(i).getMeetingUsers().get(i2))){
                            FirebaseDatabase.getInstance().getReference().child("meetings").child(uids.get(i)).child("meetingUsers").child(String.valueOf(i2)).child("meetingStatus").setValue(3);
                        }
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();

        mWeekView = findViewById(R.id.weekView);
        mWeekView.setOnEventClickListener((WeekViewEvent event, RectF eventRect) -> {
            for (UserMeetingStatus u : myMeetings.get((int)event.getId()).getMeetingUsers()) {
                if(u.getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
                    if(u.getMeetingStatus()==1){
                        dialog.show();
                        selectedMeeting = u;
                        Toast.makeText(getApplicationContext(), event.getName() + " " + event.getLocation(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_btn:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MeetingsActivity.this, LoginActivity.class));
                finish();
        }
        return true;
    }

    @Override
    protected void onResume() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("meetings");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Meeting> list = new ArrayList<>();
                List<String> locuids = new ArrayList<>();
                myMeetings.clear();
                uids.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    locuids.add(ds.getKey());
                    Meeting meeting = ds.getValue(Meeting.class);
                    list.add(meeting);
                }
                for (int i = 0; i < list.size(); i++) {
                    List<UserMeetingStatus> list2 = list.get(i).getMeetingUsers();
                    for (int i2 = 0; i2 < list2.size(); i2++) {
                        if (list2.get(i2).getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                            uids.add(i,locuids.get(i));
                            myMeetings.add(i, list.get(i));
                        }
                    }
                }
                mWeekView.notifyDatasetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mWeekView.setWeekViewLoader(new WeekViewLoader() {
            int i = 1;

            @Override
            public double toWeekViewPeriodIndex(Calendar instance) {
                return instance.getTime().getDate() + instance.getTime().getDate();
            }

            @Override
            public List<WeekViewEvent> onLoad(int periodIndex) {
                if (i == 3) {
                    i = 1;
                    List<WeekViewEvent> events = setEvents(myMeetings);
                    return events;
                } else {
                    i++;
                    List<WeekViewEvent> events = new ArrayList<>();
                    return events;
                }
            }
        });
        mWeekView.setMonthChangeListener((newYear, newMonth) -> {
            List<WeekViewEvent> eventsMonth = new ArrayList<>();
            List<WeekViewEvent> events = setEvents(myMeetings);
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getStartTime().get(Calendar.MONTH) == newMonth) {
                    eventsMonth.add(events.get(i));
                }
            }
            return eventsMonth;
        });

        super.onResume();
    }

    private void startAddMeetingActivity() {
        Intent intent = new Intent(this, AddEventActivity.class);
        startActivity(intent);
    }

    private List<WeekViewEvent> setEvents(List<Meeting> myMeetings) {
        List<WeekViewEvent> list = new ArrayList<>();
        Log.e("sizeMyMeetingDB: ", String.valueOf(myMeetings.size()));
        for (int i = 0; i < myMeetings.size(); i++) {
            Calendar start = Calendar.getInstance();
            start.setTime(new Date(myMeetings.get(i).getStartTime()));
            Calendar stop = Calendar.getInstance();
            stop.setTime(new Date(myMeetings.get(i).getEndTime()));
            WeekViewEvent newEvent = new WeekViewEvent(i, myMeetings.get(i).getMeetingTopic(), myMeetings.get(i).getLocation(), start, stop);
            for (UserMeetingStatus u : myMeetings.get(i).getMeetingUsers()) {
                if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().equals(u.getPhoneNumber())) {
                    switch (u.getMeetingStatus()) {
                        case 1:
                            newEvent.setColor(Color.rgb(225, 255, 0));
                            break;
                        case 2:
                            newEvent.setColor(Color.rgb(0, 255, 0));
                            break;
                        case 3:
                            newEvent.setColor(Color.rgb(225, 0, 0));
                            break;
                    }
                }
            }
            list.add(newEvent);
        }
        return list;
    }

}
