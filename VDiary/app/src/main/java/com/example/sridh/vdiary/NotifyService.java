package com.example.sridh.vdiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;

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
        String fuck="false";





        Calendar calendar=Calendar.getInstance();
        SharedPreferences holidayPrefs= context.getSharedPreferences("holidayPrefs",Context.MODE_PRIVATE);
        String holidayJson = holidayPrefs.getString("holidays",null);



        if(holidayJson!=null){
            List<holiday> holidays= (new Gson()).fromJson(holidayJson,new TypeToken<List<holiday>>(){}.getType());
            for (holiday h :holidays){
                Calendar dateString =h.date;
                int day = dateString.get(Calendar.DAY_OF_MONTH);
                int month =dateString.get(Calendar.MONTH);
                int year =dateString.get(Calendar.YEAR);
                if(calendar.get(Calendar.DAY_OF_MONTH)==day && calendar.get(Calendar.MONTH)+1==month && calendar.get(Calendar.YEAR)==year){
                    holiday=true;
                    SharedPreferences.Editor edit=shared.edit();
                    edit.putBoolean("holiday",true);
                    edit.apply();
                    fuck="true";
                    break;
                }
            }
        }






        Calendar cal = Calendar.getInstance();
        Notification_Holder notifholder;
        Type t=new TypeToken<Notification_Holder>(){}.getType();
        String z=intent.getStringExtra("intent_chooser");
            Gson js = new Gson();
            notifholder = js.fromJson(intent.getStringExtra("one"), t);
        Log.d("tagthem1",notifholder.title+"   "+notifholder.cal.get(Calendar.HOUR)+"    "+notifholder.id);
        Calendar notiCalendar= notifholder.cal;
       if((notiCalendar.get(Calendar.DAY_OF_WEEK)==cal.get(Calendar.DAY_OF_WEEK) && notiCalendar.get(Calendar.HOUR_OF_DAY)>= cal.get(Calendar.HOUR_OF_DAY) && yes_no /*&& !holiday*/)) {
           Notification_Creator notifcreator = new Notification_Creator(notifholder.title, notifholder.content, notifholder.ticker, context);
            notifcreator.create_notification();
        }
    }
}
