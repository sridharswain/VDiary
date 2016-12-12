package com.example.sridh.vdiary;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by Sparsha Saha on 12/7/2016.
 */

public class Notification_Creator {
    String title;
    String name_and_teachersname;
    Calendar timings;
    Context context;
    public static PendingIntent pintent;

    public Notification_Creator(String titl, String cont, Calendar c, Context x) {
        title=titl;
        name_and_teachersname=cont;
        timings=c;
        context=x;
    }

    public void create_notification() {
        NotificationCompat.Builder notibuilder=new NotificationCompat.Builder(context);
        notibuilder.setContentTitle(title);
        notibuilder.setContentText(name_and_teachersname);
        notibuilder.setSmallIcon(R.drawable.logo);
        notibuilder.setTicker("You have a deadline to meet");
        Intent newIntent=new Intent(context,scrapper.class);
        pintent= PendingIntent.getActivity(context, (int) System.currentTimeMillis(),newIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        notibuilder.setContentIntent(pintent);
        notibuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0,notibuilder.build());

        Vibrator vib=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        if(vib.hasVibrator()) {
            vib.vibrate(400);
        }
    }
}
