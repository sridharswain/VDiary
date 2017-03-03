package com.example.sridh.vdiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static com.example.sridh.vdiary.prefs.isWidgetEnabled;
import static com.example.sridh.vdiary.prefs.put;

/**
 * Implementation of App Widget functionality. Sid
 */
public class widget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        (new widgetServiceReceiver()).onReceive(context,(new Intent(context,widgetServiceReceiver.class)));
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // Enter relevant functionality for when the first widget is created
        put(context,isWidgetEnabled,true);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent toWidgetService = new Intent(context,widgetServiceReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,toWidgetService,0);
        Calendar calendarMan = Calendar.getInstance();
        calendarMan.set(Calendar.HOUR,12);
        calendarMan.set(Calendar.MINUTE,0);
        calendarMan.set(Calendar.SECOND,0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarMan.getTimeInMillis(), 12 * 60 * 60 * 1000, pendingIntent);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
        put(context,isWidgetEnabled,false);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent toWidgetService = new Intent(context,widgetServiceReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,toWidgetService,0);
        alarmManager.cancel(pendingIntent);
    }
}

