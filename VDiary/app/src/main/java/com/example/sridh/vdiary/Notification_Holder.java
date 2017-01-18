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
    Calendar cal;
    String title;
    String content;
    String ticker;
    boolean scheduled;
    int id;
    //long mills;

    public Notification_Holder(Calendar i,String title,String content,String ticker)
    {
        cal=i;
        this.title=title;
        this.content=content;
        this.ticker=ticker;
        scheduled=false;
        //mills=cal.getTimeInMillis();
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
        List<Notification_Holder> list=new ArrayList<>();
        list=json.fromJson(f,type);
        return list;
    }

}
