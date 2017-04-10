package com.example.sridh.vdiary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;


public class SelectCampus extends AppCompatActivity {

    TextView tv_campus;
    RadioButton rb_chennai,rb_vellore;
    FloatingActionButton fb_toScrapper;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         context = this;
        if(scrapper.readFromPrefs(context)){
            startActivity(new Intent(this, workSpace.class));
            ((Activity)context).overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
            finish();
        }
        else initLayout();
    }

    void initLayout(){
        vClass.getFonts(getApplicationContext());
        vClass.setStatusBar(getWindow(),getApplicationContext(), R.color.colorPrimaryDark);
        setContentView(R.layout.activity_select_campus);
        tv_campus = (TextView)findViewById(R.id.tv_campus);
        tv_campus.setTypeface(vClass.nunito_Extrabold);

        rb_chennai=(RadioButton)findViewById(R.id.rb_chennai);
        rb_chennai.setTypeface(vClass.nunito_bold);

        rb_vellore=(RadioButton)findViewById(R.id.rb_vellore);
        rb_vellore.setTypeface(vClass.nunito_bold);

        fb_toScrapper =(FloatingActionButton)findViewById(R.id.fb_toScrapper);

        rb_chennai.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    rb_vellore.setChecked(false);
                }
            }
        });
        rb_vellore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) rb_chennai.setChecked(false);
            }
        });

        fb_toScrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rb_chennai.isChecked()) prefs.put(getApplicationContext(),prefs.campusUrl,vClass.CHENNAI_URL);
                else prefs.put(getApplicationContext(),prefs.campusUrl,vClass.VELLORE_URL);
                startActivity(new Intent(SelectCampus.this,scrapper.class));
                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
            }
        });
    }
}
