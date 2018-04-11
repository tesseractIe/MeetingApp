package com.example.tesseract.meetingapp.Other;

import java.util.List;

public class MeetingUsers {

    private String key;
    private String creator;
    private List<User> users;

    public MeetingUsers(String key, String creator, List<User> users) {
        this.key = key;
        this.creator = creator;
        this.users = users;
    }

    public String getKey() {
        return key;
    }

    public String getCreator() {
        return creator;
    }

    public List<User> getUsers() {
        return users;
    }
}
