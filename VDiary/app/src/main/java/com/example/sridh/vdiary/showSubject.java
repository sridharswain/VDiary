package com.example.sridh.vdiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.TextView;

public class showSubject extends AppCompatActivity {
    Toolbar bar;
    public static ListAdapter todoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_subject);
        int position=getIntent().getIntExtra("position",0);
        subject clicked= vClass.subList.get(position);
        show(clicked);
        
    }
    //Initialize the popup activity to show the contents of te subject
    void show(subject sub){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout(width,(int)(.5*height));
        bar=(Toolbar)findViewById(R.id.showToolbar);
        bar.inflateMenu(R.menu.menu_show_subject);
        bar.setTitle(sub.code);
        ((TextView)findViewById(R.id.subject_Title)).setText(sub.title);
        ((TextView)findViewById(R.id.subject_Teacher)).setText(sub.teacher);
    }
    void initMenu(){
        bar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_addAssingment:
                        break;
                    case R.id.action_setMeeting:
                        break;
                }
                return false;
            }
        });
    }
}
