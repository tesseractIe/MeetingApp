package com.example.tesseract.meetingapp.Activity.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.example.tesseract.meetingapp.Models.Meeting;
import com.example.tesseract.meetingapp.Models.MeetingID;
import com.example.tesseract.meetingapp.Models.User;
import com.example.tesseract.meetingapp.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddEventActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

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
        String key = FirebaseDatabase.getInstance().getReference("meetings").push().getKey();
        FirebaseDatabase.getInstance().getReference().child("meetings").push()
                .setValue(new Meeting(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                        meetingTopic.getText().toString(), key, 1523439493, 1523439493, "shop", 1));
        List<User> list = new ArrayList<>();
        list.add(new User(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),new MeetingID(key)));
        for(int i = 0;i<list.size();i++){
            FirebaseDatabase.getInstance().getReference().child("users").child(list.get(i).getPhoneNumber()).child("meetings").push()
                    .setValue(new MeetingID(key));
        }
        finish();
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
            int maxVal;
            if (minValue.intValue() == maxValue.intValue()) {
                maxVal = maxValue.intValue() + 30;
            } else {
                maxVal = maxValue.intValue();
            }
            startTime.setText("Start:\n" + secondsToString(minValue.intValue()));
            endTime.setText("End:\n" + secondsToString(maxVal));
        });

        meetingLocation.setOnClickListener(v -> startPlacePicker());

        meetingDate.setOnClickListener(v -> {
            CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                    .setOnDateSetListener(AddEventActivity.this);
            cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
        });
    }

    private void startPlacePicker() {
        int PLACE_PICKER_REQUEST = 5353;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(AddEventActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    private String secondsToString(int pTime) {
        return String.format("%02d:%02d", pTime / 60, pTime % 60);
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
        Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdfDate.format(date);
        meetingDate.setText(strDate);
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
}
