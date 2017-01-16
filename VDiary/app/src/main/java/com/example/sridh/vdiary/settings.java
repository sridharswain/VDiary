package com.example.sridh.vdiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

import static com.example.sridh.vdiary.settings.con;

public class settings extends AppCompatActivity {
ListView listView;
    static int o=0;
    SharedPreferences shared;
    static Context con;
    static SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        con=this;
        shared=getSharedPreferences("notiftimetable",Context.MODE_PRIVATE);
        editor=shared.edit();
        listView=(ListView)findViewById(R.id.setting_view);
        vClass.setStatusBar(getWindow(),getApplicationContext());
        SettingsAdapter setadap=new SettingsAdapter(this,vClass.setting_list);
        listView.setAdapter(setadap);




    }

    public static void ShutDownNotifications(List<settings_list> j)
    {

        Intent intent=new Intent(con,NotifyService.class);
        PendingIntent pendingintent;
        AlarmManager alarm=(AlarmManager)con.getSystemService(Context.ALARM_SERVICE);
        for (int k = 0; k < vClass.timeTable.size(); k++) {
            List<subject> f = vClass.timeTable.get(k);
            for (int l = 0; l < f.size(); l++) {
                subject sub = f.get(l);
                if(sub.type.equals("")!=true) {
                    pendingintent = PendingIntent.getBroadcast(con, sub.notif_id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarm.cancel(pendingintent);

                }
            }
        }
        editor.putString("settinglist",new Gson().toJson(j));
        editor.apply();


    }

    public static void TurnOnNotification(List<settings_list> p)
    {
        /*for (int k = 0; k < vClass.timeTable.size(); k++) {
            List<subject> f = vClass.timeTable.get(k);
            for (int l = 0; l < f.size(); l++) {
                subject sub = f.get(l);
                if (!sub.type.equals("")) {
                    AlarmManager alarmManager = (AlarmManager) con.getSystemService(Context.ALARM_SERVICE);
                    Intent in = new Intent(con, NotifyService.class);
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
                    Toast.makeText(con, hh, Toast.LENGTH_SHORT).show();
                    in.putExtra("one", hh);
                    in.putExtra("intent_chooser","one");
                    PendingIntent pintent = PendingIntent.getBroadcast(con,sub.notif_id, in, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - 5 * 60 * 1000, 24 * 7 * 60 * 60 * 1000, pintent);
                    o++;
                }
            }
        }*/
        editor.putString("settinglist",new Gson().toJson(p));
        editor.apply();

    }
}

class SettingsAdapter extends BaseAdapter
{
static List<settings_list> list;
    Context context;

    public static View rowview;
    public static LayoutInflater inflater=null;
    //parameterized constructor
    public SettingsAdapter(Context c, List<settings_list> j)
    {
        list=j;
        context=c;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class Holder
    {
        TextView settings_name;
        Switch settings_on_off;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        rowview=inflater.inflate(R.layout.rowview_settings,null);
        final Holder holder=new Holder();
        holder.settings_name=(TextView)rowview.findViewById(R.id.setting_name);
        holder.settings_on_off=(Switch)rowview.findViewById(R.id.settings_on_off);
        holder.settings_name.setText(list.get(position).title);
        if(list.get(position).checked==true)
            holder.settings_on_off.setChecked(true);

        holder.settings_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(position==1 && holder.settings_on_off.isChecked()==false)
                {
                    list.get(position).checked=false;
                    newthread2 nw2=new newthread2();
                    nw2.execute();
                }
                else if(position==1 && holder.settings_on_off.isChecked()==true)
                {
                    list.get(position).checked=true;
                    newthread n=new newthread();
                    n.execute();
                }
            }
        });


        return rowview;
    }
}

class newthread extends AsyncTask<Void,Void,Void>
{

    @Override
    protected Void doInBackground(Void... params) {
       settings.TurnOnNotification(SettingsAdapter.list);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(con, settings.o+"", Toast.LENGTH_SHORT).show();
        super.onPostExecute(aVoid);
    }
}

class newthread2 extends AsyncTask<Void,Void,Void>{

    @Override
    protected Void doInBackground(Void... params) {
        settings.ShutDownNotifications(SettingsAdapter.list);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(con, settings.o+"", Toast.LENGTH_SHORT).show();
        super.onPostExecute(aVoid);
    }
}


class settings_list
{
    String title;
    boolean checked;
}