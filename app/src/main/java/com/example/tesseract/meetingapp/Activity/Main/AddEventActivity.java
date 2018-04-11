package com.example.tesseract.meetingapp.Activity.Main;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.datepicker.DatePickerBuilder;
import com.codetroopers.betterpickers.datepicker.DatePickerDialogFragment;
import com.example.tesseract.meetingapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    ConstraintLayout mainLayout;
    ConstraintLayout fragmentLayout;

    Button addNewMeeting;
    Button cancel;

    EditText meetingTopic;
    EditText meetingContacts;
    EditText meetingDate;
    EditText meetingStartTime;
    EditText meetingEndTime;
    EditText meetingLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        mainLayout = findViewById(R.id.main_layout);
        fragmentLayout = findViewById(R.id.fragment_layout);

        meetingTopic = findViewById(R.id.meeting_topic);
        meetingContacts = findViewById(R.id.meeting_contacts);
        meetingDate = findViewById(R.id.meeting_date);
        meetingStartTime = findViewById(R.id.meeting_start_time);
        meetingEndTime = findViewById(R.id.meeting_end_time);
        meetingLocation = findViewById(R.id.meeting_location);

        addNewMeeting = findViewById(R.id.create_meeting);
        cancel = findViewById(R.id.cancel_button);

        meetingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(AddEventActivity.this);
                cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });

        addNewMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        Log.e("log:", String.valueOf(year));
        Date date = new Date(year-1900,monthOfYear,dayOfMonth);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdfDate.format(date);
        meetingDate.setText(strDate);
    }

    @Override
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();
        CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = (CalendarDatePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialogFragment != null) {
            calendarDatePickerDialogFragment.setOnDateSetListener(this);
        }
    }
}
