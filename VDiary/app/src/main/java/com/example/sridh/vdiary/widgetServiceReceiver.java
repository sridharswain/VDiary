package com.example.sridh.vdiary;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.List;

public class widgetServiceReceiver extends BroadcastReceiver {
    public widgetServiceReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, widget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_today);
        updateWidget(appWidgetManager,context,appWidgetIds);
    }

    void updateWidget(AppWidgetManager appWidgetManager,Context context,int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            Intent launchActivity = new Intent(context, splashScreen.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, 0);
            views.setOnClickPendingIntent(R.id.widget_status,pendingIntent);
            views.setPendingIntentTemplate(R.id.widget_today,pendingIntent);
            if (isLoggedIn(context)) {
                Calendar calendar = Calendar.getInstance();
                String occasion = readHolidayPrefs(context, calendar);
                if ((occasion == null)) {
                    int today = calendar.get(Calendar.DAY_OF_WEEK);
                    if (today > 1 && today < 7) {
                        Intent intent = new Intent(context, widgetService.class);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
                        views.setRemoteAdapter(R.id.widget_today, intent);
                        changeStatus(views,false);
                    }
                    else {
                        changeStatus(views,true);
                        views.setTextViewText(R.id.widget_status, "No Classes Today :)");
                    }
                } else {
                    changeStatus(views,true);
                    views.setTextViewText(R.id.widget_status, occasion);
                }
            } else {
                changeStatus(views,true);
                views.setTextViewText(R.id.widget_status, "Login to Zchedule to view today's schedule");
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    String readHolidayPrefs(Context context,Calendar calendar){
        SharedPreferences holidayPrefs= context.getSharedPreferences("holidayPrefs",Context.MODE_PRIVATE);
        String holidayJson = holidayPrefs.getString("holidays",null);
        if(holidayJson!=null){
            List<holiday> holidays= (new Gson()).fromJson(holidayJson,new TypeToken<List<holiday>>(){}.getType());
            for (holiday h :holidays){
                Calendar dateString =h.date;
                int day = dateString.get(Calendar.DAY_OF_MONTH);
                int month =dateString.get(Calendar.MONTH);
                int year =dateString.get(Calendar.YEAR);
                if(calendar.get(Calendar.DAY_OF_MONTH)==day && calendar.get(Calendar.MONTH)==month && calendar.get(Calendar.YEAR)==year){
                    return h.ocassion;
                }
            }
        }
        return null;
    }

    boolean isLoggedIn(Context context){
        SharedPreferences isLoggedIn = context.getSharedPreferences("isLoggedInPrefs",Context.MODE_PRIVATE);
        return isLoggedIn.getBoolean("isLoggedIn",false);
    }
    void changeStatus(RemoteViews views,boolean x){
        if(x){
            views.setViewVisibility(R.id.widget_status,View.VISIBLE);
            views.setViewVisibility(R.id.widget_today,View.INVISIBLE);
        }
        else{
            views.setViewVisibility(R.id.widget_status,View.INVISIBLE);
            views.setViewVisibility(R.id.widget_today,View.VISIBLE);
        }
    }
}
