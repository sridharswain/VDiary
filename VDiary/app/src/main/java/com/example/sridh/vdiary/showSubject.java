package com.example.sridh.vdiary;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class showSubject extends AppCompatActivity {
    subject clicked;
    int width=vClass.width;
    int height=vClass.height;
    Context context;
    int att,noofdays;
    NumberPicker leave;
    String array[]=new String[101];

    TextView mon,tue,wed,thu,fri,newAtt;
    public static ListAdapter todoList;
    Typeface nunito_bold,nunito_reg;
    int c=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_subject);
        context =this;
        for(int i=50;i>=-50;i--)
        {
            array[c++]=Integer.toString(i);
        }
        c=0;
        int position=getIntent().getIntExtra("position",0);
        clicked= vClass.subList.get(position);
        leave=(NumberPicker)findViewById(R.id.leave_picker);
        leave.setDisplayedValues(array);

        leave.setMaxValue(100);
        leave.setMinValue(0);
        leave.setValue(50);

        new Async_search(position).execute();
        show(clicked); //Initialize the popup activity to show the contents of the subject
    }
    void show(subject sub){
        nunito_reg=Typeface.createFromAsset(context.getAssets(),"fonts/Nunito-Regular.ttf");
        nunito_bold=Typeface.createFromAsset(context.getAssets(),"fonts/Nunito-Bold.ttf");
        getWindow().setLayout(width,((int)(0.68*height)));
        TextView Title = ((TextView)findViewById(R.id.subject_Title));
        Title.setTypeface(nunito_bold);
        Title.setText(sub.title);
        TextView teacher = ((TextView)findViewById(R.id.subject_Teacher));
        teacher.setTypeface(nunito_reg);
        teacher.setText(sub.teacher);
        newAtt=(TextView)findViewById(R.id.tv_newAtt);
        String attString = clicked.attString;
        att = Integer.parseInt(attString.substring(0,attString.length()-1));
        noofdays=clicked.ctd;
        newAtt.setText(attString);
        newAtt.setTypeface(nunito_reg);
            leave.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int old, int next) {
                    double newAttendance=0.0;
                    int leave=next;
                    leave=leave-50;
                    double class_att = att * noofdays / 100.0;
                    if(leave<0)
                    {
                        leave=leave*-1;
                        double att_final2=(class_att+leave)/(noofdays+leave)*100.0;
                        newAttendance=((int) (att_final2));
                    }
                    else if((noofdays+leave)>=0) {
                        class_att = att * noofdays / 100.0;
                        double att_final = (class_att / (noofdays + leave)) * 100.0;
                        newAttendance = ((int) (att_final));
                    }
                    newAtt.setText(newAttendance+"%");
                }
            });
    }



    class Async_search extends AsyncTask<Void,Void,Void>
    {
        int position;
        ArrayList<Integer> occurrence;
        Async_search(int x)
        {
            position=x;
            occurrence = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            subject sub=vClass.subList.get(position);
            for(int i=0;i<vClass.timeTable.size();i++)
            {
                List<subject> z=vClass.timeTable.get(i);
                for(int j=0;j<z.size();j++)
                {
                    if(z.get(j).title.equals(sub.title) && z.get(j).type.equals(sub.type))
                    {
                        occurrence.add(i);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //SET THE TEXTVIEWS OF OCUURENCE FOUND
            mon=(TextView)findViewById(R.id.tv_mon);
            tue=(TextView)findViewById(R.id.tv_tue);
            wed=(TextView)findViewById(R.id.tv_wed);
            thu=(TextView)findViewById(R.id.tv_thu);
            fri=(TextView)findViewById(R.id.tv_fri);

            mon.setTypeface(nunito_reg);
            tue.setTypeface(nunito_reg);
            wed.setTypeface(nunito_reg);
            thu.setTypeface(nunito_reg);
            fri.setTypeface(nunito_reg);
            TextView occurred=null;
            for (int i: occurrence){
                switch (i){
                    case 0:
                        occurred=mon;
                        break;
                    case 1:
                        occurred=tue;
                        break;
                    case 2:
                        occurred=wed;
                        break;
                    case 3:
                        occurred=thu;
                        break;
                    case 4:
                        occurred=fri;
                        break;
                }
                if(occurred!=null) {
                    occurred.setTextColor(getResources().getColor(R.color.colorWhite));
                    occurred.setBackground(getResources().getDrawable(R.drawable.border_class_occurence));
                }
            }
            super.onPostExecute(aVoid);
        }
    }
}
