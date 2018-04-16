package com.example.tesseract.meetingapp.Activity.Main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;
import com.example.tesseract.meetingapp.Activity.Auth.LoginActivity;
import com.example.tesseract.meetingapp.Models.Meeting;
import com.example.tesseract.meetingapp.Models.MeetingStatus;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeetingsActivity extends AppCompatActivity {

    private List<Meeting> myMeetings = new ArrayList<>();
    private List<String> uids = new ArrayList<>();
    private UserMeetingStatus selectedMeeting;
    private DatabaseReference db;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;

    @BindView(R.id.weekView)
    WeekView mWeekView;

    @BindView(R.id.add_meeting)
    FloatingActionButton addButton;

    @OnClick(R.id.add_meeting)
    public void addMeeting() {
        startAddMeetingActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mettings);
        ButterKnife.bind(this);

        initViews();
        initAlertDialog();
        initViewsListeners();
        initFirebase();
        db = FirebaseDatabase.getInstance().getReference();
    }

    private void initViews() {
        addButton.hide();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.connecting));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    private void initFirebase() {
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (!connected) {
                    addButton.hide();
                } else {
                    progressDialog.hide();
                    addButton.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    /**
     * Alert dialog initialization
     */
    private void initAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.confirm, (dialog, id) -> {
            setSelectedMeetingStatus(MeetingStatus.MEETING_CONFIRMED);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            setSelectedMeetingStatus(MeetingStatus.MEETING_CANCELED);
        });
        dialog = builder.create();
    }

    /**
     * Views listeners initialization
     */
    private void initViewsListeners() {
        mWeekView.setOnEventClickListener((WeekViewEvent event, RectF eventRect) -> {
            for (UserMeetingStatus u : myMeetings.get((int) event.getId()).getMeetingUsers()) {
                if (u.getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                    if (!myMeetings.get((int) event.getId()).getUserPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                        dialog.show();
                        selectedMeeting = u;
                    }
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
    }


    /**
     * Change selected meeting status.
     *
     * @param status - Meeting status
     */
    private void setSelectedMeetingStatus(MeetingStatus status) {
        if (selectedMeeting != null) {
            for (int i = 0; i < myMeetings.size(); i++) {
                for (int i2 = 0; i2 < myMeetings.get(i).getMeetingUsers().size(); i2++) {
                    if (selectedMeeting.equals(myMeetings.get(i).getMeetingUsers().get(i2))) {
                        switch (status) {
                            case MEETING_CANCELED:
                                db.child("meetings").child(uids.get(i)).child("meetingUsers")
                                        .child(String.valueOf(i2)).child("meetingStatus").setValue(3);
                                break;
                            case MEETING_CONFIRMED:
                                db.child("meetings").child(uids.get(i)).child("meetingUsers")
                                        .child(String.valueOf(i2)).child("meetingStatus").setValue(2);
                                break;
                        }
                    }
                }
            }
        }
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
            case R.id.log_out_btn://logout button
                FirebaseAuth.getInstance().signOut();//end firebase session
                startActivity(new Intent(MeetingsActivity.this, LoginActivity.class));//launch start
                finish();
        }
        return true;
    }

    @Override
    protected void onResume() {
        db.child("meetings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Meeting> list = new ArrayList<>();
                List<String> locuids = new ArrayList<>();
                myMeetings.clear();
                uids.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {//get all updated meetings
                    Meeting meeting = ds.getValue(Meeting.class);
                    locuids.add(ds.getKey());
                    list.add(meeting);
                }
                for (int i = 0; i < list.size(); i++) {//get my meetings
                    List<UserMeetingStatus> list2 = list.get(i).getMeetingUsers();
                    for (int i2 = 0; i2 < list2.size(); i2++) {
                        if (list2.get(i2).getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                            uids.add(i, locuids.get(i));
                            myMeetings.add(i, list.get(i));
                        }
                    }
                }
                mWeekView.notifyDatasetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(this.getClass().getName(), databaseError.getDetails());
            }
        });

        mWeekView.setWeekViewLoader(new WeekViewLoader() {//three view fix
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
        super.onResume();
    }

    /**
     * Start add meeting activity. don`t finish this activity.
     */
    private void startAddMeetingActivity() {
        Intent intent = new Intent(this, AddEventActivity.class);
        startActivity(intent);
    }

    /**
     * @param myMeetings
     * @return - list of weekView events
     */
    private List<WeekViewEvent> setEvents(List<Meeting> myMeetings) {
        List<WeekViewEvent> list = new ArrayList<>();
        for (int i = 0; i < myMeetings.size(); i++) {
            Calendar start = Calendar.getInstance();
            start.setTime(new Date(myMeetings.get(i).getStartTime()));
            Calendar stop = Calendar.getInstance();
            stop.setTime(new Date(myMeetings.get(i).getEndTime()));
            StringBuilder usersStatus = new StringBuilder();

            for (UserMeetingStatus u : myMeetings.get(i).getMeetingUsers()) {
                usersStatus.append(u.getUserName()).append(" : ");
                if (u.getMeetingStatus() == 1) {
                    usersStatus.append("o").append("\n");
                }
                if (u.getMeetingStatus() == 2) {
                    usersStatus.append("v").append("\n");
                }
                if (u.getMeetingStatus() == 3) {
                    usersStatus.append("x").append("\n");
                }
            }
            WeekViewEvent newEvent = new WeekViewEvent(i, myMeetings.get(i).getMeetingTopic(), myMeetings.get(i).getLocation()
                    + "\n" + usersStatus, start, stop);
            for (UserMeetingStatus u : myMeetings.get(i).getMeetingUsers()) {

                if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().equals(u.getPhoneNumber())) {
                    switch (u.getMeetingStatus()) {
                        case 1:
                            newEvent.setColor(Color.rgb(255, 153, 51));
                            break;
                        case 2:
                            newEvent.setColor(Color.rgb(51, 204, 51));
                            break;
                        case 3:
                            newEvent.setColor(Color.rgb(153, 0, 0));
                            break;
                    }
                }
            }
            list.add(newEvent);
        }
        return list;
    }
}
