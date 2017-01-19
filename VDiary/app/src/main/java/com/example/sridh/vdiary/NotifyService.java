package com.example.sridh.vdiary;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;

/**
 * Created by Sparsha Saha on 12/12/2016.
 */

public class NotifyService extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        Notification_Holder notifholder;
        Type t=new TypeToken<Notification_Holder>(){}.getType();
        String z=intent.getStringExtra("intent_chooser");
            Gson js = new Gson();
            notifholder = js.fromJson(intent.getStringExtra("one"), t);
        Calendar notiCalendar= notifholder.cal;
       if((notiCalendar.get(Calendar.DAY_OF_WEEK)==calendar.get(Calendar.DAY_OF_WEEK) && notiCalendar.get(Calendar.HOUR)== calendar.get(Calendar.HOUR))) {
            Notification_Creator notifcreator = new Notification_Creator(notifholder.title, notifholder.content, notifholder.ticker, context);
            notifcreator.create_notification();
        }
    }
}
