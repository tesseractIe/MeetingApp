package com.example.tesseract.meetingapp.Activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.tesseract.meetingapp.Activity.Main.MeetingsActivity;
import com.example.tesseract.meetingapp.R;
import com.example.tesseract.meetingapp.Services.NotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if(!NotificationService.isInstanceCreated()){
                startService(new Intent(this, NotificationService.class));
            }
            Intent intent = new Intent(this, MeetingsActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
