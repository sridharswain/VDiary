package com.example.sridh.vdiary;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class widget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            boolean setSchedule= true;
            String ocassion ="";
            RemoteViews views= new RemoteViews(context.getPackageName(),R.layout.widget);
            SharedPreferences holidayPrefs= context.getSharedPreferences("holidayPrefs",Context.MODE_PRIVATE);
            String holidayJson = holidayPrefs.getString("holidays",null);
            Calendar calendar= Calendar.getInstance();
            /*if(holidayJson!=null){
                List<holiday> holidays= (new Gson()).fromJson(holidayJson,new TypeToken<List<holiday>>(){}.getType());
                for (holiday h :holidays){
                    Calendar dateString =h.date;
                    int day = dateString.get(Calendar.DAY_OF_MONTH);
                    int month =dateString.get(Calendar.MONTH);
                    int year =dateString.get(Calendar.YEAR);
                    if(calendar.get(Calendar.DAY_OF_MONTH)==day && calendar.get(Calendar.MONTH)+1==month && calendar.get(Calendar.YEAR)==year){
                        setSchedule=false;
                        ocassion=h.ocassion;
                        break;
                    }
                }
            }
            else{
               ocassion="Login to Zchedule to see today's schedule";setSchedule=false;
            }*/
            //updateAppWidget(context, appWidgetManager, appWidgetId);
            if(setSchedule) {
                int today = calendar.get(Calendar.DAY_OF_WEEK);
                if (today > 1 && today < 7) {
                    Intent intent = new Intent(context, widgetService.class);
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
                    views.setRemoteAdapter(R.id.widget_today, intent);
                    views.setViewVisibility(R.id.widget_status, View.INVISIBLE);
                } else {
                    views.setTextViewText(R.id.widget_status, "No Classes Today :)");
                }
            }
            else{
                views.setTextViewText(R.id.widget_status,ocassion);
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

