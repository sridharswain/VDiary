package com.example.sridh.vdiary;

import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
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
    boolean available=false;
    List<task> tasks=null;
    Gson jsonBuilder = new Gson();
    int width;
    int height;
    TextView notask;
    LinearLayout taskGridLeft;
    LinearLayout taskGridRight;
    int[] colors=new int[]{R.color.teal,R.color.sunflower,R.color.nephritis,R.color.belize,R.color.green_cyan,R.color.amethyst,R.color.pomegranate};
    public static ListAdapter todoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_subject);
        int position=getIntent().getIntExtra("position",0);
        clicked= vClass.subList.get(position);
        show(clicked);  //Initialize the popup activity to show the contents of the subject
        tasks=vClass.courseTasks.get(clicked.code+clicked.type);
        if(tasks!=null){
            layTaskView();
            notask.setVisibility(View.INVISIBLE);
            available=true;
        }

    }
    void show(subject sub){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width=dm.widthPixels;
        height=dm.heightPixels;
        getWindow().setLayout(width,(int)(.6*height));
        bar=(Toolbar)findViewById(R.id.showToolbar);
        bar.inflateMenu(R.menu.menu_show_subject);
        bar.setTitle(sub.code);
        initMenu();
        ((TextView)findViewById(R.id.subject_Title)).setText(sub.title);
        ((TextView)findViewById(R.id.subject_Teacher)).setText(sub.teacher);
        taskGridLeft=(LinearLayout)findViewById(R.id.task_grid_view_left);
        int taskViewWdth=((int) (width*0.496));
        taskGridLeft.getLayoutParams().width= taskViewWdth;
        taskGridRight=(LinearLayout)findViewById(R.id.task_grid_view_right);
        taskGridRight.getLayoutParams().width= taskViewWdth;
        notask=(TextView)findViewById(R.id.no_task_text);
    }
    void initMenu(){
        bar.setBackgroundColor(getResources().getColor(R.color.taskbar_orange));
        bar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_addAssingment:
                        showTaskAdder();
                        break;
                }
                return true;
            }
        });
    }
    void writeToPrefs(){
        SharedPreferences.Editor prefEditor= getSharedPreferences("academicPrefs",MODE_PRIVATE).edit();
        prefEditor.putString("tasks",jsonBuilder.toJson(vClass.courseTasks));
        prefEditor.commit();
    }
    void layTaskView(){
        //Toast.makeText(getApplicationContext(),tasks.get(0).title,Toast.LENGTH_LONG).show();
        int i=0;
        taskGridLeft.removeAllViews();
        taskGridRight.removeAllViews();
        while(i<tasks.size()){
            taskGridLeft.addView(addTask(i));
            i++;
            if(i<tasks.size())
                taskGridRight.addView(addTask(i));
            i++;
        }
    }
    View addTask(int index){
        task cTask= tasks.get(index);
        View taskView= getLayoutInflater().inflate(R.layout.course_task_view,null);
        ((TextView)taskView.findViewById(R.id.task_title)).setText(cTask.title);
        ((TextView)taskView.findViewById(R.id.task_desc)).setText(cTask.desc);
        taskView.setBackground(getResources().getDrawable(R.drawable.soft_corner_taskview));
        GradientDrawable softShape=(GradientDrawable)taskView.getBackground();
        softShape.setColor(getResources().getColor(colors[index%(colors.length)]));
        return taskView;
    }
    void showTaskAdder(){
        AlertDialog.Builder alertBuilder= new AlertDialog.Builder(this);
        final View alertView= getLayoutInflater().inflate(R.layout.floatingview_add_task,null);
        alertBuilder.setView(alertView);
        final AlertDialog alertBox= alertBuilder.create();
        alertBox.show();
        (alertView.findViewById(R.id.add_task_addButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=((TextView)alertView.findViewById(R.id.add_task_title)).getText().toString().trim();
                String desc=((TextView)alertView.findViewById(R.id.add_task_desc)).getText().toString().trim();
                DatePicker date=(DatePicker)alertView.findViewById(R.id.add_task_deadLine);
                if(name.equals("")){
                    Toast.makeText(getApplicationContext(),"Invalid Data !",Toast.LENGTH_SHORT).show();
                }
                else{
                    Calendar deadLine= Calendar.getInstance();
                    deadLine.set(date.getYear(),date.getMonth(),date.getDayOfMonth());
                    if(!available){
                        tasks=new ArrayList<>();
                        available=true;
                        notask.setVisibility(View.INVISIBLE);
                    }
                    tasks.add(new task(name,desc,deadLine));

                    vClass.courseTasks.put(clicked.code+clicked.type,tasks);
                    writeToPrefs();
                    updateTaskGrid(tasks.size()-1);
                    alertBox.cancel();
                }
            }
        });
    }
    void updateTaskGrid(int index){
        if((index)%2==0 ){
            taskGridLeft.addView(addTask(index));
        }
        else{
            taskGridRight.addView(addTask(index));
        }
    }
}