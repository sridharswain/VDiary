package com.example.sridh.vdiary;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by Sparsha Saha on 12/22/2016.
 */

public class NotifyServiceTimetable extends BroadcastReceiver {
    NotificationCompat.Builder notification;
    Context con;

    public NotifyServiceTimetable(NotificationCompat.Builder notification,Context c)
    {
        this.notification=notification;
        con=c;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager=(NotificationManager)con.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification.build());
        Toast.makeText(context, "must ring", Toast.LENGTH_SHORT).show();

    }
}
