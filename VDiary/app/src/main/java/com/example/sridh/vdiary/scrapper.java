package com.example.sridh.vdiary;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.sridh.vdiary.prefs.*;

import static com.example.sridh.vdiary.prefs.holidays;
import static com.example.sridh.vdiary.prefs.teachers;


public class scrapper extends AppCompatActivity {

    //for Notifications
    public static String title;
    public static Context context;
    //END

    EditText regBox,passBox;
    WebView loginWebView, attWebView,scheduleWebView;
    CheckBox cb;
    TextView tip;
    RelativeLayout loadView;
    ScrollView loginView;
    FloatingActionButton reload;
    Button login;
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
    List<String> attendedList = new ArrayList<>();
    List<subject> courses = new ArrayList<>();
    List<List<subject>> scheduleList = new ArrayList<>();

    public static String[] tips= new String[]{"Widget is better than having a screen shot in your gallery.","Switch notifications on, in settings menu to get notification about the next class.","Tap on the clock icon on the toolbar to view weekly schedule."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        start();
    }

    public void start() {
        if(readFromPrefs(context)){
            context.startActivity(new Intent(this, workSpace.class));
            ((Activity)context).overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
            finalise();
        }
        else{
            setUp();
            initWebViews();
        }
    } //STARTS THE PROCESSING OF SCRAPPING

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
                if(!webNotConnected(loginWebView.getTitle()) &&loginWebView.getUrl().equals("https://academicscc.vit.ac.in/student/stud_login.asp")) {
                    loginWebView.evaluateJavascript(getcmd("return document.getElementsByName(\"message\")[0].value"), new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String message) {
                            if (!message.equals("\"\"") && !message.equals("null")) {
                                if(!trim(message).equals("Verification Code does not match.  Enter exactly as shown.")) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    load(false);
                                }
                                else{
                                    loginWebView.evaluateJavascript(getcmd(vClass.autoCaptchaCommand), new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String captchaString) {
                                            placeCreds();
                                        }
                                    });
                                }
                                //captchaBox.setText("");
                            }
                            else{
                                loginWebView.evaluateJavascript(getcmd(vClass.autoCaptchaCommand), new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String captchaString) {
                                        placeCreds();
                                        //setCaptcha(captchaString); //CAPTCHA ADDED
                                    }
                                });
                            }
                        }
                    });
                    //String getCaptcha = getcmd("var img= document.getElementById('imgCaptcha'); var canvas = document.createElement('canvas'); canvas.width = img.naturalWidth; canvas.height = img.naturalHeight; canvas.getContext('2d').drawImage(img, 0, 0); return canvas.toDataURL('image/png').replace(/^data:image\\/(png|jpg);base64,/, '');");
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
            tip.setText("Connection Failed!");
            showRetry();
            return true;
        }
        return false;
    }  //RETURNS TRUE IF WEB IS NOT CONNECTED

    private class scheduleClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
                courses.clear();
                scheduleList.clear();
                if (!webNotConnected(scheduleWebView.getUrl()) && scheduleWebView.getUrl().equals("https://academicscc.vit.ac.in/student/course_regular.asp?sem=" + vClass.SEM)) {
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
            if (!webNotConnected(attWebView.getUrl()) && loggedIn) getAttendance();
        }
    } //WEBVIEWCLIENT TO GET ATTENDANCE

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finalise();
    }

    static String getcmd(String js){

        return "(function(){"+js+"})()";

    } //RETURN THE FUNCTION FORMAT OF THE GIVEN COMMAND

    /*private void setCaptcha(String imgString){
        if(!imgString.equals("null")) {
            byte[] decodedString = Base64.decode(imgString, 0);
            Bitmap capImg = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            //captcha.setImageBitmap(capImg);
            load(false);
        }else {
            showRetry();
        }
    } //CONVERTS THE BASE-64 STRING TO BITMAP IMAGE AND SETS TO CAPTCHA IMAGEVIEW*/

    private void setUp(){
        setContentView(R.layout.activity_login);
        vClass.setStatusBar(getWindow(),getApplicationContext(),R.color.taskbar_orange);
        vClass.getFonts(this);
        tip =(TextView)findViewById(R.id.status);
        tip.setTypeface(vClass.nunito_reg);
        tip.setText("Tip: "+getTip());
        loginView=(ScrollView)findViewById(R.id.loginView);
        loadView=(RelativeLayout)findViewById(R.id.loadView);
        regBox=(EditText)findViewById(R.id.regBox);
        passBox=(EditText)findViewById(R.id.passbox);
        cb=(CheckBox)findViewById(R.id.saveCreds);
        login=(Button)findViewById(R.id.login);
        login.setTypeface(vClass.nunito_bold);
        reload=(FloatingActionButton)findViewById(R.id.refresh_FloatButton);
        pb_loading=(ProgressBar)findViewById(R.id.pb_login);
        toggle_showPassword =(ImageButton)findViewById(R.id.toogle_showPassword);
        ((TextView)findViewById(R.id.tv_ffcs)).setTypeface(vClass.nunito_reg);
        ((TextView)findViewById(R.id.note)).setTypeface(vClass.nunito_reg);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tip.setText(getTip());
                loginWebView.setWebViewClient(new loginClient());
                loginWebView.loadUrl("https://academicscc.vit.ac.in/student/stud_login.asp");
                reload.setVisibility(View.INVISIBLE);
                pb_loading.setVisibility(View.VISIBLE);
                loggedIn = false;
                scheduleWebView.stopLoading();
                attWebView.stopLoading();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWebView.loadUrl("https://academicscc.vit.ac.in/student/stud_login.asp");
                hideSoftKeyboard(scrapper.this);
                load(true);
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
        load(false);
        readCreds();
    } //SETS RELATIVE LAYOUT OF THE MAIN PAGE

    void initWebViews(){
        loginWebView = new WebView(this);//(WebView)findViewById(R.id.loginWeb);
        scheduleWebView=new WebView(this);//(WebView)findViewById(R.id.scheduleWeb);
        scheduleWebView.setWebViewClient(new scheduleClient());
        scheduleWebView.getSettings().setDomStorageEnabled(true);
        scheduleWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.getSettings().setDomStorageEnabled(true);
        loginWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.setWebViewClient(new loginClient());
        attWebView = new WebView(this);
        attWebView.setWebViewClient(new attendanceClient());
        attWebView.getSettings().setDomStorageEnabled(true);
        attWebView.getSettings().setJavaScriptEnabled(true);
    } //INITIALIZE THE WEBVIEWS AND LAST LOADING THE LOGIN PAGE

    private void placeCreds(){
                String input="document.getElementsByName(\"regno\")[0].value=\""+regBox.getText().toString()+"\"; document.getElementsByName(\"passwd\")[0].value=\""+passBox.getText()+"\"; document.forms[0].submit();";
                loginWebView.evaluateJavascript(getcmd(input), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //CREDENTIALS ARE PLACED ON THEIR RESPECTIVE PLACES AND THE FORM IS SUBMITTED
                    }
                });
        // document.getElementsByName("vrfcd")[0].value=""+captchaBox.getText()+"";


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
    } //SHOW AND INSHOW PASSWORD CONTROLLER

    String trim(String str) throws StringIndexOutOfBoundsException{
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
        scheduleWebView.evaluateJavascript(getcmd("var rows=document.getElementsByTagName('table')[1].rows;var c;for(c=0;c<rows.length;c++){if(rows[c].cells.length==15){rows[c].deleteCell(0)}}"), new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if(isLoaded) {
                        scheduleWebView.evaluateJavascript("var rows=document.getElementsByTagName('table')[1].rows;var c;for(c=0;c<rows.length;c++){if(rows[c].cells.length==14){rows[c].deleteCell(0)}}", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows.length.toString()"), new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        int rows = Integer.parseInt(trim(value));
                                        for (int row = 1; row < rows - 2; row++) {
                                            final int rowa = row;
                                            scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + row + "].cells[8].innerText.toString()"), new ValueCallback<String>() {
                                                @Override
                                                public void onReceiveValue(String value) {
                                                    final String room = trim(value);
                                                    if (!room.equals("NIL")) {
                                                        final subject sub = new subject();
                                                        //ROOM
                                                        sub.room = room;
                                                        //CODE
                                                        scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[1].innerText.toString()"), new ValueCallback<String>() {
                                                            @Override
                                                            public void onReceiveValue(String code) {
                                                                sub.code = trim(code);
                                                            }
                                                        });
                                                        //NAME
                                                        scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[2].innerText.toString()"), new ValueCallback<String>() {
                                                            @Override
                                                            public void onReceiveValue(String name) {
                                                                sub.title = toTitleCase(trim(name));
                                                            }
                                                        });
                                                        //TEACHER
                                                        scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[9].innerText.toString()"), new ValueCallback<String>() {
                                                            @Override
                                                            public void onReceiveValue(String teacher) {
                                                                String rawTeacher = trim(teacher).split("-")[0];
                                                                sub.teacher = toTitleCase(rawTeacher.substring(0, rawTeacher.length() - 1));
                                                            }
                                                        });
                                                        //SLOT
                                                        scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[7].innerText.toString()"), new ValueCallback<String>() {
                                                            @Override
                                                            public void onReceiveValue(String resultSlot) {
                                                                sub.slot=trim(resultSlot);
                                                            }
                                                        });
                                                        //TYPE
                                                        scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[1].rows[" + rowa + "].cells[3].innerText.toString()"), new ValueCallback<String>() {
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
        scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table').length"), new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                getFromTable2(Integer.parseInt(s)-1);
            }
        });
    }  //CALCULATE THE INDEX OF THE TIMETABLE'S INDEX

    private void getFromTable2(final int index){
        scheduleWebView.evaluateJavascript(getcmd("document.getElementsByTagName('table')["+index+"].rows[0].deleteCell(7);"), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //LUNCH DELETED
                        if(isLoaded) {
                            for (int rowa = 2; rowa <= 6; rowa++) {
                                final int row = rowa;
                                final List<subject> today = new ArrayList<subject>();
                                scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[" + row + "].cells.length.toString()"), new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String rawCols) {
                                        //Toast.makeText(scrapper.this, rawCols, Toast.LENGTH_SHORT).show();
                                        final int cols = Integer.parseInt(trim(rawCols));
                                        final AtomicReference extraTime = new AtomicReference(0);
                                        for (int col = 1; col < cols; col++) {
                                            final int cell = col;
                                            scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[" + row + "].cells[" + cell + "].colSpan.toString()"), new ValueCallback<String>() {
                                                @Override
                                                public void onReceiveValue(String value) {
                                                    int rawcolSpan = Integer.parseInt(trim(value));
                                                    if (rawcolSpan > 1) {
                                                        extraTime.set(rawcolSpan - 1);
                                                    }
                                                    scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[" + row + "].cells['" + cell + "'].bgColor"), new ValueCallback<String>() {
                                                        @Override
                                                        public void onReceiveValue(String color) {
                                                            final String cellColor = trim(color);
                                                            scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[" + row + "].cells[" + cell + "].innerText.toString()"), new ValueCallback<String>() {
                                                                @Override
                                                                public void onReceiveValue(String value) {
                                                                    String text = trim(value);
                                                                    final subject sub = new subject();
                                                                    if (cellColor.equals("#CCFF33")) {
                                                                        String[] foundText= text.split("-");
                                                                        String rawCode = foundText[0];
                                                                        sub.code = rawCode.substring(0,rawCode.length()-1); //CODE
                                                                        String rawType = foundText[1];
                                                                        String type = rawType.substring(1, rawType.length() - 1); //TYPE
                                                                        sub.type = type;
                                                                        //TIME
                                                                        if (type.equals("ETH") || type.equals("SS") || type.equals("TH")) {
                                                                            scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[0].cells[" + (cell + Integer.parseInt(String.valueOf(extraTime.get()))) + "].innerText.toString()"), new ValueCallback<String>() {
                                                                                @Override
                                                                                public void onReceiveValue(String value) {
                                                                                    String time = trim(value);
                                                                                    sub.startTime = time.substring(0, 8);
                                                                                    sub.endTime = time.substring(14, time.length());
                                                                                    today.add(sub);
                                                                                }
                                                                            });
                                                                        } else {
                                                                            scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[1].cells[" + (cell + Integer.parseInt(String.valueOf(extraTime.get()))) + "].innerText.toString()"), new ValueCallback<String>() {
                                                                                @Override
                                                                                public void onReceiveValue(String value) {
                                                                                    String rawstime = trim(value);
                                                                                    String sTime = rawstime.substring(0, 8);
                                                                                    final AtomicReference<String> time = new AtomicReference<String>(sTime);
                                                                                    scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[1].cells[" + (cell + 1 + Integer.parseInt(String.valueOf(extraTime.get()))) + "].innerText.toString()"), new ValueCallback<String>() {
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
                                                                        scheduleWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')["+index+"].rows[1].cells[" + (cell + Integer.parseInt(String.valueOf(extraTime.get()))) + "].innerText.toString()"), new ValueCallback<String>() {
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
                                                                attWebView.setWebViewClient(new attendanceClient());
                                                                attWebView.loadUrl("https://academicscc.vit.ac.in/student/attn_report.asp?sem=" + vClass.SEM);
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
        if (attWebView.getUrl().equals("https://academicscc.vit.ac.in/student/attn_report.asp?sem="+vClass.SEM)) {
            attWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[2].cells[2].innerText"), new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    vClass.semStart = trim(value);
                    attWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[2].cells[3].innerText"), new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            vClass.cat1 = trim(value);
                            attWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[3].cells[3].innerText"), new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    vClass.cat2 = trim(value);
                                    attWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows[4].cells[3].innerText"), new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                            vClass.fat = trim(value);
                                            attWebView.loadUrl("https://academicscc.vit.ac.in/student/attn_report.asp?sem="+vClass.SEM+"&fmdt=" + vClass.semStart + "&todt=" + vClass.fat);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
        else if(attWebView.getUrl().toLowerCase().equals(("https://academicscc.vit.ac.in/student/attn_report.asp?sem="+vClass.SEM + "&fmdt=" + vClass.semStart + "&todt=" + vClass.fat).toLowerCase())){
            attWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[4].rows.length"), new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    final int rows = Integer.parseInt(value);
                    for (int i = 1; i < rows; i++) {
                        final int j = i;
                        attWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[4].rows[" + j + "].cells[8].innerText"), new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                attList.add(trim(value));
                                attWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[4].rows[" + j + "].cells[7].innerText"), new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        ctdList.add(trim(value));
                                        attWebView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[4].rows[" + j + "].cells[6].innerText"), new ValueCallback<String>() {
                                            @Override
                                            public void onReceiveValue(String s) {
                                                attendedList.add(trim(s));
                                                if (j == rows - 1) {
                                                    new compileInf().execute();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        workSpace.loadedAttView=attWebView;
                        workSpace.rowsInAtt=rows;
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
            super.onPreExecute();
            NetworkChangeReceiver.attachFirebaseListener(context);
        }

        @Override
        protected Void doInBackground(Void... params) {
            float sum=0;
            vClass.subList=courses;
            vClass.timeTable=scheduleList;
            for (int i=0;i<courses.size();i++){
                int ctd,attended;
                String attString;
                if(!attendanceStatus){
                    ctd=0;
                    attString=String.valueOf(0);
                    attended=0;
                }
                else{
                    ctd=Integer.parseInt(ctdList.get(i));
                    attString = attList.get(i)+"%";
                    attended=Integer.parseInt(attendedList.get(i));
                    sum+=Integer.parseInt(attList.get(i));
                }
                vClass.subList.get(i).ctd=ctd;
                vClass.subList.get(i).attString=attString;
                vClass.subList.get(i).classAttended=attended;
            }
            try{
                sum=sum/vClass.subList.size();
                put(context,avgAttendance,(int)(Math.ceil(sum)));
            }
            catch (Exception e){
                //ATTENDANCE NOT YET UPLOADED
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
                put(context,lastRefreshed,(new Gson()).toJson(Calendar.getInstance()));//editor.putString("last_ref",last_ref);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            workSpace.refreshedByScrapper=true;
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
                if(!sub.code.equals("")){
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent toNotifyService = new Intent(context,NotifyService.class);
                    toNotifyService.putExtra("fromClass","scheduleNotification");
                    Calendar calendar = GregorianCalendar.getInstance();
                    int startHour,startMin;
                    String time=formattedTime(sub);
                    startHour=Integer.parseInt(time.substring(0, 2));
                    startMin=Integer.parseInt(time.substring(3, 5));

                    calendar.setLenient(false);
                    calendar.set(GregorianCalendar.HOUR_OF_DAY,startHour);
                    calendar.set(GregorianCalendar.MINUTE,startMin);
                    calendar.set(GregorianCalendar.DAY_OF_WEEK,day);
                    calendar.set(GregorianCalendar.SECOND,0);

                    Notification_Holder newNotification =  new Notification_Holder(calendar,sub.title,sub.room,"Upcoming class in 5 minutes");
                    toNotifyService.putExtra("notificationContent",(new Gson()).toJson(newNotification));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,notificationCode,toNotifyService,0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 5 * 60 * 1000, 24 * 7 * 60 * 60 * 1000, pendingIntent);
                    notificationCode++;
                }
            }
            day++;
        }
        put(context,scheduleNotificationCount,notificationCode);
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
        put(context,allSub,jsonBuilder.toJson(vClass.subList));//editor.putString("allSub",jsonBuilder.toJson(vClass.subList));
        put(context,schedule,jsonBuilder.toJson(vClass.timeTable));//editor.putString("schedule",jsonBuilder.toJson(vClass.timeTable));
        put(context,isLoggedIn,true);//editor.putBoolean("isLoggedIn",true);
        updateWidget();
    } //WRITE ACADEMIC CONTENT TO SHARED PREFERENCES

    boolean readFromPrefs(Context context){
        String allSubJson = get(context,allSub,null); //academicPrefs.getString("allSub",null);
        String scheduleJson = get(context,schedule,null);//academicPrefs.getString("schedule",null);
        if(allSubJson!=null && scheduleJson!=null){
            return true;
        }
        return false;
    } //READ ACADEMIC CONTENT FROM SHARED PREFERENCES

    void saveCreds(){
        put(context,regNo,regBox.getText().toString());//credPrefs.putString("regNo",regBox.getText().toString());
        put(context,password,passBox.getText().toString());//credPrefs.putString("password", passBox.getText().toString());
    }  //SAVE THE CREDENTIALS IF THE USER WANTS TO

    void readCreds(){
        String reg=get(context,regNo,null);//credPrefs.getString("regNo",null);
        String pass= get(context,password,null);//credPrefs.getString("password",null);
        if(reg!=null && pass!=null){
            regBox.setText(reg);
            passBox.setText(pass);
        }
    }  //TRY TO READ THE SAVED CREDENTIAL FROM SHARED PREFERENCES

    void delCreds(){
        put(context,regNo,null);//credPrefs.putString("regNo",null);
        put(context,password,null);//credPrefs.putString("password", null);
    }  //DELETE THE SAVED CREDENTIALS IF USER WANTS TO

    static String formattedTime(subject sub){
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



    void showRetry(){
        load(true);
        reload.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.GONE);
        //captchaBox.setText("");
    } //SHOW THE RETRY VIEW

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
            scheduleWebView.loadUrl("https://academicscc.vit.ac.in/student/course_regular.asp?sem="+vClass.SEM);
            super.onPostExecute(aVoid);
        }
    }

    void updateWidget(){
        (new widgetServiceReceiver()).onReceive(context,(new Intent(context,widgetServiceReceiver.class)));
    }  //UPDATE THE WIDGET TO SHOW TODAYS SCHEDULE

    static String toTitleCase(String input){
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
    } //RESET THE DECISION VARIABLES

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    String getTip(){
        int tipId=get(context,tipid,0);
        if(tipId+1==tips.length) put(context,tipid,0);
        else put(context,tipid,tipId+1);
        return tips[tipId];
    }  //RETURNS THE TIP TO SHOW WHILE PROCESSING
}