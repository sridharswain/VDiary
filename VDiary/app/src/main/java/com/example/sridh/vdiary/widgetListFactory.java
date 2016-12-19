package com.example.sridh.vdiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class widgetListFactory implements RemoteViewsService.RemoteViewsFactory{
    Context ctxt=null;
    int today;
    List<subject> todaySchedule;
    List<List<subject>> timeTable;
    public widgetListFactory(Context c, Intent intent){
        ctxt=c;
        //appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        today=Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            SharedPreferences prefs=ctxt.getSharedPreferences("academicPrefs",Context.MODE_PRIVATE);
            String scheduleJson=prefs.getString("schedule",null);
            if(scheduleJson!=null){
                Gson g= new Gson();
                timeTable=g.fromJson(scheduleJson,new TypeToken<ArrayList<ArrayList<subject>>>() {}.getType());
                todaySchedule=timeTable.get(today-2);
            }
    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return todaySchedule.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(ctxt.getPackageName(),R.layout.rowview_widget);
        subject session=todaySchedule.get(position);
        row.setTextViewText(R.id.widget_title,session.title);
        row.setTextViewText(R.id.widget_type,session.type);
        row.setTextViewText(R.id.widget_startTime,session.startTime);
        row.setTextViewText(R.id.widget_endTime,session.endTime);
        if(session.type.equals("")){
            row.setTextColor(R.id.widget_title,ctxt.getResources().getColor(R.color.Slight_white_orange));
            row.setTextViewTextSize(R.id.widget_title,1,15);
            row.setTextColor(R.id.widget_startTime,ctxt.getResources().getColor(R.color.Slight_white_orange));
            row.setTextColor(R.id.widget_endTime,ctxt.getResources().getColor(R.color.Slight_white_orange));
        }
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return (new RemoteViews(ctxt.getPackageName(),R.layout.activity_login));
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
