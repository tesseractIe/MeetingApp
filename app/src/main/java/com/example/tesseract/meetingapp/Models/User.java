package com.example.tesseract.meetingapp.Models;

public class User {
    private String phoneNumber;
    private MeetingID meetingIDList;

    public User(String phoneNumber, MeetingID meetingIDList) {
        this.phoneNumber = phoneNumber;
        this.meetingIDList = meetingIDList;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public MeetingID getMeetingIDList() {
        return meetingIDList;
    }
}
