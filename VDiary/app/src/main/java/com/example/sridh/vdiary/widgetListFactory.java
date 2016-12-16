package com.example.sridh.vdiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class widgetListFactory implements RemoteViewsService.RemoteViewsFactory{
    private Context ctxt=null;
    private int today;
    List<subject> todaySchedule;
    List<List<subject>> timeTable;
    public widgetListFactory(Context c, Intent intent){
        ctxt=c;
        //appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        today= Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2;
        if(today<=4){
            SharedPreferences prefs=ctxt.getSharedPreferences("academicPrefs",Context.MODE_PRIVATE);
            String scheduleJson=prefs.getString("schedule",null);
            if(scheduleJson!=null){
                Gson g= new Gson();
                timeTable=g.fromJson(scheduleJson,new TypeToken<ArrayList<ArrayList<subject>>>() {}.getType());
                todaySchedule=timeTable.get(today);
            }
        }
        else{
                todaySchedule=null;
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
        row.setTextViewText(android.R.id.text1,todaySchedule.get(position).title);
        row.setTextViewText(android.R.id.text2,todaySchedule.get(position).type);
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
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
