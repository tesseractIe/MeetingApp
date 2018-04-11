package com.example.tesseract.meetingapp.Other;

import java.util.List;

public class Meeting {

    private String             meetingId;
    private String          userPhoneNumber;
    private String          meetingTopic;
    private long            startTime;
    private long            endTime;
    private String          location;
    private int             state;

    public Meeting(String userPhoneNumber, String meetingTopic, String meetingId, long startTime, long endTime, String location, int state) {
        this.userPhoneNumber = userPhoneNumber;
        this.meetingTopic = meetingTopic;
        this.meetingId = meetingId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.state = state;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public String getMeetingTopic() {
        return meetingTopic;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public int getState() {
        return state;
    }
}
