package com.example.sridh.vdiary;
import android.content.Context;

import android.graphics.Color;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;




import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class showSubject extends AppCompatActivity {
    subject clicked;
    Context context;
    int att, noofdays;
    NumberPicker leave;
    TextView mon, tue, wed, thu, fri, newAtt,classRatio;
    Typeface nunito_bold, nunito_reg;
    HorizontalScrollView hsv;

    static LinearLayout attTracker;
    static List<subjectDay> attTrack;
    ArrayList<Integer> occurrence;

    double class_att,tempClassAtt,tempTotalClass;


    int toMul=1; // 1 if THEORY ELSE 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_subject);
        int position = getIntent().getIntExtra("position", 0);
        clicked = subject.getNewInstance(vClass.subList.get(position));
        show(clicked); //Initialize the popup activity to show the contents of the subject
        if (clicked.type.equals("ELA") || clicked.type.equals("LO")) toMul=2;
        new Async_search(position).execute();
        vClass.setStatusBar(getWindow(),getApplicationContext(),R.color.colorPrimaryDark);
    }

    void show(subject sub) {
        class_att = clicked.classAttended;
        attTrack = clicked.attTrack;
        tempClassAtt=class_att;
        tempTotalClass=clicked.ctd;
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        nunito_reg = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        nunito_bold = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Bold.ttf");
        TextView Title = ((TextView) findViewById(R.id.subject_Title));
        Title.setTypeface(nunito_bold);
        Title.setText(sub.title);
        TextView teacher = ((TextView) findViewById(R.id.subject_Teacher));
        teacher.setTypeface(nunito_reg);
        teacher.setText(sub.teacher);
        TextView slot= (TextView)findViewById(R.id.subject_slot);
        slot.setTypeface(nunito_reg);
        slot.setText(sub.slot);
        TextView lastUpdated= (TextView)findViewById(R.id.tv_lastUpdated);
        workSpace.currentShowSubjectTextView=lastUpdated;
        lastUpdated.setTypeface(nunito_reg);
        if(!clicked.lastUpdated.equals("")) lastUpdated.setText("Last Uploaded: "+clicked.lastUpdated);
        else lastUpdated.setText("Last Uploaded: Fetching");
        classRatio= (TextView)findViewById(R.id.classRatio);
        classRatio.setTypeface(nunito_reg);
        classRatio.setText(clicked.classAttended+"/"+clicked.ctd);
        String attString = clicked.attString;
        att = Integer.parseInt(attString.substring(0, attString.length() - 1));
        noofdays = clicked.ctd;

        newAtt = (TextView) findViewById(R.id.tv_newAtt);
        newAtt.setText(attString);
        newAtt.setTypeface(nunito_reg);

        leave = (NumberPicker) findViewById(R.id.leave_picker);
        final String[] intArray = numArray();
        leave.setMaxValue(100);
        leave.setMinValue(0);
        leave.setValue(50);
        leave.setWrapSelectorWheel(false);
        leave.setDisplayedValues(intArray);
        final numberPickerValueChangeHandler leaveScrollHandler = new numberPickerValueChangeHandler();
        leave.setOnValueChangedListener(leaveScrollHandler);
        leave.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        Button jump=(Button)findViewById(R.id.jumpTo);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPicked=0;
                leave.setValue(50);
                class_att=tempClassAtt;
                noofdays=(int)tempTotalClass;
            }
        });
        jump.setTypeface(vClass.nunito_reg);

        attTracker = (LinearLayout)findViewById(R.id.attTrackView);
        getAttendanceTracker(getApplicationContext());
        hsv = (HorizontalScrollView)findViewById(R.id.hsv_show_subject);
        hsvScrollFull();


    }  //SETS THE LAYOUT OF THE SUBJECT TO BE SHOWN

    class Async_search extends AsyncTask<Void, Void, Void> {
        int position;

        Async_search(int x) {
            position = x;
            occurrence = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            subject sub = vClass.subList.get(position);
            for (int i = 0; i < vClass.timeTable.size(); i++) {
                List<subject> z = vClass.timeTable.get(i);
                for (int j = 0; j < z.size(); j++) {
                    if (z.get(j).code.equals(sub.code) && z.get(j).type.equals(sub.type)) {
                        occurrence.add(i);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //SET THE TEXTVIEWS OF OCURENCE FOUND
            mon = (TextView) findViewById(R.id.tv_mon);
            tue = (TextView) findViewById(R.id.tv_tue);
            wed = (TextView) findViewById(R.id.tv_wed);
            thu = (TextView) findViewById(R.id.tv_thu);
            fri = (TextView) findViewById(R.id.tv_fri);

            mon.setTypeface(nunito_reg);
            tue.setTypeface(nunito_reg);
            wed.setTypeface(nunito_reg);
            thu.setTypeface(nunito_reg);
            fri.setTypeface(nunito_reg);
            TextView occurred = null;
            for (int i : occurrence) {
                switch (i) {
                    case 0:
                        occurred = mon;
                        break;
                    case 1:
                        occurred = tue;
                        break;
                    case 2:
                        occurred = wed;
                        break;
                    case 3:
                        occurred = thu;
                        break;
                    case 4:
                        occurred = fri;
                        break;
                }
                if (occurred != null) {
                    occurred.setTextColor(getResources().getColor(R.color.colorWhite));
                    occurred.setBackground(getResources().getDrawable(R.drawable.border_class_occurence));
                }
            }
            super.onPostExecute(aVoid);
        }
    }  // SEARCHES THE TIMETABLE FOR THE OCCURENCE OF THE SUBJECT IN A WEEK
    int lastPicked=0;
    class numberPickerValueChangeHandler implements NumberPicker.OnValueChangeListener{
        @Override
        public void onValueChange(NumberPicker numberPicker, int old, int next) {
            double newAttendance = 0.0;
            int leave = next;
            leave = leave - 50;
            boolean nextIsPresent = true;
            if (leave < 0) {
                //GOING TOWARDS POSITIVE OF THE PICKER
                leave = leave * -1;
                nextIsPresent = true;
                double classAttended = class_att + toMul * leave;
                double totalClasses = noofdays + toMul * leave;
                double att_final2 = (classAttended / totalClasses) * 100.0;
                classRatio.setText((int) (classAttended) + "/" + (int) (totalClasses));
                tempClassAtt = classAttended;
                tempTotalClass = totalClasses;
                newAttendance = (Math.ceil(att_final2));
            } else if ((noofdays + leave) >= 0) {
                //GOING TOWARDS NEGATIVE OF THE PICKER
                nextIsPresent = false;
                double totalClasses = (noofdays + toMul * leave);
                double att_final = (class_att / totalClasses) * 100.0;
                classRatio.setText((int) (class_att) + "/" + (int) (totalClasses));
                tempClassAtt = class_att;
                tempTotalClass = totalClasses;
                newAttendance = (Math.ceil(att_final));
            }
            try {
                if (leave > lastPicked) {
                    searchNextAndAdd(nextIsPresent, getApplicationContext());
                } else {

                    attTrack.remove(attTrack.size() - 1);
                    attTracker.removeViewAt(attTracker.getChildCount() - 1);
                    try {
                        if (attTrack.size() == holidayIndexes.get(holidayIndexes.size() - 1)) {
                            attTrack.remove(attTrack.size() - 1);
                            attTracker.removeViewAt(attTracker.getChildCount() - 1);
                            holidayIndexes.remove(holidayIndexes.size() - 1);
                        }
                    } catch (Exception e) {
                        //HOLIDAY INDEXES IS EMPTY
                    }
                }
            }
        catch (Exception e){
            //NO DATA ABOUT ATTENDANCE
        }

            lastPicked=leave;
            newAtt.setText(newAttendance + "%");
        }
    }  //HANDLES THE ON VALUE CHANGE ON THE NUMBER PICKER TO CHANGE ATTENDENCE

    String[] numArray(){
        String[] array= new String[101];
        for(int i=50;i>=-50;i--){
            array[50-i]=Integer.toString(i);
        }
        return array;
    }  //RETURN THE ARRAY OF NUMBERS TO SHOWN IN THE NUMBER PICKER

    static void getAttendanceTracker(Context context){
        for(int i=0;i<attTrack.size();i++){
            View point = getPresentState(attTrack.get(i),context,null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            attTracker.addView(point,params);
        }
        return;
    }

    static LinearLayout getPresentState(subjectDay day,Context context,String occasion){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        FrameLayout x = new FrameLayout(context);
        ImageView fb = new ImageView(context);
        TextView tv = new TextView(context);
        if(!day.isPresent){
            tv.setTextColor(Color.WHITE);
            fb.setBackground(context.getResources().getDrawable(R.drawable.circle_absent));
            tv.setText("A");
        }
        else{
            tv.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            fb.setBackground(context.getResources().getDrawable(R.drawable.circle_present));
            tv.setText("P");
        }
        x.addView(fb);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(vClass.nunito_reg);
        x.addView(tv);
        RelativeLayout rootPoint= new RelativeLayout(context);
        rootPoint.addView(x,new RelativeLayout.LayoutParams(60,60));
        rootPoint.setGravity(Gravity.CENTER);
        RelativeLayout rootDate = new RelativeLayout(context);
        TextView dateView = new TextView(context);
        dateView.setTypeface(vClass.nunito_reg);
        String[] dateDiv = day.date.split("-");

        int dayIndex = Integer.parseInt(dateDiv[0]);
        int month =getMonth(dateDiv[1]);
        int year = Integer.parseInt(dateDiv[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,dayIndex);
        dateView.setText(getDay(calendar.get(Calendar.DAY_OF_WEEK))+" "+dateDiv[0]+" "+dateDiv[1]);
        dateView.setTextSize(10);
        dateView.setTextColor(Color.WHITE);
        rootDate.setGravity(Gravity.CENTER_HORIZONTAL);
        rootDate.addView(dateView);
        LinearLayout totalRoot = new LinearLayout(context);
        totalRoot.setOrientation(LinearLayout.VERTICAL);
        totalRoot.addView(rootDate,params);
        totalRoot.addView(rootPoint,params);
        if(occasion!=null){
            RelativeLayout rootOccasion = new RelativeLayout(context);
            TextView occasionView = new TextView(context);
            occasionView.setTextSize(10);
            occasionView.setTextColor(Color.WHITE);
            occasionView.setTypeface(vClass.nunito_reg);
            occasionView.setText(occasion);
            rootOccasion.setGravity(Gravity.CENTER_HORIZONTAL);
            rootOccasion.addView(occasionView);
            totalRoot.addView(rootOccasion,params);
            tv.setText("H");
        }
        return totalRoot;
    }
    ArrayList<Integer> holidayIndexes = new ArrayList<>();
    void searchNextAndAdd(boolean isPresent,Context context){
            String[] date = attTrack.get(attTrack.size() - 1).date.split("-");
            int day = Integer.parseInt(date[0]);
            int month = getMonth(date[1]);
            int year = Integer.parseInt(date[2]);
            Calendar currentDate = Calendar.getInstance();
            currentDate.set(year, month, day);
            while (true) {
                currentDate.add(Calendar.DATE, 1);
                int currentDay = currentDate.get(Calendar.DAY_OF_WEEK) - 2;
                if (occurrence.contains(currentDay)) {
                    subjectDay newSubjectDay = new subjectDay();
                    newSubjectDay.isPresent = isPresent;
                    newSubjectDay.date = (currentDate.get(Calendar.DAY_OF_MONTH)) + "-" + months[currentDate.get(Calendar.MONTH)] + "-" + currentDate.get(Calendar.YEAR);
                    String occasion = getHoliday(currentDate);
                    View point = getPresentState(newSubjectDay, context, occasion);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    attTracker.addView(point, params);
                    attTrack.add(newSubjectDay);
                    hsvScrollFull();
                    if (occasion == null) break;
                    else holidayIndexes.add(attTrack.size());
                }
            }
    }
    String[] months=new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    static int getMonth(String Month){
        switch (Month){
            case "Jan":
                return 0;
            case "Feb":
                return 1;
            case "Mar":
                return 2;
            case "Apr":
                return 3;
            case "May":
                return 4;
            case "Jun":
                return 5;
            case "Jul":
                return 6;
            case "Aug":
                return 7;
            case "Sep":
                return 8;
            case "Oct":
                return 9;
            case "Nov":
                return 10;
            case "Dec":
                return 11;
        }
        return 0;
    }

    void hsvScrollFull(){
        hsv.post(new Runnable() {
            @Override
            public void run() {
                hsv.fullScroll(hsv.FOCUS_RIGHT);
            }
        });
    }

    static String getDay(int day){
        switch (day){
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
        }
        return "";
    }

    static String getHoliday(Calendar calendar){
            List<holiday> holidays= vClass.holidays;
            for (holiday h :holidays){
                Calendar dateString =h.date;
                int day = dateString.get(Calendar.DAY_OF_MONTH);
                int month =dateString.get(Calendar.MONTH);
                int year =dateString.get(Calendar.YEAR);
                if(calendar.get(Calendar.DAY_OF_MONTH)==day && calendar.get(Calendar.MONTH)==month && calendar.get(Calendar.YEAR)==year){
                    return h.ocassion;
                }
            }
        return null;
    }
}
