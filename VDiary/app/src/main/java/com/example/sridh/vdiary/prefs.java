package com.example.sridh.vdiary;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Set;

/**
 * Created by sid on 2/13/17.
 */

public class prefs {
    static String allSub="allSub";
    static String schedule="schedule";
    static String teachers="teachers";
    static String holidays="holidays";
    static String customTeachers="customTeachers";
    static String prefName="zchedulePrefs";
    static String isLoggedIn="isLoggedIn";
    static String regNo ="regNo";
    static String password="password";
    static String lastRefreshed="lastRefreshed";
    static String todolist="todolist";
    static String notificationIdentifier="notificationIdentifier";
    static String toUpdate="toUpdate";
    static String isFirst="isFirst";
    static String showNotification="showNotification";
    static String isWidgetEnabled ="isEnabled";
    static String showAttendanceOnwidget ="showAttendance";
    static String dataVersion="dataVersion";
    static String tipid="tipId";

    static SharedPreferences getPrefsInstance(Context context){
        return context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
    }
    static SharedPreferences.Editor getPrefEditor(Context context){
        return getPrefsInstance(context).edit();
    }
    static void put(Context context, String name, String value){
        SharedPreferences.Editor editor= getPrefEditor(context);
        editor.putString(name,value);
        editor.apply();
    }
    static void put(Context context,String name, boolean value){
        SharedPreferences.Editor editor= getPrefEditor(context);
        editor.putBoolean(name,value);
        editor.apply();
    }
    static void put(Context context,String name,int value){
        SharedPreferences.Editor editor= getPrefEditor(context);
        editor.putInt(name,value);
        editor.apply();
    }
    static String get(Context context,String name,String defaultValue){
        return getPrefsInstance(context).getString(name,defaultValue);
    }
    static boolean get(Context context,String name,boolean defaultValue){
        return getPrefsInstance(context).getBoolean(name,defaultValue);
    }
    static int get(Context context,String name,int defaultValue){
        return getPrefsInstance(context).getInt(name,defaultValue);
    }

    static subject getSubject(Context context,String id){
        String subJson= get(context,id,null);
        if(subJson==null) return null;
        else return (new Gson()).fromJson(subJson,new TypeToken<subject>(){}.getType());
    }
}
