package com.example.tesseract.meetingapp.Activity.Main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

    WeekView mWeekView;

    @OnClick(R.id.add_meeting)
    public void addMeeting() {
        startAddMeetingActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mettings);
        ButterKnife.bind(this);

        mWeekView = findViewById(R.id.weekView);

        mWeekView.setOnEventClickListener((event, eventRect) -> Toast.makeText(getApplicationContext(), event.getName() + " " + event.getLocation(), Toast.LENGTH_SHORT).show());

        mWeekView.setEventLongPressListener((event, eventRect) -> {

        });

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DAY_OF_WEEK, 1);

        Date today = new Date();
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
        List<Meeting> myMeetings = new ArrayList<>();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("meetings");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Meeting> list = new ArrayList<>();
                myMeetings.clear();
                //create new list
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String uId = ds.getKey();
                    Meeting meeting = ds.getValue(Meeting.class);
                    list.add(meeting);
                }

                for (int i = 0; i < list.size(); i++) {
                    List<UserMeetingStatus> list2 = list.get(i).getMeetingUsers();
                    Log.e("list: ",list2.toString());
                    for (int i2 = 0; i2 < list2.size(); i2++) {
                        if (list2.get(i2).getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                            Log.e("list: ",list.get(i).getUserPhoneNumber());
                            myMeetings.add(list.get(i));
                        }
                    }
                }
                mWeekView.notifyDatasetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mWeekView.setMonthChangeListener((newYear, newMonth) -> {
            List<WeekViewEvent> events = setEvents(myMeetings);
            return events;
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
                    // return blank list or empty list
                    List<WeekViewEvent> events = new ArrayList<>();
                    return events;
                }
            }
        });

        super.onResume();
    }

    private void startAddMeetingActivity() {
        Intent intent = new Intent(this, AddEventActivity.class);
        startActivity(intent);
    }

    private List<WeekViewEvent> setEvents(List<Meeting> myMeetings) {
        List<WeekViewEvent> list = new ArrayList<>();
        for (int i = 0; i < myMeetings.size(); i++) {
            Calendar start = Calendar.getInstance();
            start.setTime(new Date(myMeetings.get(i).getStartTime()));
            Calendar stop = Calendar.getInstance();
            stop.setTime(new Date(myMeetings.get(i).getEndTime()));
            WeekViewEvent newEvent = new WeekViewEvent(i, myMeetings.get(i).getMeetingTopic(), myMeetings.get(i).getLocation(), start, stop);
            newEvent.setColor(Color.rgb(0,255,100));
            list.add(newEvent);
        }
        return list;
    }

}
