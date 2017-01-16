package com.example.sridh.vdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class splashScreen extends AppCompatActivity {

    public static String LAUNCH_PREFS ="launchPrefs";
    public  static String IS_FIRST ="isFirst";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //if(isFirstLaunch()){
                    startActivity(new Intent(splashScreen.this,TutorialActivity.class));
                //}
                /*else {
                    startActivity(new Intent(splashScreen.this, scrapper.class));
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }*/
            }
        },550);
    }

    boolean isFirstLaunch(){
        SharedPreferences prefs = getSharedPreferences(LAUNCH_PREFS,MODE_PRIVATE);
        return prefs.getBoolean(IS_FIRST,true);
    }

}
