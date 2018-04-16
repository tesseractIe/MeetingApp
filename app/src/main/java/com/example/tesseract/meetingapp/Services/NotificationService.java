package com.example.tesseract.meetingapp.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.tesseract.meetingapp.Models.Meeting;
import com.example.tesseract.meetingapp.Models.UserMeetingStatus;
import com.example.tesseract.meetingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends Service {


    private static NotificationService instance = null;
    private List<Meeting> myOldMeetings = new ArrayList<>();
    private List<Meeting> myUpdateMeetings = new ArrayList<>();

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public void onCreate() {
        instance = this;
        FirebaseDatabase.getInstance().getReference().child("meetings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {//get all updated meetings
                    myUpdateMeetings.add(ds.getValue(Meeting.class));
                }
                if(myOldMeetings.size()>0){

                    StringBuilder contentText = new StringBuilder();
                    contentText.append("Status: ");
                    for (UserMeetingStatus u : myOldMeetings.get(0).getMeetingUsers()) {
                        contentText.append(u.getUserName()).append(" : ");
                        if (u.getMeetingStatus() == 1) {
                            contentText.append("o").append("\n");
                        }
                        if (u.getMeetingStatus() == 2) {
                            contentText.append("v").append("\n");
                        }
                        if (u.getMeetingStatus() == 3) {
                            contentText.append("x").append("\n");
                        }
                    }
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(NotificationService.this)
                                    .setSmallIcon(R.drawable.ic_launcher_background)
                                    .setContentTitle(myOldMeetings.get(0).getMeetingTopic())
                                    .setContentText(contentText);
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + myOldMeetings.get(0).getLocation());
                    Intent resultIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    resultIntent.setPackage("com.google.android.apps.maps");
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(NotificationService.this);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(543654654, mBuilder.build());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }
}
