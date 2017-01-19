package com.example.sridh.vdiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sparsha Saha on 12/12/2016.
 */

public class NotifyService extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences shared=context.getSharedPreferences("cancelnotif",Context.MODE_PRIVATE);
        boolean yes_no=shared.getBoolean("cancel_reset",true);
        boolean holiday=shared.getBoolean("holiday",false);

        SharedPreferences sharedPreferences=context.getSharedPreferences("holidayPrefs",Context.MODE_PRIVATE);
        String cocky=sharedPreferences.getString("holidays","");
        if(cocky.equals("")==false) {
            Gson jesson = new Gson();
            Type type = new TypeToken<List<holiday>>() {
            }.getType();
            vClass.holidays = jesson.fromJson(cocky, type);
        }

        Calendar today=Calendar.getInstance();
        for(int i=0;i<vClass.holidays.size();i++)
        {
            holiday h=vClass.holidays.get(i);
            if(h.date.get(Calendar.DAY_OF_MONTH)==today.get(Calendar.DAY_OF_MONTH) && h.date.get(Calendar.MONTH)==today.get(Calendar.MONTH) && h.date.get(Calendar.YEAR)==today.get(Calendar.YEAR))
            {
                holiday=true;
                SharedPreferences.Editor editor=shared.edit();
                editor.putBoolean("holiday",true);
                editor.apply();
                break;
            }
        }





        Calendar calendar = Calendar.getInstance();
        Notification_Holder notifholder;
        Type t=new TypeToken<Notification_Holder>(){}.getType();
        String z=intent.getStringExtra("intent_chooser");
            Gson js = new Gson();
            notifholder = js.fromJson(intent.getStringExtra("one"), t);
        Calendar notiCalendar= notifholder.cal;
       if((notiCalendar.get(Calendar.DAY_OF_WEEK)==calendar.get(Calendar.DAY_OF_WEEK) && notiCalendar.get(Calendar.HOUR_OF_DAY)>= calendar.get(Calendar.HOUR_OF_DAY) && yes_no && holiday)) {
            Notification_Creator notifcreator = new Notification_Creator(notifholder.title, notifholder.content, notifholder.ticker, context);
            notifcreator.create_notification();
        }
    }
}
