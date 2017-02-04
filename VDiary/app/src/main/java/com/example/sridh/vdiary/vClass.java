package com.example.sridh.vdiary;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sid on 8/28/2016.
 */
public class vClass {
    public static List<subject> subList = new ArrayList<subject>();
    public static List<List<subject>> timeTable = new ArrayList<>();
    public static String semStart;
    public static String cat1;
    public static String cat2;
    public static String fat;
    public static int width;
    public static int  height;
    public static Typeface fredoka,nunito_Extrabold,nunito_bold,nunito_reg;
    public static List<Notification_Holder> notes=new ArrayList<>();
    public static void setStatusBar(Window window, Context context,int color) {
        if(Build.VERSION.SDK_INT>=21){
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(context,color));
        }
    }  //CHANGE THE COLOR OF THE STATUS BAR
    public static List<teacher> teachers= new ArrayList<>();
    public static  List<Cabin_Details> cablist=new ArrayList<>();
    public static String FIREBASE_URL= "https://vdiary-a25b2.firebaseio.com/";
    //PARAMETERS FOR CHANGING THE LINKS
    public static String SEM = "WS";
    public static String VERSION= "1.8";

    //HOLIDAYS
    public static List<holiday> holidays= new ArrayList<>();

    public static void getFonts(Context context){
        fredoka=Typeface.createFromAsset(context.getAssets(),"fonts/FredokaOne-Regular.ttf");
        nunito_bold=Typeface.createFromAsset(context.getAssets(),"fonts/Nunito-Bold.ttf");
        nunito_Extrabold=Typeface.createFromAsset(context.getAssets(),"fonts/Nunito-ExtraBold.ttf");
        nunito_reg = Typeface.createFromAsset(context.getAssets(), "fonts/Nunito-Regular.ttf");
    }

}
class Note
{
    String title;
    String note;
    String time;
    String date;
}
class Cabin_Details
{
    public String name;
    public String cabin;
    public String others;

    public void setName(String name) {
        this.name = name;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    public void setOthers(String others) {
        this.others = others;
    }
}
class subject {
    public String code;
    public String title;
    public String teacher;
    public String attString;
    public String room;
    public int ctd;
    public String startTime;
    public String endTime;
    public String type;
    public int notif_id;
}


class teacher{
    String name;
    String cabin;
    public String getName(){
        return this.name;
    }
    public String getCabin(){
        return this.cabin;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setCabin(String cabin){
        this.cabin=cabin;
    }
}
class holiday{
    Calendar date;
    String ocassion;
    boolean done_notdone;
    public holiday(Calendar calendar,String string){
        date=calendar;
        ocassion=string;
        done_notdone=false;
    }

    class settings_list
    {
        String title;
        boolean checked;
    }
}

