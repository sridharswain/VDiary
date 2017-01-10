package com.example.sridh.vdiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class scrapper extends AppCompatActivity {

    //for Notifications
    public static String title;
    public static String name_and_teachersname;
    public static Calendar timings;
    public static Context context;
    public static PendingIntent pintent;
    public static SharedPreferences shared;
    public static SharedPreferences.Editor editor;
    static int n_id=0;
    //END

    EditText regBox,passBox,captchaBox;
    WebView web,att;
    ImageView captcha,login;
    CheckBox cb;
    TextView status;
    RelativeLayout loginView,loadView;
    FloatingActionButton reload;
    boolean gotAttendance=false;
    boolean gotSchedule=false;
    boolean attendanceStatus=true;
    Gson jsonBuilder = new Gson();
    static boolean tryRefresh=false;
    Firebase database;
    List<String> attList = new ArrayList<>();
    List<String> ctdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        shared=context.getSharedPreferences("notiftimetable",Context.MODE_PRIVATE);
        editor=shared.edit();
        n_id=shared.getInt("id_time",0);
        //FIREBASE INITIATION
        Firebase.setAndroidContext(this);
        database= new Firebase(vClass.FIREBASE_URL);

        setContentView(R.layout.splash_screen);
        vClass.setStatusBar(getWindow(),getApplicationContext());
        start();
    }

    void start() {
        if(!tryRefresh && readFromPrefs()){
            startActivity(new Intent(this, workSpace.class));
            finish();
        }
        else{
            initWebViews();
            setUp();
            vClass.setStatusBar(getWindow(),getApplicationContext());
            new compileInf().execute();
        }
    } //STARTS THE PROCESSING

    private class loginClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view,url);
            String webTitle =web.getTitle();
            if(webTitle.equals("") || webTitle.equals("Webpage not available")){
                if(tryRefresh){
                    Toast.makeText(getApplicationContext(),"Connection Failed!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(scrapper.this,workSpace.class));
                    finish();
                    return;
                }
                else{
                    status.setText("Connection Falied!");
                    load(true);
                    reload.setVisibility(View.VISIBLE);
                    return;
                }
            }
            else if(web.getUrl().equals("https://academicscc.vit.ac.in/student/stud_login.asp")) {
                web.evaluateJavascript(getcmd("return document.getElementsByName(\"message\")[0].value"), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String message) {
                        if (!message.equals("\"\"") & !message.equals("null")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                String getCaptcha = getcmd("var img= document.getElementById('imgCaptcha'); var canvas = document.createElement('canvas'); canvas.width = img.naturalWidth; canvas.height = img.naturalHeight; canvas.getContext('2d').drawImage(img, 0, 0); return canvas.toDataURL('image/png').replace(/^data:image\\/(png|jpg);base64,/, '');");
                web.evaluateJavascript(getCaptcha, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String captchaString) {
                        setCaptcha(captchaString);
                    }
                });
            }
            else{
                getTeacherCabins();
                status.setText("Fetching Courses...");
                web.setWebViewClient(new scheduleClient());
                web.loadUrl("https://academicscc.vit.ac.in/student/course_regular.asp?sem="+vClass.SEM);
                att.setWebViewClient(new attendanceClient());
                att.loadUrl("https://academicscc.vit.ac.in/student/attn_report.asp?sem="+vClass.SEM);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view,handler,error);
            handler.proceed();
        }

    } // WEBVIEWCLIENT TO CONTROL LOGIN PAGE

    private class scheduleClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            vClass.subList.clear();
            vClass.timeTable.clear();
            getFormTable1();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    } // WEBVIEWCLIENT TO GET THE SCHEDULE

    private class attendanceClient extends WebViewClient{
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            getAttendance();
        }
    } //WEBVIEWCLIENT TO GET ATTENDANCE

    private String getcmd(String js){

        return "(function(){"+js+"})()";

    } //RETURN THE FUNCTION FORMAT OF THE GIVEN COMMAND

    private void setCaptcha(String imgString){
        byte[] decodedString = Base64.decode(imgString,0);
        Bitmap capImg= BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        captcha.setImageBitmap(capImg);
        load(false);
    } //CONVERTS THE BASE-64 STRING TO BITMAP IMAGE AND SETS TO CAPTCHA IMAGEVIEW

    private void setUp(){
        setContentView(R.layout.activity_login);
        status=(TextView)findViewById(R.id.status);
        loginView=(RelativeLayout)findViewById(R.id.loginView);
        loadView=(RelativeLayout)findViewById(R.id.loadView);
        captcha=(ImageView)findViewById(R.id.captcha);
        regBox=(EditText)findViewById(R.id.regBox);
        passBox=(EditText)findViewById(R.id.passbox);
        captchaBox=(EditText)findViewById(R.id.captchaBox);
        cb=(CheckBox)findViewById(R.id.saveCreds);
        login=(ImageButton)findViewById(R.id.login);
        reload=(FloatingActionButton)findViewById(R.id.refresh_FloatButton);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                web.loadUrl("https://academicscc.vit.ac.in/student/stud_login.asp");
                status.setText("Building Captcha...");
                reload.setVisibility(View.INVISIBLE);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeCreds();
                load(true);
                status.setText("Logging In...");
                if(cb.isChecked()) saveCreds();
                else delCreds();
            }
        });
        load(true);
        readCreds();
        status.setText("Building Captcha...");
    } //SETS RELATIVE LAYOUT OF THE MAIN PAGE

    void initWebViews(){
        web= new WebView(this);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebViewClient(new loginClient());
        web.loadUrl("https://academicscc.vit.ac.in/student/stud_login.asp");
        att= new WebView(this);
        att.getSettings().setDomStorageEnabled(true);
        att.getSettings().setJavaScriptEnabled(true);
    } //INITIALIZE THE WEBVIEWS AND LAST LOADING THE LOGIN PAGE

    private void placeCreds(){
                String input="document.getElementsByName(\"regno\")[0].value=\""+regBox.getText().toString()+"\"; document.getElementsByName(\"passwd\")[0].value=\""+passBox.getText()+"\"; document.getElementsByName(\"vrfcd\")[0].value=\""+captchaBox.getText()+"\"; document.forms[0].submit();";
                web.evaluateJavascript(getcmd(input), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //CREDENTIALS ARE PLACED ON THEIR RESPECTIVE PLACES AND THE FORM IS SUBMITTED
                    }
                });


    } //SETS THE CREDENTIAL TO THE FORM AND SUBMITS IT

    private String trim(String str){
        str= str.substring(1);
        return str.substring(0,str.indexOf("\""));
    } //TRIMS THE GIVEN RESULT FROM JAVASCRIPT TO REMOVE QUOTES

    private void getFormTable1(){
        web.evaluateJavascript(getcmd("var rows=document.getElementsByTagName('table')[1].rows;var c;for(c=0;c<rows.length;c++){if(rows[c].cells.length==15){rows[c].deleteCell(0)}}"), new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                web.evaluateJavascript("var rows=document.getElementsByTagName('table')[1].rows;var c;for(c=0;c<rows.length;c++){if(rows[c].cells.length==14){rows[c].deleteCell(0)}}", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows.length.toString()"), new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                int rows=Integer.parseInt(trim(value));
                                for(int row=1;row<rows-2;row++){
                                    final int rowa=row;
                                    web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + row + "].cells[8].innerText.toString()"), new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                            final String room=trim(value);
                                            if(!room.equals("NIL")){
                                                final subject sub= new subject();
                                                sub.room=room;
                                                //Toast.makeText(getApplicationContext(),room,Toast.LENGTH_LONG).show();
                                                //CODE
                                                web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[1].innerText.toString()"), new ValueCallback<String>() {
                                                    @Override
                                                    public void onReceiveValue(String code) {
                                                        sub.code=trim(code);
                                                    }
                                                });
                                                //NAME
                                                web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[2].innerText.toString()"), new ValueCallback<String>() {
                                                    @Override
                                                    public void onReceiveValue(String name) {
                                                        sub.title=trim(name);
                                                    }
                                                });
                                                //TEACHER
                                                web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[9].innerText.toString()"), new ValueCallback<String>() {
                                                    @Override
                                                    public void onReceiveValue(String teacher) {
                                                        String rawTeacher= trim(teacher).split("-")[0];
                                                        sub.teacher=rawTeacher.substring(0,rawTeacher.length()-1);
                                                    }
                                                });
                                                //TYPE
                                                web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[3].innerText.toString()"), new ValueCallback<String>() {
                                                    @Override
                                                    public void onReceiveValue(String rawtype) {
                                                        String type=trim(rawtype);
                                                        switch (type)
                                                        {
                                                            case "Embedded Theory":
                                                                sub.type = "ETH";
                                                                break;
                                                            case "Theory Only":
                                                                sub.type = "TH";
                                                                break;
                                                            case "Lab Only":
                                                                sub.type = "LO";
                                                                break;
                                                            case "Embedded Lab":
                                                                sub.type = "ELA";
                                                                break;
                                                            case "Soft Skill":
                                                                sub.type = "SS";
                                                                break;
                                                        }
                                                    }
                                                });
                                                vClass.subList.add(sub);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
        getFromTable2();
    } //GET DATA FROM ALL COURSES

    private void getFromTable2(){
        web.evaluateJavascript(getcmd("document.getElementsByTagName('table')[2].rows[0].deleteCell(7);"), new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //LUNCH DELETED
                for(int rowa=2;rowa<=6;rowa++){
                    final int row=rowa;
                    final List<subject> today= new ArrayList<subject>();
                    web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[" + row + "].cells.length.toString()"), new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String rawCols) {
                            final int cols=Integer.parseInt(trim(rawCols));
                            final AtomicReference extraTime = new AtomicReference(0);
                            for(int col=1;col<cols;col++){
                                final int cell=col;
                                web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[" + row + "].cells[" + cell + "].colSpan.toString()"), new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        int rawcolSpan=Integer.parseInt(trim(value));
                                        if(rawcolSpan>1) {
                                            extraTime.set(rawcolSpan - 1);
                                        }
                                        web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[" + row + "].cells['" + cell + "'].bgColor"), new ValueCallback<String>() {
                                            @Override
                                            public void onReceiveValue(String color) {
                                                final String cellColor = trim(color);
                                                web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[" + row + "].cells[" + cell + "].innerText.toString()"), new ValueCallback<String>() {
                                                    @Override
                                                    public void onReceiveValue(String value) {
                                                        String text= trim(value);
                                                        final subject sub = new subject();
                                                        if(cellColor.equals("#CCFF33")) {
                                                            sub.code = text.substring(0, 7); //CODE
                                                            String rawType = text.split("-")[1];
                                                            String type = rawType.substring(1, rawType.length() - 1); //TYPE
                                                            sub.type = type;
                                                            //TIME
                                                            if (type.equals("ETH") || type.equals("SS") || type.equals("TH")) {
                                                                web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[0].cells[" + (cell + Integer.parseInt(String.valueOf(extraTime.get()))) + "].innerText.toString()"), new ValueCallback<String>() {
                                                                    @Override
                                                                    public void onReceiveValue(String value) {
                                                                        String time = trim(value);
                                                                        sub.startTime=time.substring(0, 8);
                                                                        sub.endTime=time.substring(14, time.length());
                                                                        today.add(sub);
                                                                    }
                                                                });
                                                            }
                                                            else {
                                                                web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[1].cells[" + (cell + Integer.parseInt(String.valueOf(extraTime.get()))) + "].innerText.toString()"), new ValueCallback<String>() {
                                                                    @Override
                                                                    public void onReceiveValue(String value) {
                                                                        String rawstime = trim(value);
                                                                        String sTime = rawstime.substring(0, 8);
                                                                        final AtomicReference<String> time = new AtomicReference<String>(sTime);
                                                                        web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[1].cells[" + (cell + 1 + Integer.parseInt(String.valueOf(extraTime.get()))) + "].innerText.toString()"), new ValueCallback<String>() {
                                                                            @Override
                                                                            public void onReceiveValue(String value) {
                                                                                String rawetime = trim(value);
                                                                                String etime = rawetime.substring(14, rawetime.length());
                                                                                sub.startTime=time.get();
                                                                                sub.endTime=etime;
                                                                                today.add(sub);
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        }
                                                        else{
                                                            sub.code="";
                                                            sub.title=text;
                                                            sub.type="";
                                                            sub.room="";
                                                            web.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[1].cells[" + (cell + Integer.parseInt(String.valueOf(extraTime.get()))) +"].innerText.toString()"), new ValueCallback<String>() {
                                                                @Override
                                                                public void onReceiveValue(String value) {
                                                                    String time=trim(value);
                                                                    sub.startTime=time.substring(0, 8);
                                                                    sub.endTime=time.substring(14, time.length());
                                                                    today.add(sub);
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                                if(row==6 && cell==cols-1){
                                                    gotSchedule=true;
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                    vClass.timeTable.add(today);
                }
            }
        });
    } // GET DATA FROM TIME TABLE

    private void getAttendance() {
        if (att.getUrl().equals("https://academicscc.vit.ac.in/student/attn_report.asp?sem="+vClass.SEM)) {
            att.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[2].cells[2].innerText"), new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    vClass.semStart = trim(value);
                    att.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[2].cells[3].innerText"), new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            vClass.cat1 = trim(value);
                            att.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[3].cells[3].innerText"), new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    vClass.cat2 = trim(value);
                                    att.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[4].cells[3].innerText"), new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                            vClass.fat = trim(value);
                                            att.loadUrl("https://academicscc.vit.ac.in/student/attn_report.asp?sem=FS" + "&fmdt=" + vClass.semStart + "&todt=" + vClass.fat);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
        else if(att.getUrl().equals("https://academicscc.vit.ac.in/student/attn_report.asp?sem="+vClass.SEM + "&fmdt=" + vClass.semStart + "&todt=" + vClass.fat)){
            att.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[4].rows.length"), new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    final int rows = Integer.parseInt(value);
                    for (int i = 1; i < rows; i++) {
                        final int j = i;
                        att.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[4].rows[" + j + "].cells[8].innerText"), new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                attList.add(trim(value));
                                att.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[4].rows[" + j + "].cells[7].innerText"), new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        ctdList.add(trim(value));
                                        if (j == rows - 1) {
                                            status.setText("Merging Attendance...");
                                            gotAttendance = true;
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
        else{
            attendanceStatus=false;
        }
    }//GET DATAFROM THE ATTENDANCE PAGE

    class compileInf extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            while(!gotAttendance && !gotSchedule){}
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //SCRAPING COMPLETE
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            status.setText("Summarizing Views...");
            if(attendanceStatus) {
                for (int i = 0; i < vClass.subList.size(); i++) {
                    vClass.subList.get(i).ctd = Integer.parseInt(ctdList.get(i));
                    vClass.subList.get(i).attString = attList.get(i) + "%";
                }
            }
            else{
                for (int i = 0; i < vClass.subList.size(); i++) {
                    vClass.subList.get(i).ctd = 0;
                    vClass.subList.get(i).attString =String.valueOf(0)+"%";
                }
            }
            for(List<subject> i:vClass.timeTable){
                for(int count=0;count<i.size();count++){
                    subject sub=getSubject(i.get(count).code,i.get(count).type);
                    if(sub!=null){
                        i.get(count).attString=sub.attString;
                        i.get(count).teacher=sub.teacher;
                        i.get(count).title=sub.title;
                        i.get(count).room=sub.room;
                        if(sub.type.equals("ELA") || sub.type.equals("LO")){
                            i.remove(count+1);
                            placeCorrectly(i.get(count),i);
                        }
                        //Log.d("Subject",i.get(count).code+" "+" "+ i.get(count).title+" "+i.get(count).teacher+" "+i.get(count).attString+" "+i.get(count).time);
                    }
                }
            }

            //Sparsha code starts from here to schedule notifications for the timetable class
            for(int k=0;k<vClass.timeTable.size();k++)
            {
                List<subject> f=vClass.timeTable.get(k);
                for(int l=0;l<f.size();l++)
                {
                    subject sub=f.get(l);
                    if(!sub.type.equals("")) {
                        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                        Intent in=new Intent(scrapper.this,NotifyService.class);
                        scrapper.editor.putInt("id_time",n_id);
                        Calendar c=Calendar.getInstance();
                        int st_hr,st_min,ampm;
                        st_hr=Integer.parseInt(sub.startTime.substring(0,2));
                        st_min=Integer.parseInt(sub.startTime.substring(3,5));
                        String kk=sub.startTime.substring(6);

                        if(kk.equals("AM"))
                            ampm=0;
                        else
                            ampm=1;

                        c.set(Calendar.HOUR,st_hr);
                        c.set(Calendar.MINUTE,st_min);
                        c.set(Calendar.SECOND,0);
                        c.set(Calendar.DAY_OF_WEEK,k+2);
                        c.set(Calendar.AM_PM,ampm);
                        Notification_Holder nh=new Notification_Holder(c,sub.title+" "+sub.code,sub.room);
                        Gson j=new Gson();
                        in.putExtra("one",j.toJson(nh));
                        PendingIntent pintent=PendingIntent.getBroadcast(context,scrapper.n_id,in,0);
                        vClass.timeTable.get(k).get(l).notif_id=n_id;
                        n_id++;
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis()-5*60*1000,24*7*60*60*1000,pintent);



                    }

                }
            }
            writeToPrefs();
            startActivity(new Intent(scrapper.this,workSpace.class));
            finish();
        }
    } //REARRANGE THE INFORMATION SCRAPPED FORM THE WEBPAGE

    private subject getSubject(String code,String type){
        for(subject i:vClass.subList){
            if(i.code.equals(code) && i.type.equals(type)){
                return i;
            }
        }
        return null;
    } //SEARCH SUBJECT IN SUBJECT LIST

    void writeToPrefs(){

        SharedPreferences.Editor editor = getSharedPreferences("academicPrefs",MODE_PRIVATE).edit();
        editor.putString("allSub",jsonBuilder.toJson(vClass.subList));
        editor.putString("schedule",jsonBuilder.toJson(vClass.timeTable));
        editor.commit();
    } //WRITE ACADEMIC CONTENT TO SHARED PREFERENCES

    boolean readFromPrefs(){
        SharedPreferences academicPrefs= getSharedPreferences("academicPrefs",MODE_PRIVATE);
        String allSubJson=academicPrefs.getString("allSub",null);
        String scheduleJson =academicPrefs.getString("schedule",null);
        String taskJson= academicPrefs.getString("tasks",null);
        SharedPreferences teacherPrefs=getSharedPreferences("teacherPrefs",MODE_PRIVATE);
        String teachers =teacherPrefs.getString("teachers",null);
        String customTeachers=teacherPrefs.getString("customTeachers",null);
        if(customTeachers!=null){
            vClass.cablist=jsonBuilder.fromJson(customTeachers,new TypeToken<List<Cabin_Details>>(){}.getType());
        }
        if(teachers!=null){
            vClass.teachers=jsonBuilder.fromJson(teachers,new TypeToken<List<teacher>>(){}.getType());
            new tryUpdateDatabase().execute();
        }
        if(taskJson!=null){
            vClass.courseTasks=jsonBuilder.fromJson(taskJson,new TypeToken<Map<String,List<task>>>(){}.getType());
        }
        if(allSubJson!=null && scheduleJson!=null){
            vClass.subList=jsonBuilder.fromJson(allSubJson,new TypeToken<ArrayList<subject>>(){}.getType());
            vClass.timeTable=jsonBuilder.fromJson(scheduleJson, new TypeToken<ArrayList<ArrayList<subject>>>(){}.getType());
            return true;
        }
        return false;
    } //READ ACADEMIC CONTENT FROM SHARED PREFERENCES

    void saveCreds(){
        SharedPreferences.Editor credPrefs= getSharedPreferences("credPrefs",MODE_PRIVATE).edit();
        credPrefs.putString("regNo",regBox.getText().toString());
        credPrefs.putString("password", passBox.getText().toString());
        credPrefs.commit();
    }  //SAVE THE CREDENTIALS IF THE USER WANTS TO

    void readCreds(){
        SharedPreferences credPrefs=getSharedPreferences("credPrefs",MODE_PRIVATE);
        String reg=credPrefs.getString("regNo",null);
        String pass= credPrefs.getString("password",null);
        if(reg!=null && pass!=null){
            regBox.setText(reg);
            passBox.setText(pass);
        }
    }  //TRY TO READ THE SAVED CREDENTIAL FROM SHARED PREFERENCES

    void delCreds(){
        SharedPreferences.Editor credPrefs= getSharedPreferences("credPrefs",MODE_PRIVATE).edit();
        credPrefs.putString("regNo",null);
        credPrefs.putString("password", null);
        credPrefs.commit();
    }  //DELETE THE SAVED CREDENTIALS IF USER WANTS TO

    String formattedTime(subject sub){
        String rawTime= sub.startTime;
        String meridian =rawTime.substring(6,8);
        Log.d("meridian", meridian);
        int hour = Integer.parseInt(rawTime.substring(0,2));
        if(meridian.equals("PM") && hour<12){
            hour = hour+12;
            String t=hour+rawTime.substring(2);
            return t;
        }
        return rawTime;
    } //GET THE 24-HOUR FORMAT OF THE TIME OF THE SUBJECT

    void placeCorrectly(subject sub,List<subject> i){
        i.remove(sub);
        int subHour = Integer.parseInt(formattedTime(sub).substring(0,2));
        int subMin=Integer.parseInt(formattedTime(sub).substring(3,5));
        for(int count=0;count<i.size();count++){
            int checkHour =Integer.parseInt(formattedTime(i.get(count)).substring(0,2));
            if(subHour==checkHour){
                int checkMin =Integer.parseInt(formattedTime(i.get(count)).substring(3,5));
                if(subMin<checkMin){
                    i.add(count,sub);
                    return;
                }
            }
            else if(subHour<checkHour){
                i.add(count,sub);
                return;
            }
        }
    } //PLACE THE LAB IN THERE CORRECT POSITION BY INSERTION SORT

    void load(boolean x){
        if(x){
            loadView.setVisibility(View.VISIBLE);
            loginView.setVisibility(View.INVISIBLE);
        }
        else{
            loadView.setVisibility(View.INVISIBLE);
            loginView.setVisibility(View.VISIBLE);
        }
    }   //SWITCH BETWEEN LOADING SCREEN AND LOGIN SCREEN

    void getTeacherCabins(){
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vClass.teachers.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    try {
                        teacher newTeacher = snapshot.getValue(teacher.class);
                        vClass.teachers.add(newTeacher);
                    }
                    catch (Exception e){
                        //DO NOT ADD THE CHANGE REQUESTED TEACHER DETAILS
                    }
                }
                SharedPreferences.Editor editor = getSharedPreferences("teacherPrefs",MODE_PRIVATE).edit();
                editor.putString("teachers",(jsonBuilder.toJson(vClass.teachers)));
                editor.commit();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //DO NOTHING
            }
        });
    }  //GET THE CABIN DETAILS OF TEACHERS FORM FIREBASE DATABASE

    class tryUpdateDatabase extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            int customListCount=vClass.cablist.size();
            if(customListCount>0){
                for(int i=0;i<customListCount;i++){
                    database.child("custom").child(String.valueOf(i)).setValue(vClass.cablist.get(i));
                }
            }
            return null;
        }
    } //CREATE A REQUEST IN THE DATABASE TO UPDATE
}