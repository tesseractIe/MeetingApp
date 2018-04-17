package com.example.tesseract.meetingapp.Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.tesseract.meetingapp.Models.Meeting;
import com.example.tesseract.meetingapp.Models.UserMeetingStatus;
import com.example.tesseract.meetingapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationService extends Service {

    private static NotificationService instance = null;
    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public void onCreate() {
        instance = this;
        FirebaseDatabase.getInstance().getReference().child("meetings").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Meeting meeting = dataSnapshot.getValue(Meeting.class);
                StringBuilder contentText = new StringBuilder();
                contentText.append("Status: ");
                for (UserMeetingStatus u : meeting.getMeetingUsers()) {
                    contentText.append(u.getUserName()).append(" : ");
                    if (u.getMeetingStatus() == 1) {
                        contentText.append("o").append("|\n");
                    }
                    if (u.getMeetingStatus() == 2) {
                        contentText.append("v").append("|\n");
                    }
                    if (u.getMeetingStatus() == 3) {
                        contentText.append("x").append("|\n");
                    }
                }
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(NotificationService.this)
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle(meeting.getMeetingTopic())
                                .setContentText(contentText);
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + meeting.getLocation());
                Intent resultIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                resultIntent.setPackage("com.google.android.apps.maps");
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(dataSnapshot.getKey().hashCode(), mBuilder.build());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
