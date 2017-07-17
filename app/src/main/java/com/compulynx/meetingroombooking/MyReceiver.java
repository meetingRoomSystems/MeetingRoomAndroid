package com.compulynx.meetingroombooking;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * A receiver class that displays a notification 1 hour before an upcoming booking
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String fullname = intent.getExtras().getString("fullname");
        int room = intent.getExtras().getInt("room");
        int capacity = intent.getExtras().getInt("capacity");
        String message = fullname + " you have meeting in an hour at meeting room " + room + " with " + capacity + " people.";

//        create a notification object
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Meeting Reminder")
                        .setTicker("Meeting Reminder Alert!")
                        .setContentText("Meeting coming up")
                        .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(message));

        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
