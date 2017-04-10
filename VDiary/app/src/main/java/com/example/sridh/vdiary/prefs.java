package com.example.sridh.vdiary;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sid on 2/13/17.
 */

public class prefs {
    public static String allSub="allSub";
    public static String schedule="schedule";
    public static String teachers="teachers";
    public static String holidays="holidays";
    public static String customTeachers="customTeachers";
    public static String prefName="zchedulePrefs";
    public static String isLoggedIn="isLoggedIn";
    public static String regNo ="regNo";
    public static String password="password";
    public static String lastRefreshed="lastRefreshed";
    public static String todolist="todolist";
    public static String notificationIdentifier="notificationIdentifier";
    public static String toUpdate="toUpdate";
    public static String isFirst="isFirst";
    public static String showNotification="showNotification";
    public static String isWidgetEnabled ="isEnabled";
    public static String showAttendanceOnwidget ="showAttendance";
    public static String dataVersion="dataVersion";
    public static String tipid="tipId";
    public static String scheduleNotificationCount="notificationCount";
    public static String avgAttendance="avgAttendance";
    public static String campusUrl = "campusUrl";

    //SHARED PREFERENCES INSTANCE OF THE APP
    public static SharedPreferences getPrefsInstance(Context context){
        return context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
    }

    //SHARED PREFERENCES EDITOR INSTANCE OF THE APP
    public static SharedPreferences.Editor getPrefEditor(Context context){
        return getPrefsInstance(context).edit();
    }

    //STRING PREFERENCES
    public static void put(Context context, String name, String value){
        SharedPreferences.Editor editor= getPrefEditor(context);
        editor.putString(name,value);
        editor.apply();
    }
    public static String get(Context context,String name,String defaultValue){
        return getPrefsInstance(context).getString(name,defaultValue);
    }

    //BOOLEAN PREFERENCES
    public static void put(Context context,String name, boolean value){
        SharedPreferences.Editor editor= getPrefEditor(context);
        editor.putBoolean(name,value);
        editor.apply();
    }
    public static boolean get(Context context,String name,boolean defaultValue){
        return getPrefsInstance(context).getBoolean(name,defaultValue);
    }

    //INTEGER PREFERENCES
    public static void put(Context context,String name,int value){
        SharedPreferences.Editor editor= getPrefEditor(context);
        editor.putInt(name,value);
        editor.apply();
    }
    public static int get(Context context,String name,int defaultValue){
        return getPrefsInstance(context).getInt(name,defaultValue);
    }
}
