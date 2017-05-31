package com.example.sridh.vdiary;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.ExceptionCatchingInputStream;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.sridh.vdiary.prefs.*;
import static com.example.sridh.vdiary.scrapper.getcmd;
import static com.example.sridh.vdiary.scrapper.toTitleCase;
import static com.example.sridh.vdiary.vClass.CurrentTheme;
import static com.example.sridh.vdiary.vClass.MY_CAMPUS;
import static com.example.sridh.vdiary.vClass.getCurrentTheme;


public class workSpace extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;
    static Context context;

    static int id = 1000;


    static ListView resultList;
    static EditText teacherSearch;

    public static listAdapter_courses courseAdapter;
    static WebView loginWebView, attWebView, scheduleWebView;

    static WebView loadedAttView;
    WebView luFetchView;
    static int rowsInAtt;
    public static boolean loggedIn=false;
    boolean isLoaded = true;
    List<String> attList = new ArrayList<>();
    List<String> ctdList = new ArrayList<>();
    List<String> attendedList = new ArrayList<>();
    List<subject> courses = new ArrayList<>();
    List<List<subject>> scheduleList = new ArrayList<>();
    Gson jsonBuilder= new Gson();
    boolean attendanceStatus=true;
    public static boolean refreshing=false;
    public static boolean refreshedByScrapper=false;

    ProgressBar pb_syncing;
    ImageButton action_sync;

    public static TextView currentShowSubjectTextView=null;
    public static int currentShowing = -1;

    public static showSubject currentInView=null;

    boolean isPasswordChanged=false;
    static themeProperty ThemeProperty ;


    @Override
    public void onBackPressed() {
        if (resultList.getVisibility() == View.VISIBLE) {
            resultList.setVisibility(View.INVISIBLE);
        } else finish();
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        CurrentTheme = prefs.getTheme(context); //GETTING THE THEME FROM THE SHARED PREFERENCES
        ThemeProperty= getCurrentTheme();
        setTheme(ThemeProperty.theme);

        View rootView = getLayoutInflater().inflate(R.layout.activity_workspace,null);
        setContentView(rootView);
        setOnTouchListener(rootView,workSpace.this);
        vClass.getFonts(context);
        getDimensions();
        id = get(context,notificationIdentifier,1000);

        readFromPrefs(getApplicationContext());  //set all the data to the environment variables



        String z = get(context,lastRefreshed,"");//s.getString("last_ref", "");
        if (!z.equals("")) {
            try {
                Calendar lastSynced = new Gson().fromJson(z, new TypeToken<Calendar>() {
                }.getType());
                Toast.makeText(context, "Last synced on " + getDateTimeString(lastSynced), Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                Toast.makeText(context, "Last synced on " + z, Toast.LENGTH_LONG).show();
            }
        }
        String get_list = get(context,todolist,null);//shared.getString("todolist", null);
        if (get_list != null) {
            vClass.notes = Notification_Holder.convert_from_jason(get_list);
        }
        //vClass.notes is initialized
        setToolbars();
        //shared.getInt("notificationIdentifier", 1000);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setTabLayout(tabLayout);
        luFetchView = new WebView(this);
        luFetchView.getSettings().setJavaScriptEnabled(true);
        luFetchView.getSettings().setDomStorageEnabled(true);
        if(!refreshedByScrapper){
            refreshing=true;
            initWebViews();
        }
        else{
            pb_syncing.setVisibility(View.VISIBLE);
            action_sync.setVisibility(View.GONE);
            getlastDayUpdated(1);
        }


    }

    boolean readFromPrefs(Context context){
        Gson jsonBuilder = new Gson();
        String allSubJson = get(context,allSub,null); //academicPrefs.getString("allSub",null);
        String scheduleJson = get(context,schedule,null);//academicPrefs.getString("schedule",null);
        String teachersJson = get(context,teachers,null);//teacherPrefs.getString("teachers",null);
        String holidaysJson = get(context,holidays,null);//holidayPrefs.getString("holidays",null);
        String customTeachersJson = get(context,customTeachers,null);//teacherPrefs.getString("customTeachers",null);
        if(customTeachersJson!=null){
            vClass.cablist=jsonBuilder.fromJson(customTeachersJson,new TypeToken<List<Cabin_Details>>(){}.getType());
        }
        if(teachersJson!=null){
            vClass.teachers = jsonBuilder.fromJson(teachersJson,new TypeToken<List<teacher>>(){}.getType());
        }
        if(holidaysJson!=null){
            vClass.holidays=jsonBuilder.fromJson(holidaysJson,new TypeToken<List<holiday>>(){}.getType());
        }
        if(allSubJson!=null && scheduleJson!=null){
            Log.d("timeTabledda",allSubJson);
            Log.d("timeTabledaca",scheduleJson);
            vClass.subList=jsonBuilder.fromJson(allSubJson,new TypeToken<ArrayList<subject>>(){}.getType());
            vClass.timeTable=jsonBuilder.fromJson(scheduleJson, new TypeToken<ArrayList<ArrayList<subject>>>(){}.getType());
            return true;
        }
        return false;
    } //READ ACADEMIC CONTENT FROM SHARED PREFERENCE

    void getlastDayUpdated(final int index){
        if(index>=rowsInAtt) {
            if(get(context,isLoggedIn,true)) {
                pb_syncing.setVisibility(View.GONE);
                action_sync.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Synced", Toast.LENGTH_SHORT).show();
                put(context, allSub, (new Gson()).toJson(vClass.subList));
            }
        }
        else{
            loadedAttView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[4].rows['" + index + "'].cells[10].innerHTML"), new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    String form= "<html><body><form method='POST' action='"+MY_CAMPUS+"student/attn_report_details.asp'>"+s.substring(1,s.length()-1).replace("\\u003C","<").replace("\\\"","").replace("\\u003E",">")+"</form></body></html>";
                    luFetchView.setWebViewClient(new lastUpdatedWebClient(index));
                    luFetchView.loadDataWithBaseURL(null, form, "text/html", "utf-8", null);
                }
            });
        }
    } //FETCHES THE LAST UPLOADED INFORMATION EVENTUALLY

    class lastUpdatedWebClient extends WebViewClient{
        int index =0;
        boolean loaded=false;
        public lastUpdatedWebClient(int index){
            this.index=index;
        }
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            super.onPageFinished(view, url);
            if (!loaded){
                loaded=true;
                view.evaluateJavascript(getcmd("document.forms[0].submit()"), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        //loaded=true;
                    }
                });
            }
            else {
                if (!webNotConnected(view.getTitle())) {
                    view.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows.length"), new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(final String length) {
                            view.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows['" + (Integer.parseInt(length) - 1) + "'].cells[1].innerText"), new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    try {
                                        vClass.subList.get(index - 1).lastUpdated = trim(s);
                                        try {
                                            if (currentShowing == index - 1) {
                                                currentShowSubjectTextView.setText("Last Uploaded: " + vClass.subList.get(index - 1).lastUpdated);
                                            }
                                        } catch (Exception e) {
                                            //DO NOTHING
                                        }
                                        Log.d("last Updated", s);
                                        getAllAttInf(2, view, vClass.subList.get(index - 1).attTrack, Integer.parseInt(length), index);
                                    }catch (Exception e){
                                        //USER LOGGEDOUT
                                        return;
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }
    }  //WEBCLIENT FOR FETCHING THE LAST UPLOADED DATE FOR EACH SUBJECT

    void getAllAttInf(final int index, final WebView webView, final List<subjectDay> attTrack, final int dayLength,final int x) {
        if (index == dayLength) {
            getlastDayUpdated(x+1);
            if(currentShowing==x-1){
                try {
                    currentInView.finish();
                    Intent showSubjectIntent = new Intent(context, showSubject.class);
                    showSubjectIntent.putExtra("position", currentShowing);
                    startActivity(showSubjectIntent);
                }
                catch(Exception e){
                    //SHOWSUBJECT NULL POINTER
                }
            }
            put(context, allSub, (new Gson()).toJson(vClass.subList));
            return;
        }
        final subjectDay newSubjectDay = new subjectDay();
        webView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows['" + index + "'].cells[1].innerText"), new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String date) {
                newSubjectDay.date=trim(date);
                webView.evaluateJavascript(getcmd("return document.getElementsByTagName('table')[2].rows['" + index + "'].cells[3].innerText"), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String isPresent) {
                        if(!trim(isPresent).equals("Absent")) newSubjectDay.isPresent=true;
                        else newSubjectDay.isPresent=false;
                        attTrack.add(newSubjectDay);
                        getAllAttInf(index+1,webView,attTrack,dayLength,x);
                    }
                });
            }
        });
    }

    private void initWebViews(){
        pb_syncing.setVisibility(View.VISIBLE);
        action_sync.setVisibility(View.GONE);
        attList = new ArrayList<>();
        ctdList = new ArrayList<>();
        attendedList = new ArrayList<>();
        courses = new ArrayList<>();
        scheduleList = new ArrayList<>();
        loginWebView =new WebView(context);
        scheduleWebView =new WebView(context);
        scheduleWebView.getSettings().setDomStorageEnabled(true);
        scheduleWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.getSettings().setDomStorageEnabled(true);
        loginWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.setWebViewClient(new loginClient());
        loginWebView.loadUrl(MY_CAMPUS+"student/stud_login.asp");
        attWebView = new WebView(context);
        attWebView.getSettings().setDomStorageEnabled(true);
        attWebView.getSettings().setJavaScriptEnabled(true);

    } //INITIALIZE THE WEBVIEWS AND LAST LOADING THE LOGIN PAGE


    private class loginClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if(!isPasswordChanged) {
                if (url.equals(MY_CAMPUS + "student/home.asp")) {
                    loggedIn = true;
                    new waitForLogIn().execute();
                    Log.d("Logged In", "Logged IN");
                    loginWebView.stopLoading();
                }
            }
            else{
                refreshing=false;
                pb_syncing.setVisibility(View.GONE);
                action_sync.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Password was Changed!\nLogin again", Toast.LENGTH_LONG).show();
                loginWebView.stopLoading();
            }
        }
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view,url);
            Log.d("webviewTitle",loginWebView.getTitle());
            if(!loggedIn) {
                if(!isPasswordChanged){
                if(!webNotConnected(loginWebView.getTitle()) && loginWebView.getUrl().equals(MY_CAMPUS+"student/stud_login.asp")) {
                    loginWebView.evaluateJavascript(getcmd("return document.getElementsByName(\"message\")[0].value"), new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String message) {
                            if (!message.equals("\"\"") && !message.equals("null")) {
                                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                isPasswordChanged = true;

                            }
                        }
                    });
                    //String getCaptcha = getcmd("var img= document.getElementById('imgCaptcha'); var canvas = document.createElement('canvas'); canvas.width = img.naturalWidth; canvas.height = img.naturalHeight; canvas.getContext('2d').drawImage(img, 0, 0); return canvas.toDataURL('image/png').replace(/^data:image\\/(png|jpg);base64,/, '');");
                    loginWebView.evaluateJavascript(getcmd(vClass.autoCaptchaCommand), new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String captchaString) {
                            Log.d("Status", "Captcha Added");
                            //setCaptcha(captchaString); //CAPTCHA ADDED
                            String reg = get(context, regNo, null);
                            String pass = get(context, password, null);
                            String input = "document.getElementsByName(\"regno\")[0].value=\"" + reg + "\"; document.getElementsByName(\"passwd\")[0].value=\"" + pass + "\"; document.forms[0].submit();";
                            loginWebView.evaluateJavascript(getcmd(input), new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    //CREDENTIALS ARE PLACED ON THEIR RESPECTIVE PLACES AND THE FORM IS SUBMITTED
                                }
                            });
                        }
                    });
                }
                }
            }
        }
    }  //HANDLES PAGE FINISHED OF THE LOGIN PAGE

    boolean webNotConnected(String webTitle) {
        Log.d("WebviewURL",webTitle);
        if (webTitle.equals("") || webTitle.equals("Webpage not available") || webTitle.equals("Web page not available")) {
            refreshing=false;
            pb_syncing.setVisibility(View.GONE);
            action_sync.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }  //RETURN TRUE IF WEB IS NOT CONNECTED

    class waitForLogIn extends AsyncTask<Void,Void,Void> {
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
            try {
                scheduleWebView.setWebViewClient(new scheduleClient());
                scheduleWebView.loadUrl(MY_CAMPUS+"student/course_regular.asp?sem=" + vClass.SEM);
                super.onPostExecute(aVoid);
            }
            catch (Exception e){
                //DO NOTHING
            }
        }
    }  //CHECKS IF THE SERVERS HAS ACCEPTED THE LOGIN REQUESTED

    private class scheduleClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("Status",url);
            courses.clear();
            scheduleList.clear();
            if (scheduleWebView.getUrl().equals(MY_CAMPUS+"student/course_regular.asp?sem=" + vClass.SEM)) {
                getFromTable1();
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

    private void getFromTable1(){
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
    }  //GETS THE INDEX OF THE SCHEDULE TABLE FROM THE WEBPAGE

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
                                                        attWebView.loadUrl(MY_CAMPUS+"student/attn_report.asp?sem=" + vClass.SEM);
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

    private class attendanceClient extends WebViewClient{
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("Status",url);
            if (loggedIn) getAttendance();
        }
    } //WEBVIEWCLIENT TO GET ATTENDANCE

    private void getAttendance() {
        if (attWebView.getUrl().equals(MY_CAMPUS+"student/attn_report.asp?sem="+vClass.SEM)) {
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
                                            attWebView.loadUrl(MY_CAMPUS+"student/attn_report.asp?sem="+vClass.SEM+"&fmdt=" + vClass.semStart + "&todt=" + vClass.fat);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
        else if(attWebView.getUrl().toLowerCase().equals((MY_CAMPUS+"student/attn_report.asp?sem="+vClass.SEM + "&fmdt=" + vClass.semStart + "&todt=" + vClass.fat).toLowerCase())){
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
                    }
                    loadedAttView=attWebView;
                    rowsInAtt=rows;
                    getlastDayUpdated(1);
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
                put(context,avgAttendance,((int)(Math.ceil(sum))));
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
            cancelNotifications(context);
            createNotification(context,vClass.timeTable);
            put(context,lastRefreshed,(new Gson()).toJson(Calendar.getInstance()));//editor.putString("last_ref",last_ref);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                courseAdapter.update(vClass.subList);
            }
            catch (Exception e){
                //COURSE ADAPTER NIT YET READY
            }
            refreshing=false;
            vClass.isSyncedThisSession=true;
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
                    Calendar calendar = Calendar.getInstance();
                    int startHour,startMin,AMPM;
                    String time=formattedTime(sub);
                    startHour=Integer.parseInt(time.substring(0, 2));
                    startMin=Integer.parseInt(time.substring(3, 5));

                    calendar.setLenient(false);
                    calendar.set(Calendar.HOUR_OF_DAY,startHour);
                    calendar.set(Calendar.MINUTE,startMin);
                    calendar.set(Calendar.DAY_OF_WEEK,day);
                    calendar.set(Calendar.SECOND,0);

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
        if(get(context,isLoggedIn,false)) {
            put(context, allSub, jsonBuilder.toJson(vClass.subList));//editor.putString("allSub",jsonBuilder.toJson(vClass.subList));
            put(context, schedule, jsonBuilder.toJson(vClass.timeTable));//editor.putString("schedule",jsonBuilder.toJson(vClass.timeTable));
            updateWidget();
        }
    } //WRITE ACADEMIC CONTENT TO SHARED PREFERENCES

    static void updateWidget(){
        (new widgetServiceReceiver()).onReceive(context,(new Intent(context,widgetServiceReceiver.class)));
    }  // UPDATE THE CONTENTS OF WIDGET TO SHOW TODAYS SCHEDULE

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
    }  //REMOVE THE QUOTES FROM THE RESULT SCRAPPED FORM THE WEBPAGE

    void setTabLayout(TabLayout tabLayout){
        GradientDrawable softShape = (GradientDrawable) tabLayout.getBackground();
        softShape.setColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
        final int[] unselectedDrawables= new int[]{R.drawable.notselected_course_book,R.drawable.notselected_teacher,R.drawable.notselected_tasks,R.drawable.notselected_summary};
        final int[] selectedDrawables = new int[]{R.drawable.selected_course_book,R.drawable.selected_teacher,R.drawable.selected_tasks,R.drawable.selected_summary};
        for(int i=1;i<4;i++){
            tabLayout.getTabAt(i).setIcon(unselectedDrawables[i]);
        }
        tabLayout.getTabAt(0).setIcon(R.drawable.selected_course_book);
        //TABICONS INITIALISED
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                tab.setIcon(selectedDrawables[position]);
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                tab.setIcon(unselectedDrawables[position]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }  //CONTROLS THE IMAGES WHILE SWITCHING THE TABS

    static void delPrefs(){
        put(context,allSub,null);//editor.putString("allSub",jsonBuilder.toJson(vClass.subList));
        put(context,schedule,null);//editor.putString("schedule",jsonBuilder.toJson(vClass.timeTable));
        put(context,isLoggedIn,false);//editor.putBoolean("isLoggedIn",true);
        updateWidget();
    }  //DELETES THE PREFERENCES WHEN LOGOUT IS PRESSED

    void setToolbars() {
        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        GradientDrawable softShape = (GradientDrawable) appBarLayout.getBackground();
        softShape.setColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
        Toolbar toolbar = (Toolbar) findViewById(R.id.workspacetoptoolbar);
        pb_syncing=(ProgressBar)toolbar.findViewById(R.id.pb_syncing);
        action_sync=(ImageButton)toolbar.findViewById(R.id.action_sync);
        action_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!refreshing && !vClass.isSyncedThisSession){
                    refreshing=true;
                    initWebViews();
                }
            }
        });
        toolbar.inflateMenu(R.menu.menu_workspace_top);
        toolbar.setBackgroundColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
        TextView title = (TextView)toolbar.findViewById(R.id.workSpace_title);
        title.setTypeface(vClass.fredoka);
        //setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();

                switch (id){
                    case R.id.toSchedule:
                        Intent i = new Intent(workSpace.this, schedule.class);
                        startActivity(i);
                        break;
                }
                return true;
            }
        });
    }  //SET THE TOOLBARS FOR THE WORKSPACE CLASS

    public static void confirmLogout(final Context context,final Activity activity){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        cancelNotifications(context);
                        delPrefs();
                        try{
                            loginWebView.stopLoading();
                            attWebView.stopLoading();
                            scheduleWebView.stopLoading();
                            loadedAttView.stopLoading();
                        }
                        catch (Exception E){
                            //DO NOTHING...
                        }
                        currentShowing=-1;
                        currentInView=null;
                        currentShowSubjectTextView=null;
                        context.startActivity(new Intent(context, scrapper.class));
                        activity.overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                        activity.finish();
                        try {
                            this.finalize();
                        } catch (Throwable throwable) {
                            //throwable.printStackTrace();
                        }
                        break;
                }
            }
        };

        builder.setMessage("Doing this will delete all data!\n\nAre you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);
        AlertDialog confirmLogoutDialog = builder.create();
        confirmLogoutDialog.show();
    }  //ASK FOR CONFIRMATION TO LOGOUT

    static void cancelNotifications(Context context) {
        int day=2;
        int notificationCode=1;
        for(List<subject> today: vClass.timeTable){
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
                    alarmManager.cancel(pendingIntent);
                    notificationCode++;
                }
            }
            day++;
        }
    } //CANCEL ALL THE NOTIFICATION OF THE SUBJECTS



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        List<teacher> searchResult;
        TextView noNotesText;

        public PlaceholderFragment(){
        }
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 final Bundle savedInstanceState) {
            switch (getArguments().getInt(ARG_SECTION_NUMBER) - 1) {
                case 0:
                    View rootViewCourse = inflater.inflate(R.layout.fragment_courses, container, false);
                    setOnTouchListener(rootViewCourse,getActivity());
                    ListView lview = (ListView) rootViewCourse.findViewById(R.id.course_listview);
                    listAdapter_courses cadd = new listAdapter_courses(context, vClass.subList);
                    lview.setAdapter(cadd);
                    workSpace.courseAdapter=cadd;
                    lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            workSpace.currentShowing=position;
                            Intent showSubjectIntent = new Intent(context, showSubject.class);
                            showSubjectIntent.putExtra("position", position);
                            startActivity(showSubjectIntent);
                        }
                    });
                    return rootViewCourse;
                case 1:
                    View rootViewteachers = inflater.inflate(R.layout.fragment_teachers, container, false);
                    teacherSearch = (EditText) rootViewteachers.findViewById(R.id.teachers_searchText);
                    resultList = (ListView) rootViewteachers.findViewById(R.id.teachers_search_list);
                    resultList.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            hideSoftKeyboard(getActivity());
                            return false;
                        }
                    });
                    setOnViewTouchListener(rootViewteachers,getActivity());
                    setSearcher();
                    FloatingActionButton fab = (FloatingActionButton) rootViewteachers.findViewById(R.id.teachers_add);
                    ListView lv = (ListView) rootViewteachers.findViewById(R.id.teachers_list);
                    setOnTouchListener(lv,getActivity());
                    final listAdapter_teachers mad = new listAdapter_teachers(context, vClass.cablist);
                    listAdapter_searchTeacher.teacherAdapter = mad;
                    lv.setAdapter(mad);
                    fab.setOnClickListener(new View.OnClickListener() { //Onclick Listener for floating action Button
                        @Override
                        public void onClick(View v) {
                            showCabinAlertDialog(mad);
                        }
                    });
                    return rootViewteachers;
                case 2:
                    View rootViewNotes = inflater.inflate(R.layout.fragment_notes, container, false);
                    setOnTouchListener(rootViewNotes,getActivity());
                    taskGridLeft = (LinearLayout) rootViewNotes.findViewById(R.id.task_grid_view_left);
                    int taskViewWidth = ((int) (vClass.width * 0.492));
                    taskGridLeft.getLayoutParams().width = taskViewWidth;
                    taskGridRight = (LinearLayout) rootViewNotes.findViewById(R.id.task_grid_view_right);
                    taskGridRight.getLayoutParams().width = taskViewWidth;
                    noNotesText = (TextView)rootViewNotes.findViewById(R.id.ifNoteExists);
                    noNotesText.setTypeface(vClass.nunito_bold);
                    populateTaskGrid();
                    FloatingActionButton fb = (FloatingActionButton) rootViewNotes.findViewById(R.id.notes_add);
                    fb.setOnClickListener(new View.OnClickListener() { //Floating action button onclick listener
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alert;
                            View root = getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_todo, null);
                            final EditText title = (EditText) root.findViewById(R.id.title);
                            final EditText other = (EditText) root.findViewById(R.id.note);
                            final Switch reminderSwitch =(Switch)root.findViewById(R.id.add_todo_reminder_switch);
                            Toolbar addTaskToolbar=((Toolbar)root.findViewById(R.id.add_task_toolbar));
                            addTaskToolbar.inflateMenu(R.menu.menu_add_todo);
                            reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                                    if(checked) {
                                        c = null;
                                        showReminderSetter(reminderSwitch);
                                    }
                                    else {
                                        c = null;
                                        reminderSwitch.setText("Set Reminder");
                                    }
                                }
                            });
                            AlertDialog.Builder bui = new AlertDialog.Builder(context);
                            bui.setView(root);
                            alert = bui.create();
                            alert.show();
                            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    hideSoftKeyboard(getActivity());
                                }
                            });
                            addTaskToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    int id = item.getItemId();
                                    if(id==R.id.action_add_todo) {
                                        if (title.getText().toString() != null && title.getText().toString().equals("") != true & other.getText().toString() != null && other.getText().toString().equals("") != true) {
                                            Notification_Holder n;
                                            if(c!=null) {
                                                n = new Notification_Holder(c, title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                                schedule_todo_notification(n);
                                                c=null;
                                            }
                                            else
                                                n = new Notification_Holder(Calendar.getInstance(), title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                            vClass.notes.add(n);
                                            populateTaskGrid();
                                            Gson json = new Gson();
                                            String temporary = json.toJson(vClass.notes);
                                            put(context,todolist,temporary);//editor.putString("todolist", temporary);
                                            alert.cancel();
                                            workSpace.hideSoftKeyboard(getActivity());
                                        } else
                                            Toast.makeText(getContext(), "Both title and note must contain some text", Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                    else if(id== R.id.action_cancel_todo){
                                        alert.cancel(); //TODO TEST FOR THE CANCELLATION OF THE ALERT DIALOG
                                    }
                                    return false;
                                }
                            });
                        }
                    });
                    return rootViewNotes;
                case 3:
                    return getSummaryView();
            }
            return null;
        }

        View getSummaryView(){
            View rootViewSummary = getActivity().getLayoutInflater().inflate(R.layout.fragment_summary,null);
            setOnTouchListener(rootViewSummary,getActivity());
            PieChart pie = (PieChart)rootViewSummary.findViewById(R.id.avgAtt);
            pie.setLayoutParams(new RelativeLayout.LayoutParams(((int)(vClass.width*0.53)),((int)(vClass.height*0.35))));
            int avg = get(context,avgAttendance,0);
            pie.setCenterText("Avg\n"+get(context,avgAttendance,0)+"%");
            pie.setCenterTextTypeface(vClass.nunito_Extrabold);
            if(avg<75 ) pie.setCenterTextColor(Color.RED);
            else pie.setCenterTextColor(Color.BLACK);
            pie.setCenterTextSize(25);
            ArrayList<Entry> pieEntry= new ArrayList<>();
            pieEntry.add(new Entry(avg,0));
            pieEntry.add(new Entry(100-avg,1));
            ArrayList<String> labels=new ArrayList<>();
            labels.add("");
            labels.add("");
            PieDataSet dataSet = new PieDataSet(pieEntry,"");
            dataSet.setColors(ColorTemplate.createColors(getResources(),new int[]{ThemeProperty.colorPrimaryDark,ThemeProperty.colorPrimary})); //TODO APPLY CHNAGES ACORDING TO THE THEME OF THE APP
            PieData data = new PieData(labels,dataSet);
            pie.setData(data);
            pie.setDescription("");
            pie.setDrawSliceText(false);
            data.setDrawValues(false);
            pie.getLegend().setEnabled(false);
            pie.animateY(1500);

            TextView lastRef= (TextView)rootViewSummary.findViewById(R.id.lastRefreshed);
            lastRef.setTypeface(vClass.nunito_bold);
            String lastSyncedJson = get(context,lastRefreshed,"");
            try {
                Calendar lastSynced = new Gson().fromJson(lastSyncedJson, new TypeToken<Calendar>() {
                }.getType());
                lastRef.setText("Last Synced:\n" + getDateTimeString(lastSynced));
            }
            catch (Exception e){
                lastRef.setText("Last Synced:\n" +lastSyncedJson);
            }

            RelativeLayout logoutButt = (RelativeLayout)rootViewSummary.findViewById(R.id.rl_logout);
            TextView logoutText = (TextView)rootViewSummary.findViewById(R.id.tv_logout);
            logoutText.setTextColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
            logoutText.setTypeface(vClass.nunito_reg);
            ImageView iv_logout =(ImageView)logoutButt.findViewById(R.id.iv_logout);
            iv_logout.setImageDrawable(getResources().getDrawable(ThemeProperty.drawableResources[3]));
            logoutButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    workSpace.confirmLogout(context,getActivity());
                }
            });

            RelativeLayout aboutButt = (RelativeLayout)rootViewSummary.findViewById(R.id.rl_about);
            TextView aboutText = (TextView)rootViewSummary.findViewById(R.id.tv_about);
            aboutText.setTextColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
            aboutText.setTypeface(vClass.nunito_reg);
            ImageView iv_about =(ImageView)aboutButt.findViewById(R.id.iv_about);
            iv_about.setImageDrawable(getResources().getDrawable(ThemeProperty.drawableResources[2]));
            aboutButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context,About.class));
                }
            });

            RelativeLayout settingButt = (RelativeLayout)rootViewSummary.findViewById(R.id.rl_setting);
            TextView settingText = (TextView)rootViewSummary.findViewById(R.id.tv_setting);
            settingText.setTextColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
            settingText.setTypeface(vClass.nunito_reg);
            ImageView iv_setting =(ImageView)settingButt.findViewById(R.id.iv_setting);
            iv_setting.setImageDrawable(getResources().getDrawable(ThemeProperty.drawableResources[1]));
            settingButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context,settings.class));
                }
            });

            RelativeLayout shareButt = (RelativeLayout)rootViewSummary.findViewById(R.id.rl_share);
            TextView shareText = (TextView)rootViewSummary.findViewById(R.id.tv_share);
            shareText.setTextColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
            shareText.setTypeface(vClass.nunito_reg);
            ImageView iv_share =(ImageView)shareButt.findViewById(R.id.iv_share);
            iv_share.setImageDrawable(getResources().getDrawable(ThemeProperty.drawableResources[0]));
            shareButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.fourthstatelabs.zchedule&hl=en");
                    try {
                        context.startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context, "Whatsapp is not installed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return rootViewSummary;
        }
        public void schedule_todo_notification(Notification_Holder n) {
            if (n.cal.getTimeInMillis() > System.currentTimeMillis()) {
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getActivity(), NotifyService.class);
                Gson js = new Gson();
                String f = js.toJson(n);
                intent.putExtra("notificationContent", f);
                intent.putExtra("fromClass","WorkSpace");
                id++;
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id, intent, 0);
                put(context,notificationIdentifier,id);//editor.putInt("identifier", id);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, n.cal.getTimeInMillis(), pendingIntent);
            }
        }
        Calendar c;
        void showReminderSetter(final Switch reminderSwitch){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            View dateTimeView = getActivity().getLayoutInflater().inflate(R.layout.floatingview_set_datetime,null);
            alertBuilder.setView(dateTimeView);
            final TimePicker time = (TimePicker) dateTimeView.findViewById(R.id.timePicker);
            final DatePicker date = (DatePicker) dateTimeView.findViewById(R.id.datePicker);
            Button ok = (Button)dateTimeView.findViewById(R.id.datetime_ok);
            final AlertDialog alert = alertBuilder.create();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    c=Calendar.getInstance();
                    c.set(date.getYear(), date.getMonth(), date.getDayOfMonth(), time.getCurrentHour(), time.getCurrentMinute());
                    alert.cancel();
                }
            });
            (dateTimeView.findViewById(R.id.datetime_cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.cancel();
                }
            });
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if(c==null){
                        reminderSwitch.setChecked(false);
                    }else{
                        reminderSwitch.setText(getDateTimeString(c));

                    }
                }
            });
            alert.show();
        }

        void showCabinAlertDialog(final listAdapter_teachers cabinListAdapter) {
            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            final View alertCabinView = getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_cabin, null);
            alertBuilder.setView(alertCabinView);
            final AlertDialog alert = alertBuilder.create();
            Toolbar addCabinToolbar =(Toolbar)alertCabinView.findViewById(R.id.alert_cabin_toolbar);
            addCabinToolbar.inflateMenu(R.menu.menu_add_todo);
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    workSpace.hideSoftKeyboard(getActivity());
                }
            });
            addCabinToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if(id==R.id.action_add_todo){
                        String name = ((TextView) alertCabinView.findViewById(R.id.alert_cabin_teacherName)).getText().toString();
                        String cabin = ((TextView) alertCabinView.findViewById(R.id.alert_cabin_cabinAddress)).getText().toString();
                        if (name.trim().equals("") || cabin.trim().equals("")) {
                            Toast.makeText(context, "Invalid Data !", Toast.LENGTH_LONG).show();
                        }
                        else {
                            for (int i = 0; i < vClass.cablist.size(); i++) {
                                if (vClass.cablist.get(i).name.toLowerCase().equals(name.toLowerCase())) {
                                    vClass.cablist.get(i).cabin = cabin;
                                    writeCabListToPrefs();
                                    cabinListAdapter.updatecontent(vClass.cablist);
                                    alert.cancel();
                                    return true;
                                }
                            }
                            Cabin_Details c = new Cabin_Details();
                            c.name = name;
                            c.cabin = cabin;
                            vClass.cablist.add(c);
                            vClass.toBeUpdated.add(c);
                            listAdapter_searchTeacher.writeEditedToPrefs(context);
                            writeCabListToPrefs();
                            cabinListAdapter.updatecontent(vClass.cablist);
                            alert.cancel();
                            return true;
                        }
                    }
                    return false;
                }
            });
            alert.show();
        }  //CREATE AND HANDLES THE ALERT DIALOG BOX TO ADD CABIN

        void setSearcher() {
            searchResult = new ArrayList<>();
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(teacherSearch.getWindowToken(), 0);
            teacherSearch.setTypeface(vClass.nunito_reg);
            final listAdapter_searchTeacher searchAdapter = new listAdapter_searchTeacher(context, searchResult,teacherSearch);
            resultList.setAdapter(searchAdapter);
            teacherSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    searchResult.clear();
                    String search = teacherSearch.getText().toString();
                    if (!(search.length() < 2)) {
                        resultList.setVisibility(View.VISIBLE);
                        for (int i = 0; i < vClass.teachers.size(); i++) {
                            teacher var = vClass.teachers.get(i);
                            if (var.name.toLowerCase().contains(search.toLowerCase())) {
                                searchResult.add(var);
                            }
                        }
                    } else {
                        resultList.setVisibility(View.INVISIBLE);
                    }
                    if(searchResult.size()==0)
                        resultList.setVisibility(View.INVISIBLE);
                    searchAdapter.update(searchResult);
                }
            });
        }

        LinearLayout taskGridLeft, taskGridRight;

        void applyGrid(final int i){
            if(i>=vClass.notes.size()) return;
            else{
                int leftHeight=taskGridLeft.getMeasuredHeight();
                int rightHeight =taskGridRight.getMeasuredHeight();
                View viewToAdd=getTaskView(i);
                setTaskOnClick(viewToAdd,i);
                if(leftHeight<=rightHeight){
                    taskGridLeft.addView(viewToAdd);
                }
                else{
                    taskGridRight.addView(viewToAdd);
                }
                viewToAdd.post(new Runnable() {
                    @Override
                    public void run() {
                        applyGrid(i+1);
                    }
                });
            }
        }

        void populateTaskGrid() {
            taskGridLeft.removeAllViews();
            taskGridRight.removeAllViews();
            if(vClass.notes.size()>0) {
                noNotesText.setVisibility(View.GONE);
                View viewToAdd = getTaskView(0);
                setTaskOnClick(viewToAdd, 0);
                taskGridLeft.addView(viewToAdd);
                viewToAdd.post(new Runnable() {
                    @Override
                    public void run() {
                        applyGrid(1);
                    }
                });
            }
            else{
                noNotesText.setVisibility(View.VISIBLE);
            }
        }
        AlertDialog expanded;
        void setTaskOnClick(View view,final int index){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(getExpanded(index));
                    expanded= alertDialogBuilder.create();
                    expanded.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    expanded.show();
                    expanded.getWindow().setLayout(((int)(vClass.width*0.8)),RelativeLayout.LayoutParams.WRAP_CONTENT);
                }
            });
        }

        int[] colors = new int[]{R.color.dot_light_screen1,R.color.dot_light_screen5, R.color.dot_light_screen2,R.color.dot_light_screen3,R.color.dot_light_screen4};

        View getTaskView(final int index) {
            final Notification_Holder cTask = vClass.notes.get(index);
            final View taskView = getActivity().getLayoutInflater().inflate(R.layout.course_task_card_view, null);
            TextView title =((TextView) taskView.findViewById(R.id.task_title));
            ImageButton edit=(ImageButton) taskView.findViewById(R.id.task_edit);
            title.setTypeface(vClass.nunito_bold);
            title.setText(cTask.title);
            TextView deadLineTextView= (TextView) taskView.findViewById(R.id.task_deadLine);
            deadLineTextView.setTypeface(vClass.nunito_reg);
            TextView desc=((TextView) taskView.findViewById(R.id.task_desc));
            desc.setTypeface(vClass.nunito_reg);
            desc.setText(cTask.content);
            (taskView.findViewById(R.id.task_delete)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delTask(cTask);
                    populateTaskGrid();
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Notification_Holder n=vClass.notes.get(index);
                    final AlertDialog alert;
                    View root = getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_todo, null);
                    final EditText title = (EditText) root.findViewById(R.id.title);
                    final EditText other = (EditText) root.findViewById(R.id.note);
                    final Switch reminderSwitch =(Switch)root.findViewById(R.id.add_todo_reminder_switch);
                    Toolbar addTaskToolbar=((Toolbar)root.findViewById(R.id.add_task_toolbar));
                    addTaskToolbar.inflateMenu(R.menu.menu_add_todo);
                    title.setText(n.title);
                    other.setText(n.content);


                    AlertDialog.Builder bui = new AlertDialog.Builder(context);
                    bui.setView(root);
                    alert = bui.create();
                    alert.show();

                    reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if(checked) {
                                c = null;
                                showReminderSetter(reminderSwitch);
                            }
                            else {
                                c = null;
                                reminderSwitch.setText("Set Reminder");
                            }
                        }
                    });
                    addTaskToolbar.setTitle("Edit Note");
                    addTaskToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if(id==R.id.action_add_todo) {
                                if (title.getText().toString() != null && title.getText().toString().equals("") != true & other.getText().toString() != null && other.getText().toString().equals("") != true) {
                                    Notification_Holder n;
                                    if(c!=null) {
                                        n = new Notification_Holder(c, title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                        schedule_todo_notification(n);
                                        c=null;
                                    }
                                    else
                                        n = new Notification_Holder(Calendar.getInstance(), title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                    updateTask(n,index);
                                    populateTaskGrid();
                                    alert.cancel();
                                    workSpace.hideSoftKeyboard(getActivity());
                                } else
                                    Toast.makeText(getContext(), "Both title and note must contain some text", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            return false;
                        }
                    });
                }
            });
            Calendar deadLine = cTask.cal;
            deadLineTextView.setText(getDateTimeString(deadLine));
            //taskView.setBackground(getResources().getDrawable(R.drawable.soft_corner_taskview));
            GradientDrawable softShape = (GradientDrawable) taskView.getBackground();
            final int colorIndex = index % (colors.length);
            softShape.setColor(getResources().getColor(colors[colorIndex]));
            return taskView;
        }

        View getExpanded(final int index){
            final Notification_Holder cTask = vClass.notes.get(index);
            final View taskView = getActivity().getLayoutInflater().inflate(R.layout.expanded_task_card_view, null);
            TextView title =((TextView) taskView.findViewById(R.id.task_title));
            ImageButton edit=(ImageButton) taskView.findViewById(R.id.task_edit);
            title.setTypeface(vClass.nunito_bold);
            title.setText(cTask.title);
            TextView deadLineTextView= (TextView) taskView.findViewById(R.id.task_deadLine);
            deadLineTextView.setTypeface(vClass.nunito_reg);
            TextView desc=((TextView) taskView.findViewById(R.id.task_desc));
            desc.setTypeface(vClass.nunito_reg);
            desc.setText(cTask.content);
            (taskView.findViewById(R.id.task_delete)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{expanded.cancel();}
                    catch (Exception e){}
                    delTask(cTask);
                    populateTaskGrid();
                    }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Notification_Holder n=vClass.notes.get(index);
                    final AlertDialog alert;
                    View root = getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_todo, null);
                    final EditText title = (EditText) root.findViewById(R.id.title);
                    final EditText other = (EditText) root.findViewById(R.id.note);
                    final Switch reminderSwitch =(Switch)root.findViewById(R.id.add_todo_reminder_switch);
                    Toolbar addTaskToolbar=((Toolbar)root.findViewById(R.id.add_task_toolbar));
                    addTaskToolbar.inflateMenu(R.menu.menu_add_todo);
                    title.setText(n.title);
                    other.setText(n.content);


                    AlertDialog.Builder bui = new AlertDialog.Builder(context);
                    bui.setView(root);
                    alert = bui.create();
                    alert.show();

                    reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if(checked) {
                                c = null;
                                showReminderSetter(reminderSwitch);
                            }
                            else {
                                c = null;
                                reminderSwitch.setText("Set Reminder");
                            }
                        }
                    });
                    addTaskToolbar.setTitle("Edit Note");
                    addTaskToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if(id==R.id.action_add_todo) {
                                if (title.getText().toString() != null && title.getText().toString().equals("") != true & other.getText().toString() != null && other.getText().toString().equals("") != true) {
                                    Notification_Holder n;
                                    if(c!=null) {
                                        n = new Notification_Holder(c, title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                        schedule_todo_notification(n);
                                    }
                                    else
                                        n = new Notification_Holder(Calendar.getInstance(), title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                    updateTask(n,index);
                                    Gson json = new Gson();
                                    String temporary = json.toJson(vClass.notes);
                                    put(context,todolist,temporary);//editor.putString("todolist", temporary);
                                    int indexOfView=index/2;
                                    if (index % 2 != 0) {
                                        View view = getTaskView(index);
                                        setTaskOnClick(view,index);
                                        taskGridRight.removeViewAt(indexOfView);
                                        taskGridRight.addView(view,indexOfView);
                                    } else {
                                        View view = getTaskView(index);
                                        setTaskOnClick(view,index);
                                        taskGridLeft.removeViewAt(indexOfView);
                                        taskGridLeft.addView(view,indexOfView);
                                    }
                                    expanded.cancel();
                                    workSpace.hideSoftKeyboard(getActivity());
                                    alert.cancel();
                                } else
                                    Toast.makeText(getContext(), "Both title and note must contain some text", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            return false;
                        }
                    });
                }
            });
            Calendar deadLine = cTask.cal;
            deadLineTextView.setText(getDateTimeString(deadLine));
            //taskView.setBackground(getResources().getDrawable(R.drawable.soft_corner_taskview));
            GradientDrawable softShape = (GradientDrawable) taskView.getBackground();
            final int colorIndex = index % (colors.length);
            softShape.setColor(getResources().getColor(colors[colorIndex]));
            return taskView;
        }


        void delTask(Notification_Holder task) {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), NotifyService.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), task.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
            vClass.notes.remove(task);
            put(context,todolist,Notification_Holder.convert_to_jason(vClass.notes));//editor.putString("todolist", Notification_Holder.convert_to_jason(vClass.notes));
        }

        void updateTask(Notification_Holder newtask, int index){
            Notification_Holder oldTask= vClass.notes.get(index);
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), NotifyService.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), oldTask.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
            vClass.notes.set(index,newtask);
            put(context,todolist,Notification_Holder.convert_to_jason(vClass.notes));//editor.putString("todolist", Notification_Holder.convert_to_jason(vClass.notes));
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Courses";
                case 1:
                    return "Faculty";
                case 2:
                    return "Tasks";
                case 3:
                    return "Summary";
            }
            return null;
        }



    }

    public static void writeCabListToPrefs() {
        Gson json=new Gson();
        String cabListJson=json.toJson(vClass.cablist);
        put(context,customTeachers,cabListJson);//cabListEditor.putString("customTeachers",cabListJson);
    } //SAVE THE CONTENT OF CABLIST TO THE PREFERENCES

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e){ /*KEYBOARD HIDE FAILED*/ }
    }  //HIDES THE KEYBOARD

    public static void setOnTouchListener(View view, final Activity activity) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    if (resultList.getVisibility()==View.VISIBLE){
                        resultList.setVisibility(View.INVISIBLE);
                    }
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setOnTouchListener(innerView,activity);
            }
        }
    }  //SET THE TOUCH TO HIDE THE KEYBOARD

    public static void setOnViewTouchListener(View view,final Activity activity){
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(activity);
                if (resultList.getVisibility()==View.VISIBLE){
                    resultList.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
    }

    void getDimensions(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        vClass.width=dm.widthPixels;
        vClass.height=dm.heightPixels;
    }  //GET THE DIMENSIONS OF THE DEVICE

    public static String getDateTimeString(Calendar deadLine){
        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int taskDay= deadLine.get(Calendar.DAY_OF_YEAR);
        String dateString;
        String timeString = getHour(deadLine.get(Calendar.HOUR))+":"+getMinute(deadLine.get(Calendar.MINUTE))+getAMPM(deadLine.get(Calendar.AM_PM));
        if(today==taskDay){
            dateString= "Today";
        }
        else if(today==taskDay-1){
            dateString="Tomorrow";
        }
        else if(today==taskDay+1){
            dateString="Yesterday";
        }
        else{
            dateString=deadLine.get(Calendar.DATE) + "/" + (deadLine.get(Calendar.MONTH) + 1) + "/" + deadLine.get(Calendar.YEAR);
        }

        return (dateString+" "+timeString);
    }

    static String getAMPM(int AMPM){
        if(AMPM==0)
            return "AM";
        else
            return "PM";
    }

    static String getMinute(int minute){
        if(minute<10){
            return ("0"+minute);
        }
        return String.valueOf(minute);
    }

    static String getHour(int hour){
        if(hour==0) return "12";
        else return String.valueOf(hour);
    }
}