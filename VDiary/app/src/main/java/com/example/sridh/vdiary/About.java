package com.example.sridh.vdiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        vClass.setStatusBar(getWindow(),getApplicationContext());
        ((TextView)findViewById(R.id.about_core_developers)).setText("Core developers:\n        Sparsha Saha\n        Sridhar Swain");
        ((TextView)findViewById(R.id.about_name_and_version)).setText("V Diary Beta "+vClass.VERSION);
        String thanks="";
        /*thanks = "Special Thanks to:\n";
        thanks= thanks+ "Prasang Sharma, Aman Hussain\n";
        thanks= thanks+ "\n\nAlpha Testers:\n";
        thanks= thanks+ "Gaurav, Subhojeet, Dipankar, Hemant, Mohit, Abhishek, Madhurima, Akanksha, Amrit";*/
        ((TextView)findViewById(R.id.thanks_view)).setText(thanks);
    }
}