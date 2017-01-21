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
        Gson json = new Gson();
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
        //to-do reschedule end


        //Daily timetable re-schedule
       /* sharedPreferences=context.getSharedPreferences("academicPrefs",MODE_PRIVATE);
        Gson jason=new Gson();
        Type type=new TypeToken<ArrayList<ArrayList<subject>>>(){}.getType();
        String timetab=sharedPreferences.getString("schedule",null);
        SharedPreferences settingprefs=context.getSharedPreferences(settings.SETTING_PREFS_NAME,Context.MODE_PRIVATE);
        if(settingprefs.getBoolean(settings.SHOW_NOTIF_KEY,true)) {
            if (timetab != null)
                vClass.timeTable = jason.fromJson(timetab, type);
            int n_id=1;
            for (int s = 0; s < vClass.timeTable.size(); s++) {
                List<subject> subs = vClass.timeTable.get(s);
                for (int t = 0; t < vClass.timeTable.get(s).size(); t++) {
                    subject sub = subs.get(t);
                    x = new Intent(context, NotifyService.class);
                    if (sub.type.equals("") != true) {
                        Calendar c = Calendar.getInstance();
                        int st_hr, st_min, ampm;
                        st_hr = Integer.parseInt(sub.startTime.substring(0, 2));
                        st_min = Integer.parseInt(sub.startTime.substring(3, 5));
                        String kk = sub.startTime.substring(6);
                        if (kk.equals("AM"))
                            ampm = 0;
                        else
                            ampm = 1;

                        c.set(Calendar.HOUR, st_hr);
                        c.set(Calendar.MINUTE, st_min);
                        c.set(Calendar.SECOND, 0);
                        c.set(Calendar.DAY_OF_WEEK, s + 2);
                        c.set(Calendar.AM_PM, ampm);
                        Notification_Holder nh = new Notification_Holder(c, sub.title + " " + sub.code, sub.room,"Upcoming class in 5 minutes");
                        Gson j = new Gson();
                        x.putExtra("one", j.toJson(nh));
                        x.putExtra("intent_chooser", "one");
                        PendingIntent pintent = PendingIntent.getBroadcast(context, n_id++, x, 0);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - 5 * 60 * 1000, 24 * 7 * 60 * 60 * 1000, pintent);

                    }
                }

            }
        }*/

        scrapper.createNotification(context);
        //Daily timetable reschedule end
    }
}
