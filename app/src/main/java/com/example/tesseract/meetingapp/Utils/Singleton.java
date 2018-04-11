package com.example.tesseract.meetingapp.Utils;

public class Singleton {
    private static Singleton instance;

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public String newMeetingTopic;
    public String newMeetingContacts;
    public String newMeetingDate;
    public String newMeetingStartTime;
    public String newMeetingEndTime;
    public String newMeetingLocation;


}