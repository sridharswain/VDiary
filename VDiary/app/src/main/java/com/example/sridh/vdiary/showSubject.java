package com.example.sridh.vdiary;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

public class showSubject extends AppCompatActivity {
    Toolbar bar;
    subject clicked;
    int width=vClass.width;
    int height=vClass.height;
    static TextView test;
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
        test=(TextView)findViewById(R.id.testview);
        Async_search a=new Async_search(position);
        a.execute();
        show(clicked);  //Initialize the popup activity to show the contents of the subject
    }
    void show(subject sub){
        getWindow().setLayout(width,((int)(0.6*height)));
        bar=(Toolbar)findViewById(R.id.showToolbar);
        bar.inflateMenu(R.menu.menu_show_subject);
        initMenu();
        ((TextView)findViewById(R.id.subject_Title)).setText(sub.title);
        ((TextView)findViewById(R.id.subject_Teacher)).setText(sub.teacher);

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

    static void setText(String j)
    {
        test.setText(j);
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
        showSubject.setText(x);
        super.onPostExecute(aVoid);
    }
}