package com.example.sridh.vdiary;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sid on 8/28/2016.
 */
public class vClass {
    public static List<subject> subList = new ArrayList<subject>();
    public static  List<List<subject>> timeTable = new ArrayList<>();
    public static String semStart;
    public static String cat1;
    public static String cat2;
    public static String fat;
    public static Map<String,Boolean> lis=new HashMap<>();
    public static List<Note> notes=new ArrayList<>();
    public static void setStatBar(Window window,Context context){
        if(Build.VERSION.SDK_INT>=21){
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(context,R.color.taskbar_orange));
        }
    }
}
class Note
{
    String title;
    String note;
    String time;
    String date;
}

