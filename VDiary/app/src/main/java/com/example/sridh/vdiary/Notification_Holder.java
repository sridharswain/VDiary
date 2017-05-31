package com.example.sridh.vdiary;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sparsha Saha on 12/11/2016.
 */

public class Notification_Holder {
    String title;
    String content;
    String ticker;
    int id;
    int dayofweek;
    int hourOfDay;
    int minute;
    Calendar cal;
    //long mills;
    public Notification_Holder(Calendar i,String title,String content,String ticker ){
        this.title=title;
        this.content=content;
        this.ticker=ticker;
        cal=i;
        dayofweek= cal.get(Calendar.DAY_OF_WEEK);
        hourOfDay=cal.get(Calendar.HOUR_OF_DAY);
        minute=cal.get(Calendar.MINUTE);
    }

    public static String convert_to_jason(List<Notification_Holder> lis)
    {
        String tojason;
        Gson json=new Gson();
        tojason=json.toJson(lis);
        return tojason;
    }

    public static List<Notification_Holder> convert_from_jason(String f)
    {
        Type type=new TypeToken<List<Notification_Holder>>(){}.getType();
        Gson json=new Gson();
        List<Notification_Holder> list;
        list=json.fromJson(f,type);
        return list;
    }

}
