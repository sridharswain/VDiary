package com.example.sridh.vdiary;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import static com.example.sridh.vdiary.settings.con;

public class settings extends AppCompatActivity {

    static Context con;
    Switch toggle_showNotification;
    Switch toggle_showAttendance;
    public static SharedPreferences settingPrefs;
    public static SharedPreferences.Editor settingPrefsEditor;
    public static String SETTING_PREFS_NAME= "settingPrefs";
    public static String SHOW_ATT_KEY ="showAttendance";
    public static String SHOW_NOTIF_KEY = "showNotification";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        con=this;
        vClass.setStatusBar(getWindow(),getApplicationContext(),R.color.colorPrimaryDark);
    }

    void initLayout(){
        setContentView(R.layout.activity_settings);
        settingPrefs = getSharedPreferences(SETTING_PREFS_NAME,MODE_PRIVATE);
        settingPrefsEditor= getSharedPreferences(SETTING_PREFS_NAME,MODE_PRIVATE).edit();
        toggle_showNotification= (Switch)findViewById(R.id.toggle_showNotification);
        toggle_showAttendance=(Switch)findViewById(R.id.toggle_showAttendance);
        setSettingConfig();
        toggle_showNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean toShowNotification) {
                if (toShowNotification){
                    new turnOnNotifications().execute();
                }
                else{
                    new turnOffNotifications().execute();
                }
                saveSettingConfig();
            }
        });
        toggle_showAttendance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean toShowAttendance) {
                updateWidget();
            }
        });
    }

    void saveSettingConfig(){
        settingPrefsEditor.putBoolean(SHOW_NOTIF_KEY,toggle_showNotification.isChecked());
        settingPrefsEditor.putBoolean(SHOW_ATT_KEY,toggle_showAttendance.isChecked());
        settingPrefsEditor.commit();
    }

    void setSettingConfig(){
        toggle_showNotification.setChecked(settingPrefs.getBoolean(SHOW_NOTIF_KEY,true));
        toggle_showAttendance.setChecked(settingPrefs.getBoolean(SHOW_ATT_KEY,false));
    }
    public static void ShutDownNotifications()
    {
        /*Intent intent=new Intent(context,NotifyService.class);
        PendingIntent pendingintent;
        AlarmManager alarm=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        for (int k = 0; k < vClass.timeTable.size(); k++) {
            List<subject> f = vClass.timeTable.get(k);
            for (int l = 0; l < f.size(); l++) {
                subject sub = f.get(l);
                if(!sub.type.equals("")) {
                    pendingintent = PendingIntent.getBroadcast(context, sub.notif_id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarm.cancel(pendingintent);

                }
            }
        }*/
        SharedPreferences shared=con.getSharedPreferences("cancelnotif",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=shared.edit();
        edit.putBoolean("cancel_reset",false);
        edit.apply();
    }

    public static void TurnOnNotification()
    {
        /*for (int k = 0; k < vClass.timeTable.size(); k++) {
            List<subject> f = vClass.timeTable.get(k);
            for (int l = 0; l < f.size(); l++) {
                subject sub = f.get(l);
                if (!sub.type.equals("")) {
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent in = new Intent(context, NotifyService.class);
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
                    c.set(Calendar.DAY_OF_WEEK, k + 2);
                    c.set(Calendar.AM_PM, ampm);
                    Notification_Holder noh = new Notification_Holder(c,sub.title + " " + sub.code, sub.room);
                    Gson jackson = new Gson();
                    String hh=jackson.toJson(noh);
                    Toast.makeText(context, hh, Toast.LENGTH_SHORT).show();
                    in.putExtra("one", hh);
                    in.putExtra("intent_chooser","one");
                    PendingIntent pintent = PendingIntent.getBroadcast(context,sub.notif_id, in, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - 5 * 60 * 1000, 24 * 7 * 60 * 60 * 1000, pintent);
                    o++;
                }
            }
        }*/
    }

    void updateWidget(){
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, widget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        saveSettingConfig();
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_today);
    }
}

class turnOnNotifications extends AsyncTask<Void,Void,Void>
{

    @Override
    protected Void doInBackground(Void... params) {
        settings.TurnOnNotification();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        SharedPreferences shared=con.getSharedPreferences("cancelnotif",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=shared.edit();
        edit.putBoolean("cancel_reset",true);
        edit.apply();
        super.onPostExecute(aVoid);
    }
}

class turnOffNotifications extends AsyncTask<Void,Void,Void>{

    @Override
    protected Void doInBackground(Void... params) {
        settings.ShutDownNotifications();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}