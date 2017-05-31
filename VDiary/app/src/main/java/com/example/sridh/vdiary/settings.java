package com.example.sridh.vdiary;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;


import static com.example.sridh.vdiary.prefs.putTheme;
import static com.example.sridh.vdiary.prefs.showAttendanceOnwidget;
import static com.example.sridh.vdiary.prefs.showNotification;
import static com.example.sridh.vdiary.prefs.put;
import static com.example.sridh.vdiary.prefs.get;
import static com.example.sridh.vdiary.vClass.CurrentTheme;
import static com.example.sridh.vdiary.vClass.getCurrentTheme;

public class settings extends AppCompatActivity {

    Context context;
    Switch toggle_showNotification, toggle_showAttendance;
    RelativeLayout layout;
    ImageButton selectedCircle;
    int[] circleIDs= new int[]{R.id.theme_circle_red,R.id.theme_circle_blue,R.id.theme_circle_teal};
    int[] circleNotsId =new int[]{R.drawable.circle_red_nots,R.drawable.circle_blue_nots,R.drawable.circle_teal_nots};
    int[] circleSId= new int[]{R.drawable.circle_red_s,R.drawable.circle_blue_s,R.drawable.circle_teal_s};
    int CircleIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();

        CurrentTheme = prefs.getTheme(context);  //TODO TAKE THIS STATEMENT TO WORKSPACE

        CircleIndex = getIdOf(CurrentTheme);
        setTheme(getCurrentTheme().theme);
        setContentView(R.layout.activity_settings);
        selectedCircle= (ImageButton)findViewById(circleIDs[CircleIndex]);
        selectedCircle.setImageDrawable(getResources().getDrawable(circleSId[CircleIndex]));
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_settings);
        toolbar.setBackgroundColor(getResources().getColor(getCurrentTheme().colorPrimaryDark));
        /*initLayout(); //TODO UNCOMMENT THIS REGION
        vClass.setStatusBar(getWindow(),context,R.color.colorPrimaryDark);*/
    }

    void initLayout(){

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
    public void onRedClick(View view) {
        handleThemeCircleSelection(Theme.red);
    }
    public void onBlueClick(View view){
        handleThemeCircleSelection(Theme.blue);
    }
    public void onTealClick(View view){
        handleThemeCircleSelection(Theme.teal);
    }

    void handleThemeCircleSelection(Theme clickedTheme){
        if(CurrentTheme!=clickedTheme){   //CHECK IF THE CIRCLE SELECTED NOT THE SAME

            selectedCircle.setImageDrawable(getResources().getDrawable(circleNotsId[CircleIndex]));  //CHANGE THE IMAGE OF THE OLDER CIRCLE TO NOT SELECTED
            CircleIndex = getIdOf(clickedTheme);  //GET THE INDEX OF NEW CIRCLE
            selectedCircle = (ImageButton)findViewById(circleIDs[CircleIndex]);  //GET THE View OF THE IMAGEBUTTON
            selectedCircle.setImageDrawable(getResources().getDrawable(circleSId[CircleIndex])); //CHANGE THE BACKGROUND OF THE IMAGEBUTTON TO SELECTED

            CurrentTheme=clickedTheme;
            putTheme(context,clickedTheme);
            Toast.makeText(getApplicationContext(),"Restart App to apply changes",Toast.LENGTH_LONG).show();
        }
    }

    int getIdOf(Theme theme){
        switch (theme){
            case red:
                return 0;
            case blue:
                return 1;
            case teal:
                return 2;
        }
        return 0;
    }
}