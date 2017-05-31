package com.example.sridh.vdiary;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.example.sridh.vdiary.prefs.schedule;
import static com.example.sridh.vdiary.prefs.get;
import static com.example.sridh.vdiary.prefs.showAttendanceOnwidget;

public class widgetListFactory implements RemoteViewsService.RemoteViewsFactory {
    Context context = null;
    int today;
    List<subject> todaySchedule;
    List<List<subject>> timeTable;
    boolean shouldShowAttendance;

    public widgetListFactory(Context c, Intent intent) {
        this.context = c;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String scheduleJson = get(context,schedule,null);//prefs.getString("schedule", null);
        if (scheduleJson != null && today>1 && today<7) {
            Gson g = new Gson();
            timeTable = g.fromJson(scheduleJson, new TypeToken<ArrayList<List<subject>>>() {
            }.getType());
            todaySchedule = timeTable.get(today - 2);
        }
        shouldShowAttendance = get(context,showAttendanceOnwidget,false);//context.getSharedPreferences(SETTING_PREFS_NAME, Context.MODE_PRIVATE).getBoolean(SHOW_ATT_KEY, false);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        try{
            return todaySchedule.size();
        }
        catch (Exception r){
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row;
        subject session = todaySchedule.get(position);
        if (session.type.equals("")) {
            row = new RemoteViews(context.getPackageName(), R.layout.rowview_widget_free_slot);
            row.setTextViewText(R.id.widget_free_slot_title, session.title);
        } else {
            if (shouldShowAttendance) {
                row = new RemoteViews(context.getPackageName(), R.layout.rowview_widget_with_attendance);

                row.setTextViewText(R.id.widget_attendance, session.attString);
            } else
                row = new RemoteViews(context.getPackageName(), R.layout.rowview_widget_class_slot);
            row.setTextViewText(R.id.widget_title, session.title);
            row.setTextViewText(R.id.widget_Time, session.startTime.toLowerCase());
            row.setTextViewText(R.id.widget_type, session.type);
            row.setTextViewText(R.id.widget_room, session.room);
        }
        row.setOnClickFillInIntent(R.id.widget_layout,new Intent());
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        //return (new RemoteViews(ctxt.getPackageName(),R.layout.activity_login));
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
