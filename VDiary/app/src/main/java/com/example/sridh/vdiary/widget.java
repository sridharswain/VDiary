package com.example.sridh.vdiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.List;

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
        SharedPreferences.Editor widgetPrefsEditor=context.getSharedPreferences("widgetPrefs",Context.MODE_PRIVATE).edit();
        widgetPrefsEditor.putBoolean("isEnabled",true);
        widgetPrefsEditor.apply();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent toWidgetService = new Intent(context,widgetServiceReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,toWidgetService,0);
        Calendar calendarMan = Calendar.getInstance();
        calendarMan.set(Calendar.HOUR,12);
        calendarMan.set(Calendar.MINUTE,0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarMan.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
        SharedPreferences.Editor widgetPrefsEditor=context.getSharedPreferences("widgetPrefs",Context.MODE_PRIVATE).edit();
        widgetPrefsEditor.putBoolean("isEnabled",false);
        widgetPrefsEditor.commit();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent toWidgetService = new Intent(context,widgetServiceReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,toWidgetService,0);
        alarmManager.cancel(pendingIntent);
    }
}

