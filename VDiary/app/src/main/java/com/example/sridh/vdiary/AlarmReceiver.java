package com.example.sridh.vdiary;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Sparsha Saha on 10/30/2016.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationmanager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification=new NotificationCompat.Builder(context);
        notification.setContentTitle("Test Content");
        notification.setContentText("Test content text");
        notification.setContentIntent(PendingIntent.getActivity(context,0,new Intent(context,workSpace.class),0));
        notificationmanager.notify(0,notification.build());
    }
}
