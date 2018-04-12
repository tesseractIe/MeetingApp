package com.example.tesseract.meetingapp.Models;

public class User {
    private String phoneNumber;
    private String nickname;

    public User() {
    }

    public User(String phoneNumber, String nickname) {

        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
