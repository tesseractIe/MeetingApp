package com.example.tesseract.meetingapp.Models;


public class UserMeetingStatus {
    private String phoneNumber;
    private String userName;
    private int meetingStatus;

    public UserMeetingStatus() {
    }

    public UserMeetingStatus(String phoneNumber, String userName, int meetingStatus) {
        this.phoneNumber = phoneNumber;
        this.meetingStatus = meetingStatus;
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getMeetingStatus() {
        return meetingStatus;
    }

    public String getUserName() {
        return userName;
    }
}
