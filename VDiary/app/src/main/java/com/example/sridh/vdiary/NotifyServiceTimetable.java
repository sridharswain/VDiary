package com.example.sridh.vdiary;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Sparsha Saha on 12/22/2016.
 */

public class NotifyServiceTimetable extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(scrapper.context);
        notificationbuilder.setContentTitle("test");
        notificationbuilder.setContentText("good");
        Intent inte=new Intent(context,scrapper.class);
        notificationbuilder.setSmallIcon(R.drawable.logo);
        notificationbuilder.setTicker("You have a class now");
        PendingIntent pendingIntent=PendingIntent.getActivity(scrapper.context,(int) System.currentTimeMillis(),inte,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationbuilder.setContentIntent(pendingIntent);
        notificationbuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0,notificationbuilder.build());

        Vibrator vib=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        if(vib.hasVibrator()) {
            vib.vibrate(400);
        }

    }
}
