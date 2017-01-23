package com.example.sridh.vdiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sparsha Saha on 1/11/2017.
 */

public class AtBoot extends BroadcastReceiver {
    SharedPreferences sharedPreferences;
    AlarmManager alarmManager;
    Intent x;
    PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences("todoshared", MODE_PRIVATE);
        String f = sharedPreferences.getString("todolist", null);
        alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(f!=null)
            vClass.notes = Notification_Holder.convert_from_jason(f);

        //to-do reschedule
        for (int i=0;i<vClass.notes.size();i++)
        {
            x=new Intent(context,NotifyService.class);
            Gson js=new Gson();
            Notification_Holder n=vClass.notes.get(i);
            String m=js.toJson(n);
            x.putExtra("one",m);
            pendingIntent=PendingIntent.getBroadcast(context,vClass.notes.get(i).id,x,0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, vClass.notes.get(i).cal.getTimeInMillis(),pendingIntent);
        }
        scrapper.createNotification(context);
        //Daily timetable reschedule end
    }
}
