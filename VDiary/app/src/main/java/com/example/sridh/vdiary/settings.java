package com.example.sridh.vdiary;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;


import static com.example.sridh.vdiary.prefs.showAttendanceOnwidget;
import static com.example.sridh.vdiary.prefs.showNotification;
import static com.example.sridh.vdiary.prefs.put;
import static com.example.sridh.vdiary.prefs.get;
public class settings extends AppCompatActivity {

    Context context;
    Switch toggle_showNotification;
    Switch toggle_showAttendance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        initLayout();
        vClass.setStatusBar(getWindow(),context,R.color.colorPrimaryDark);
    }

    void initLayout(){
        setContentView(R.layout.activity_settings);
        toggle_showNotification= (Switch)findViewById(R.id.toggle_showNotification);
        toggle_showAttendance=(Switch)findViewById(R.id.toggle_showAttendance);
        setSettingConfig();
        toggle_showNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean toShowNotification) {
                put(context,showNotification,toShowNotification);
            }
        });
        toggle_showAttendance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean toShowAttendance) {
                put(context,showAttendanceOnwidget,toShowAttendance);
                updateWidget();
            }
        });
    }

    void setSettingConfig(){
        toggle_showNotification.setChecked(get(context,showNotification,true));//settingPrefs.getBoolean(SHOW_NOTIF_KEY,true));
        toggle_showAttendance.setChecked(get(context,showAttendanceOnwidget,false));//settingPrefs.getBoolean(SHOW_ATT_KEY,false));
    }
   void updateWidget(){
        (new widgetServiceReceiver()).onReceive(context,(new Intent(context,widgetServiceReceiver.class)));
    }

}