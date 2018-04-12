package com.example.tesseract.meetingapp.Activity.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.example.tesseract.meetingapp.Models.Meeting;
import com.example.tesseract.meetingapp.Models.UserMeetingStatus;
import com.example.tesseract.meetingapp.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddEventActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    Date date;
    int startTimeInSec;
    int endTimeInSec;

    ConstraintLayout mainLayout;
    ConstraintLayout fragmentLayout;

    @BindView(R.id.meeting_time)
    CrystalRangeSeekbar rangeSeekBar;
    @BindView(R.id.meeting_topic)
    EditText meetingTopic;
    @BindView(R.id.meeting_contacts)
    EditText meetingContacts;
    @BindView(R.id.meeting_date)
    EditText meetingDate;
    @BindView(R.id.meeting_location)
    EditText meetingLocation;
    @BindView(R.id.start_time)
    TextView startTime;
    @BindView(R.id.end_time)
    TextView endTime;


    @OnClick(R.id.create_meeting)
    public void addNewMeeting() {
        Date tempDate = date;
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(tempDate);
        tempCalendar.add(Calendar.MINUTE,startTimeInSec);

        Calendar tempCalendar2 = Calendar.getInstance();
        tempCalendar2.setTime(tempDate);
        tempCalendar2.add(Calendar.MINUTE,endTimeInSec);
        if(Calendar.getInstance().getTime().getTime()>tempCalendar.getTime().getTime()){
            Toast.makeText(this,"False time!",Toast.LENGTH_SHORT).show();
        }else{
            List<UserMeetingStatus> phoneNumbers = new ArrayList<>();
            UserMeetingStatus list = new UserMeetingStatus(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),1);
            UserMeetingStatus list2 = new UserMeetingStatus(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),2);
            UserMeetingStatus list3 = new UserMeetingStatus(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),3);
            phoneNumbers.add(list);
            phoneNumbers.add(list2);
            phoneNumbers.add(list3);
            String key = FirebaseDatabase.getInstance().getReference("meetings").push().getKey();
            FirebaseDatabase.getInstance().getReference().child("meetings").push()
                    .setValue(new Meeting(phoneNumbers,FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                            meetingTopic.getText().toString(),meetingLocation.getText().toString(), tempCalendar.getTime().getTime(), tempCalendar2.getTime().getTime(),  1));
            finish();
        }
    }

    @OnClick(R.id.cancel_button)
    public void cancel() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);
        mainLayout = findViewById(R.id.main_layout);
        //fragmentLayout = findViewById(R.id.fragment_layout);

        rangeSeekBar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            startTimeInSec = minValue.intValue();
            endTimeInSec = maxValue.intValue();
            int maxVal;
            if (minValue.intValue() == maxValue.intValue()) {
                maxVal = maxValue.intValue() + 30;
            } else {
                maxVal = maxValue.intValue();
            }
            startTime.setText("Start:\n" + minutesToHour(minValue.intValue()));
            endTime.setText("End:\n" + minutesToHour(maxVal));
        });

        meetingLocation.setOnClickListener(v -> {
            int PLACE_PICKER_REQUEST = 5353;
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(AddEventActivity.this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        });

        meetingDate.setOnClickListener(v -> {
            CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                    .setOnDateSetListener(AddEventActivity.this);
            cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5353) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                meetingLocation.setText(String.format("Place: %s", place.getName()));
            }
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        date = new Date(year - 1900, monthOfYear, dayOfMonth);
        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.DAY_OF_WEEK,-1);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdfDate.format(date);
        if(currentDate.getTime().getTime()>date.getTime()){
            Toast.makeText(this,"False date!",Toast.LENGTH_SHORT).show();
        }else {
            meetingDate.setText(strDate);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = (CalendarDatePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialogFragment != null) {
            calendarDatePickerDialogFragment.setOnDateSetListener(this);
        }
    }

    private String minutesToHour(int time) {
        return String.format("%02d:%02d", time / 60, time % 60);
    }

}
