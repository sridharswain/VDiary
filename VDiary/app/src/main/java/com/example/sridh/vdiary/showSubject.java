package com.example.sridh.vdiary;

import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class showSubject extends AppCompatActivity {
    Toolbar bar;
    subject clicked;
    int width=vClass.width;
    int height=vClass.height;
    TextView notask;
    LinearLayout taskGridLeft;
    LinearLayout taskGridRight;
    int taskViewWidth;
    public static ListAdapter todoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_subject);
        int position=getIntent().getIntExtra("position",0);
        clicked= vClass.subList.get(position);
        show(clicked);  //Initialize the popup activity to show the contents of the subject
    }
    void show(subject sub){
        getWindow().setLayout(width,((int)(0.6*height)));
        bar=(Toolbar)findViewById(R.id.showToolbar);
        bar.inflateMenu(R.menu.menu_show_subject);
        initMenu();
        ((TextView)findViewById(R.id.subject_Title)).setText(sub.title);
        ((TextView)findViewById(R.id.subject_Teacher)).setText(sub.teacher);
        taskGridLeft=(LinearLayout)findViewById(R.id.task_grid_view_left);
        taskViewWidth=((int) (width*0.496));
        taskGridLeft.getLayoutParams().width= taskViewWidth;
        taskGridRight=(LinearLayout)findViewById(R.id.task_grid_view_right);
        taskGridRight.getLayoutParams().width= taskViewWidth;
        notask=(TextView)findViewById(R.id.no_task_text);
    }
    void initMenu(){
        bar.setBackgroundColor(getResources().getColor(R.color.taskbar_orange));
        bar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_addAssingment:
                        //
                        break;
                }
                return true;
            }
        });
    }
}