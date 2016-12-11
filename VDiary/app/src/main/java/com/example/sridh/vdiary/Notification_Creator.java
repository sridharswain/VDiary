package com.example.sridh.vdiary;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Sparsha Saha on 12/7/2016.
 */

public class Notification_Creator {
    String title;
    String name_and_teachersname;
    String timings;
    Context context;
    public static PendingIntent pintent;

    public Notification_Creator(String a,String b,String c,Context x)
    {
        title=a;
        name_and_teachersname=b;
        timings=c;
        context=x;
    }

    public void create_notification()
    {
        NotificationCompat.Builder notibuilder=new NotificationCompat.Builder(context);
        notibuilder.setContentTitle(title);
        notibuilder.setContentText(name_and_teachersname+'\n'+timings);
        notibuilder.setSmallIcon(R.drawable.logo);
        Intent newIntent=new Intent(context,schedule.class);
        pintent= PendingIntent.getActivity(context, (int) System.currentTimeMillis(),newIntent,0);
        notibuilder.setContentIntent(pintent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0,notibuilder.build());

        Vibrator vib=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        if(vib.hasVibrator()) {
            vib.vibrate(400);
        }

    }



}
