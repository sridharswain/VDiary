package com.example.sridh.vdiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;



public class scrapper extends AppCompatActivity {

    //for Notifications
    public static String title;
    public static Context context;
    public static SharedPreferences shared;
    public static SharedPreferences.Editor editor;
    static int n_id=0;
    //END

    EditText regBox,passBox,captchaBox;
    WebView loginWebView,att,schedule;
    ImageView captcha;
    CheckBox cb;
    TextView status;
    RelativeLayout loadView;
    ScrollView loginView;
    FloatingActionButton reload,login;
    boolean attendanceStatus=true;
    boolean isPasswordShown=false;
    boolean loggedIn=false;
    boolean isLoaded = true;
    Gson jsonBuilder = new Gson();
    static boolean tryRefresh=false;
    ProgressBar pb_loading;
    ImageButton toggle_showPassword;
    List<String> attList = new ArrayList<>();
    List<String> ctdList = new ArrayList<>();
    List<subject> courses = new ArrayList<>();
    List<List<subject>> scheduleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        shared=context.getSharedPreferences("notiftimetable",Context.MODE_PRIVATE);
        editor=shared.edit();
        n_id=shared.getInt("id_time",0);

        //FIREBASE INITIATION
        vClass.setStatusBar(getWindow(),getApplicationContext(),R.color.taskbar_orange);
        getDimensions();
        vClass.getFonts(this);
        start();
    }

    void start() {
        if(!tryRefresh && readFromPrefs()){
            startActivity(new Intent(this, workSpace.class));
            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
            finalise();
        }
        else{
            initWebViews();
            setUp();
        }
    } //STARTS THE PROCESSING

    private class loginClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if(url.equals("https://academicscc.vit.ac.in/student/home.asp")){
                loggedIn=true;
                new waitForLogIn().execute();
                loginWebView.stopLoading();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view,url);
            if(!loggedIn) {
                if (webNotConnected(loginWebView.getTitle())) {}
                else if (loginWebView.getUrl().equals("https://academicscc.vit.ac.in/student/stud_login.asp")) {
                    loginWebView.evaluateJavascript(getcmd("return document.getElementsByName(\"message\")[0].value"), new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String message) {
                            if (!message.equals("\"\"") && !message.equals("null")) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                captchaBox.setText("");
                            }
                        }
                    });
                    String getCaptcha = getcmd("var img= document.getElementById('imgCaptcha'); var canvas = document.createElement('canvas'); canvas.width = img.naturalWidth; canvas.height = img.naturalHeight; canvas.getContext('2d').drawImage(img, 0, 0); return canvas.toDataURL('image/png').replace(/^data:image\\/(png|jpg);base64,/, '');");
                    loginWebView.evaluateJavascript(getCaptcha, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String captchaString) {
                            setCaptcha(captchaString);
                        }
                    });
                }
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view,handler,error);
            handler.proceed();
        }

    } // WEBVIEWCLIENT TO CONTROL LOGIN PAGE

    boolean webNotConnected(String webTitle) {
        if (webTitle.equals("") || webTitle.equals("Webpage not available") || webTitle.equals("Web page not available")) {
            if (tryRefresh) {
                Toast.makeText(getApplicationContext(), "Connection Failed!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(scrapper.this, workSpace.class));
                finalise();
            } else {
                status.setText("Connection Failed!");
                showRetry();
            }
            return true;
        }
        return false;
    }

    private class scheduleClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
                courses.clear();
                scheduleList.clear();
                if (schedule.getUrl().equals("https://academicscc.vit.ac.in/student/course_regular.asp?sem=" + vClass.SEM)) {
                    getFormTable1();
                    calculateTable2();
                }
                else if(loggedIn) {
                    new waitForLogIn().execute();
                }
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
            if (loggedIn) getAttendance();
        }
    } //WEBVIEWCLIENT TO GET ATTENDANCE

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finalise();
    }

    private String getcmd(String js){

        return "(function(){"+js+"})()";

    } //RETURN THE FUNCTION FORMAT OF THE GIVEN COMMAND

    private void setCaptcha(String imgString){
        if(!imgString.equals("null")) {
            byte[] decodedString = Base64.decode(imgString, 0);
            Bitmap capImg = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            captcha.setImageBitmap(capImg);
            load(false);
        }else {
            showRetry();
        }
    } //CONVERTS THE BASE-64 STRING TO BITMAP IMAGE AND SETS TO CAPTCHA IMAGEVIEW

    private void setUp(){
        setContentView(R.layout.activity_login);
        status=(TextView)findViewById(R.id.status);
        loginView=(ScrollView)findViewById(R.id.loginView);
        loadView=(RelativeLayout)findViewById(R.id.loadView);
        captcha=(ImageView)findViewById(R.id.captcha);
        regBox=(EditText)findViewById(R.id.regBox);
        passBox=(EditText)findViewById(R.id.passbox);
        captchaBox=(EditText)findViewById(R.id.captchaBox);
        cb=(CheckBox)findViewById(R.id.saveCreds);
        login=(FloatingActionButton)findViewById(R.id.login);
        reload=(FloatingActionButton)findViewById(R.id.refresh_FloatButton);
        pb_loading=(ProgressBar)findViewById(R.id.pb_login);
        toggle_showPassword =(ImageButton)findViewById(R.id.toogle_showPassword);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    loginWebView.setWebViewClient(new loginClient());
                    loginWebView.loadUrl("https://academicscc.vit.ac.in/student/stud_login.asp");
                    status.setText("Building Captcha...");
                    reload.setVisibility(View.INVISIBLE);
                    pb_loading.setVisibility(View.VISIBLE);
                    loggedIn = false;
                    schedule.stopLoading();
                    att.stopLoading();
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
        toggle_showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toogleShowPassword();
            }
        });
        load(true);
        readCreds();
        status.setText("Building Captcha...");
    } //SETS RELATIVE LAYOUT OF THE MAIN PAGE

    void initWebViews(){
        loginWebView =new WebView(this);
        schedule=new WebView(this);//new WebView(this);
        schedule.getSettings().setDomStorageEnabled(true);
        schedule.getSettings().setJavaScriptEnabled(true);
        loginWebView.getSettings().setDomStorageEnabled(true);
        loginWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.setWebViewClient(new loginClient());
        loginWebView.loadUrl("https://academicscc.vit.ac.in/student/stud_login.asp");
        att= new WebView(this);//new WebView(this);
        att.getSettings().setDomStorageEnabled(true);
        att.getSettings().setJavaScriptEnabled(true);
    } //INITIALIZE THE WEBVIEWS AND LAST LOADING THE LOGIN PAGE

    private void placeCreds(){
                String input="document.getElementsByName(\"regno\")[0].value=\""+regBox.getText().toString()+"\"; document.getElementsByName(\"passwd\")[0].value=\""+passBox.getText()+"\"; document.getElementsByName(\"vrfcd\")[0].value=\""+captchaBox.getText()+"\"; document.forms[0].submit();";
                loginWebView.evaluateJavascript(getcmd(input), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //CREDENTIALS ARE PLACED ON THEIR RESPECTIVE PLACES AND THE FORM IS SUBMITTED
                    }
                });


    } //SETS THE CREDENTIAL TO THE FORM AND SUBMITS IT

    void toogleShowPassword(){
        if(isPasswordShown){
            //DONT SHOW PASSWORD
            passBox.setTransformationMethod(new PasswordTransformationMethod());
            Glide.with(getApplicationContext()).load(R.drawable.ic_view_password).into(toggle_showPassword);
            isPasswordShown=false;
            passBox.setSelection(passBox.getText().length());
        }
        else{
            //SHOW PASSWORD
            passBox.setTransformationMethod(null);
            Glide.with(getApplicationContext()).load(R.drawable.ic_unview_password).into(toggle_showPassword);
            isPasswordShown=true;
            passBox.setSelection(passBox.getText().length());
        }
    }

    private String trim(String str) throws StringIndexOutOfBoundsException{
        try {
            str = str.substring(1);
            str = str.substring(0, str.indexOf("\""));
            isLoaded=true;
            return str;
        }
        catch (Exception e){
            isLoaded=false;
            new waitForLogIn().execute();
            return "0";
        }
    } //TRIMS THE GIVEN RESULT FROM JAVASCRIPT TO REMOVE QUOTES

    private void getFormTable1(){
            schedule.evaluateJavascript(getcmd("var rows=document.getElementsByTagName('table')[1].rows;var c;for(c=0;c<rows.length;c++){if(rows[c].cells.length==15){rows[c].deleteCell(0)}}"), new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if(isLoaded) {
                        schedule.evaluateJavascript("var rows=document.getElementsByTagName('table')[1].rows;var c;for(c=0;c<rows.length;c++){if(rows[c].cells.length==14){rows[c].deleteCell(0)}}", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows.length.toString()"), new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        int rows = Integer.parseInt(trim(value));
                                        for (int row = 1; row < rows - 2; row++) {
                                            final int rowa = row;
                                            schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + row + "].cells[8].innerText.toString()"), new ValueCallback<String>() {
                                                @Override
                                                public void onReceiveValue(String value) {
                                                    final String room = trim(value);
                                                    if (!room.equals("NIL")) {
                                                        final subject sub = new subject();
                                                        //ROOM
                                                        sub.room = room;
                                                        //CODE
                                                        schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[1].innerText.toString()"), new ValueCallback<String>() {
                                                            @Override
                                                            public void onReceiveValue(String code) {
                                                                sub.code = trim(code);
                                                            }
                                                        });
                                                        //NAME
                                                        schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[2].innerText.toString()"), new ValueCallback<String>() {
                                                            @Override
                                                            public void onReceiveValue(String name) {
                                                                sub.title = toTitleCase(trim(name));
                                                            }
                                                        });
                                                        //TEACHER
                                                        schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[9].innerText.toString()"), new ValueCallback<String>() {
                                                            @Override
                                                            public void onReceiveValue(String teacher) {
                                                                String rawTeacher = trim(teacher).split("-")[0];
                                                                sub.teacher = toTitleCase(rawTeacher.substring(0, rawTeacher.length() - 1));
                                                            }
                                                        });
                                                        //TYPE
                                                        schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[3].innerText.toString()"), new ValueCallback<String>() {
                                                            @Override
                                                            public void onReceiveValue(String rawtype) {
                                                                String type = trim(rawtype);
                                                                switch (type) {
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
                                                        courses.add(sub);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
    } //GET DATA FROM ALL COURSES
    void calculateTable2(){
        schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table').length"), new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                getFromTable2(Integer.parseInt(s)-1);
            }
        });
    }

    private void getFromTable2(final int index){
                schedule.evaluateJavascript(getcmd("document.getElementsByTagName('table')["+index+"].rows[0].deleteCell(7);"), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //LUNCH DELETED
                        if(isLoaded) {
                            for (int rowa = 2; rowa <= 6; rowa++) {
                                final int row = rowa;
                                final List<subject> today = new ArrayList<subject>();
                                schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[" + row + "].cells.length.toString()"), new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String rawCols) {
                                        //Toast.makeText(scrapper.this, rawCols, Toast.LENGTH_SHORT).show();
                                        final int cols = Integer.parseInt(trim(rawCols));
                                        final AtomicReference extraTime = new AtomicReference(0);
                                        for (int col = 1; col < cols; col++) {
                                            final int cell = col;
                                            schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[" + row + "].cells[" + cell + "].colSpan.toString()"), new ValueCallback<String>() {
                                                @Override
                                                public void onReceiveValue(String value) {
                                                    int rawcolSpan = Integer.parseInt(trim(value));
                                                    if (rawcolSpan > 1) {
                                                        extraTime.set(rawcolSpan - 1);
                                                    }
                                                    schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[" + row + "].cells['" + cell + "'].bgColor"), new ValueCallback<String>() {
                                                        @Override
                                                        public void onReceiveValue(String color) {
                                                            final String cellColor = trim(color);
                                                            schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[" + row + "].cells[" + cell + "].innerText.toString()"), new ValueCallback<String>() {
                                                                @Override
                                                                public void onReceiveValue(String value) {
                                                                    String text = trim(value);
                                                                    final subject sub = new subject();
                                                                    if (cellColor.equals("#CCFF33")) {
                                                                        sub.code = text.substring(0, 7); //CODE
                                                                        String rawType = text.split("-")[1];
                                                                        String type = rawType.substring(1, rawType.length() - 1); //TYPE
                                                                        sub.type = type;
                                                                        //TIME
                                                                        if (type.equals("ETH") || type.equals("SS") || type.equals("TH")) {
                                                                            schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[0].cells[" + (cell + Integer.parseInt(String.valueOf(extraTime.get()))) + "].innerText.toString()"), new ValueCallback<String>() {
                                                                                @Override
                                                                                public void onReceiveValue(String value) {
                                                                                    String time = trim(value);
                                                                                    sub.startTime = time.substring(0, 8);
                                                                                    sub.endTime = time.substring(14, time.length());
                                                                                    today.add(sub);
                                                                                }
                                                                            });
                                                                        } else {
                                                                            schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[1].cells[" + (cell + Integer.parseInt(String.valueOf(extraTime.get()))) + "].innerText.toString()"), new ValueCallback<String>() {
                                                                                @Override
                                                                                public void onReceiveValue(String value) {
                                                                                    String rawstime = trim(value);
                                                                                    String sTime = rawstime.substring(0, 8);
                                                                                    final AtomicReference<String> time = new AtomicReference<String>(sTime);
                                                                                    schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[1].cells[" + (cell + 1 + Integer.parseInt(String.valueOf(extraTime.get()))) + "].innerText.toString()"), new ValueCallback<String>() {
                                                                                        @Override
                                                                                        public void onReceiveValue(String value) {
                                                                                            String rawetime = trim(value);
                                                                                            String etime = rawetime.substring(14, rawetime.length());
                                                                                            sub.startTime = time.get();
                                                                                            sub.endTime = etime;
                                                                                            today.add(sub);
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                        }
                                                                    } else {
                                                                        sub.code = "";
                                                                        sub.title = text;
                                                                        sub.type = "";
                                                                        sub.room = "";
                                                                        schedule.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[1].cells[" + (cell + Integer.parseInt(String.valueOf(extraTime.get()))) + "].innerText.toString()"), new ValueCallback<String>() {
                                                                            @Override
                                                                            public void onReceiveValue(String value) {
                                                                                String time = trim(value);
                                                                                sub.startTime = time.substring(0, 8);
                                                                                sub.endTime = time.substring(14, time.length());
                                                                                today.add(sub);
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                            if (row == 6 && cell == cols - 1) {
                                                                att.setWebViewClient(new attendanceClient());
                                                                att.loadUrl("https://academicscc.vit.ac.in/student/attn_report.asp?sem=" + vClass.SEM);
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                                scheduleList.add(today);
                            }
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
                                            att.loadUrl("https://academicscc.vit.ac.in/student/attn_report.asp?sem="+vClass.SEM+"&fmdt=" + vClass.semStart + "&todt=" + vClass.fat);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
        else if(att.getUrl().toLowerCase().equals(("https://academicscc.vit.ac.in/student/attn_report.asp?sem="+vClass.SEM + "&fmdt=" + vClass.semStart + "&todt=" + vClass.fat).toLowerCase())){
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
                                            new compileInf().execute();
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
    }//GET DATA FROM THE ATTENDANCE PAGE

    class compileInf extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            status.setText("Summarizing Views...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
                vClass.subList=courses;
                vClass.timeTable=scheduleList;
                if (attendanceStatus) {
                    for (int i = 0; i < vClass.subList.size(); i++) {
                        vClass.subList.get(i).ctd = Integer.parseInt(ctdList.get(i));
                        vClass.subList.get(i).attString = attList.get(i) + "%";
                    }
                } else {
                    for (int i = 0; i < vClass.subList.size(); i++) {
                        vClass.subList.get(i).ctd = 0;
                        vClass.subList.get(i).attString = String.valueOf(0) + "%";
                    }
                }
                for (List<subject> i : vClass.timeTable) {
                    for (int count = 0; count < i.size(); count++) {
                        subject sub = getSubject(i.get(count).code, i.get(count).type);
                        if (sub != null) {
                            i.get(count).attString = sub.attString;
                            i.get(count).teacher = sub.teacher;
                            i.get(count).title = sub.title;
                            i.get(count).room = sub.room;
                            if (sub.type.equals("ELA") || sub.type.equals("LO")) {
                                i.remove(count + 1);
                                placeCorrectly(i.get(count), i);
                            }
                        }
                    }
                }
                writeToPrefs();
                createNotification(context,vClass.timeTable);
                Calendar calendar=Calendar.getInstance();
                String hr,min;
                if(calendar.get(Calendar.HOUR_OF_DAY)<10)
                    hr="0"+calendar.get(Calendar.HOUR_OF_DAY);
                else
                    hr=calendar.get(Calendar.HOUR_OF_DAY)+"";

                if(calendar.get(Calendar.MINUTE)<10)
                    min="0"+calendar.get(Calendar.MINUTE);
                else
                    min=calendar.get(Calendar.MINUTE)+"";

                String last_ref=calendar.get(Calendar.DATE)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR)+ "  "+ hr+":"+min;
                editor.putString("last_ref",last_ref);
                editor.apply();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivity(new Intent(scrapper.this,workSpace.class));
            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
            finish();
            try {
                this.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    } //REARRANGE THE INFORMATION SCRAPPED FORM THE WEBPAGE

    public static void createNotification(Context context,List<List<subject>> timeTable){
        int day=2;
        int notificationCode=1;
        for(List<subject> today: timeTable){
            for (subject sub : today){
                if(!sub.type.equals("")){
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent toNotifyService = new Intent(context,NotifyService.class);
                    toNotifyService.putExtra("fromClass","scheduleNotification");
                    Calendar calendar = Calendar.getInstance();
                    int startHour,startMin,AMPM;
                    startHour=Integer.parseInt(sub.startTime.substring(0, 2));
                    startMin=Integer.parseInt(sub.startTime.substring(3, 5));
                    String meridian = sub.startTime.substring(6);

                    if (meridian.equals("AM")) AMPM = 0;
                    else AMPM = 1;

                    calendar.set(Calendar.HOUR,startHour);
                    calendar.set(Calendar.MINUTE,startMin);
                    calendar.set(Calendar.AM_PM,AMPM);
                    calendar.set(Calendar.DAY_OF_WEEK,day);

                    Notification_Holder newNotification =  new Notification_Holder(calendar,sub.title,sub.room,"Upcoming class in 5 minutes");
                    toNotifyService.putExtra("notificationContent",(new Gson()).toJson(newNotification));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,notificationCode,toNotifyService,0);
                    notificationCode++;
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 5 * 60 * 1000, 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }
            }
            day++;
        }
    }

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
        editor =getSharedPreferences("isLoggedInPrefs",MODE_PRIVATE).edit();
        editor.putBoolean("isLoggedIn",true);
        editor.commit();
        updateWidget();
    } //WRITE ACADEMIC CONTENT TO SHARED PREFERENCES

    boolean readFromPrefs(){
        SharedPreferences academicPrefs= getSharedPreferences("academicPrefs",MODE_PRIVATE);
        String allSubJson=academicPrefs.getString("allSub",null);
        String scheduleJson =academicPrefs.getString("schedule",null);
        SharedPreferences teacherPrefs=getSharedPreferences("teacherPrefs",MODE_PRIVATE);
        String teachers =teacherPrefs.getString("teachers",null);
        SharedPreferences holidayPrefs= getSharedPreferences("holidayPrefs",MODE_PRIVATE);
        String holidays= holidayPrefs.getString("holidays",null);
        String customTeachers=teacherPrefs.getString("customTeachers",null);
        if(customTeachers!=null){
            vClass.cablist=jsonBuilder.fromJson(customTeachers,new TypeToken<List<Cabin_Details>>(){}.getType());
        }
        if(teachers!=null){
            vClass.teachers = jsonBuilder.fromJson(teachers,new TypeToken<List<teacher>>(){}.getType());
        }
        if(holidays!=null){
            vClass.holidays=jsonBuilder.fromJson(holidays,new TypeToken<List<holiday>>(){}.getType());
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
        Firebase.setAndroidContext(this);
        Firebase database= new Firebase(vClass.FIREBASE_URL);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vClass.teachers.clear();
                DataSnapshot teachers=dataSnapshot.child("teachers");
                for(DataSnapshot snapshot:teachers.getChildren()){
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



    void showRetry(){
        load(true);
        status.setText("Connection Failed!");
        reload.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.GONE);
        captchaBox.setText("");
    } //SHOW THE RETRY VIEW
    void getDimensions(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        vClass.width=dm.widthPixels;
        vClass.height=dm.heightPixels;
    }

    class waitForLogIn extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            status.setText("Fetching Courses...");
            schedule.setWebViewClient(new scheduleClient());
            schedule.loadUrl("https://academicscc.vit.ac.in/student/course_regular.asp?sem="+vClass.SEM);
            getTeacherCabins();
            getHolidays(context);
            super.onPostExecute(aVoid);
        }
    }

    void updateWidget(){
        (new widgetServiceReceiver()).onReceive(context,(new Intent(context,widgetServiceReceiver.class)));
    }

    String toTitleCase(String input){
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            }
            else if (nextTitleCase) {
                c = Character.toUpperCase(c);
                nextTitleCase = false;
            }
            else{
                c=Character.toLowerCase(c);
                nextTitleCase=false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }


    void finalise(){
        tryRefresh=false;
        isLoaded=false;
        loggedIn=false;
        finish();
    }

    void getHolidays(final Context context){
        Firebase.setAndroidContext(context);
        final Firebase database= new Firebase(vClass.FIREBASE_URL);
        database.child("Holidays").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String dateString = snapshot.getValue().toString();
                    Calendar c = Calendar.getInstance();
                    c.set(Integer.parseInt(dateString.substring(6)),Integer.parseInt(dateString.substring(3,5))-1,Integer.parseInt(dateString.substring(0,2)));
                    vClass.holidays.add(new holiday(c,snapshot.getKey()));
                }
                Gson serializer = new Gson();
                SharedPreferences.Editor holidays= context.getSharedPreferences("holidayPrefs",Context.MODE_PRIVATE).edit();
                holidays.putString("holidays",serializer.toJson(vClass.holidays));
                holidays.apply();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }
}