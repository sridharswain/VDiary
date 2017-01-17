package com.example.sridh.vdiary;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class showSubject extends AppCompatActivity {
    subject clicked;
    int width=vClass.width;
    int height=vClass.height;
    static TextView test;
    static Context con;
    static RadioButton mon,tue,wed,thur,fri;
    public static ListAdapter todoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_subject);
        con=this;
        test=(TextView)findViewById(R.id.zzzz);
        mon=(RadioButton)findViewById(R.id.monday);
        tue=(RadioButton)findViewById(R.id.tuesday);
        wed=(RadioButton)findViewById(R.id.wednesday);
        thur=(RadioButton)findViewById(R.id.thursday);
        fri=(RadioButton)findViewById(R.id.friday);


        int position=getIntent().getIntExtra("position",0);
        clicked= vClass.subList.get(position);Async_search a=new Async_search(position);
        a.execute();
        show(clicked);  //Initialize the popup activity to show the contents of the subject
    }
    void show(subject sub){
        getWindow().setLayout(width,((int)(0.6*height)));
        ((TextView)findViewById(R.id.subject_Title)).setText(sub.title);
        ((TextView)findViewById(R.id.subject_Teacher)).setText(sub.teacher);

    }

    static void extract_det(String z)
    {
        List<String> f=new ArrayList<String>();
        int c=0;
        String t="";
        for(int i=0;i<z.length();i++)
        {
            if(z.charAt(i)==';')
            {
                f.add(c++,t);
                t="";
            }
            else
                t=t+z.charAt(i);
        }

        setradiobuttons(f);

    }

    static void setradiobuttons(List<String> b)
    {
        for(int i=0;i<b.size();i++)
        {
            String x=b.get(i);
            if(x.equals("Monday"))
                mon.setChecked(true);
            else if(x.equals("Tuesday"))
                tue.setChecked(true);
            else if(x.equals("Wednesday"))
                wed.setChecked(true);
            else if(x.equals("Thursday"))
                thur.setChecked(true);
            else if(x.equals("Friday"))
                fri.setChecked(true);
        }
    }
}

class Async_search extends AsyncTask<Void,Void,Void>
{
    int position;
    String x="";

    Async_search(int x)
    {
        position=x;
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
                    switch(i)
                    {
                        case 0:
                            x=x+"Monday"+";";
                            break;
                        case 1:
                            x=x+"Tuesday"+";";
                            break;
                        case 2:
                            x=x+"Wednesday"+";";
                            break;
                        case 3:
                            x=x+"Thursday;";
                            break;
                        case 4:
                            x=x+"Friday;";
                            break;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        showSubject.extract_det(x);
        super.onPostExecute(aVoid);
    }
}
