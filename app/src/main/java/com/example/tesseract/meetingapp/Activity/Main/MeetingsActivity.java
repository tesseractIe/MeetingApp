package com.example.tesseract.meetingapp.Activity.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.tesseract.meetingapp.Activity.Auth.LoginActivity;
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

        mWeekView.setMonthChangeListener((newYear, newMonth) -> {
            List<WeekViewEvent> events = getEvents(newYear, newMonth);
            return events;
        });

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
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
  //              Log.e("phone: ",user.getPhoneNumber());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        super.onResume();
    }

    private void startAddMeetingActivity() {
        Intent intent = new Intent(this, AddEventActivity.class);
        startActivity(intent);
    }

    private List<WeekViewEvent> getEvents(int newYear, int newMonth) {
        List<WeekViewEvent> list = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        Calendar stop = Calendar.getInstance();
        stop.add(Calendar.HOUR, 1);
        WeekViewEvent day1 = new WeekViewEvent(1, "new event", "shop", start, stop);
        list.add(day1);

        return list;
    }
}
