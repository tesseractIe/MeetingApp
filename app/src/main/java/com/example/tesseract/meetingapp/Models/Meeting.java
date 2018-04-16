package com.example.tesseract.meetingapp.Models;

import java.util.List;

public class Meeting {

    private List<UserMeetingStatus> meetingUsers;
    private String userPhoneNumber;
    private String meetingTopic;
    private String location;
    private long startTime;
    private long endTime;
    private int state;

    public Meeting() {
    }

    public Meeting(List<UserMeetingStatus> meetingUsers, String userPhoneNumber, String meetingTopic, String location, long startTime, long endTime, int state) {
        this.meetingUsers = meetingUsers;
        this.userPhoneNumber = userPhoneNumber;
        this.meetingTopic = meetingTopic;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = state;
    }

    public List<UserMeetingStatus> getMeetingUsers() {
        return meetingUsers;
    }

    public UserMeetingStatus getMeetingUserByPhoneNumber(String phoneNumber){
        for (UserMeetingStatus u: meetingUsers){
            if(u.getPhoneNumber().equals(phoneNumber)){
                return u;
            }
        }
        return null;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public String getMeetingTopic() {
        return meetingTopic;
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
