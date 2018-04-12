package com.example.tesseract.meetingapp.Models;


public class UserMeetingStatus {
    private String phoneNumber;
    private int meetingStatus;

    public UserMeetingStatus() {
    }

    public UserMeetingStatus(String phoneNumber, int meetingStatus) {
        this.phoneNumber = phoneNumber;
        this.meetingStatus = meetingStatus;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getMeetingStatus() {
        return meetingStatus;
    }
}
