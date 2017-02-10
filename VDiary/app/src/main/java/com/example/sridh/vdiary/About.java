package com.example.sridh.vdiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        vClass.setStatusBar(getWindow(),getApplicationContext(),R.color.colorPrimaryDark);
        ((TextView)findViewById(R.id.about_core_developers)).setText("Developed By : Sparsha, Sridhar");
        ((TextView)findViewById(R.id.about_name_and_version)).setText("Zchedule Beta "+vClass.VERSION);
        /*String thanks="";
        thanks = "Special Thanks to:\n";
        thanks= thanks+ "Prasang Sharma, Aman Hussain\n";
        thanks= thanks+ "\n\nAlpha Testers:\n";
        thanks= thanks+ "Gaurav, Subhojeet, Dipankar, Hemant, Mohit, Abhishek, Madhurima, Akanksha, Amrit";
        ((TextView)findViewById(R.id.thanks_view)).setText(thanks);*/
        ((TextView)findViewById(R.id.about)).setText("It is said that Necessity is the mother of Invention. Well, Zchedule has been born right from the " +
                "necessity of a VITian. The ever increasing and demanding schedule in VIT has made it very " +
                "difficult for the students to manage things efficiently. Also the high compulsary attendance criteria " +
                "makes time management a tad bit difficult too. Thus the students end up falling short at the end of " +
                "the semester. Thus Fourth State Lab have decided to come up with something that promises to be a " +
                "blessing in disguise. If you feel that you need an assistant to cope up with the tight schedule in VIT, " +
                "Zchedule promises to be there for you whenever you need. :)");
    }
}
