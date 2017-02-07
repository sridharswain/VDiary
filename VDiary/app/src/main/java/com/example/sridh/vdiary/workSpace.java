package com.example.sridh.vdiary;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class workSpace extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;
    public static SharedPreferences shared;
    public static SharedPreferences.Editor editor;
    static Context context;

    static int id = 1000;


    static ListView resultList;

    @Override
    public void onBackPressed() {
        if (resultList.getVisibility() == View.VISIBLE) {
            resultList.setVisibility(View.INVISIBLE);
        } else finish();
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_workspace,null);
        setContentView(rootView);
        setOnTouchListener(rootView,workSpace.this);
        context = this;
        SharedPreferences s = getSharedPreferences("notiftimetable", Context.MODE_PRIVATE);
        String z = s.getString("last_ref", "");
        if (!z.equals(""))
            Toast.makeText(context, "Last synced on " + z, Toast.LENGTH_LONG).show();
        shared = getSharedPreferences("todoshared", MODE_PRIVATE);
        //Get vClass.notes list from shared preferences
        String get_list = shared.getString("todolist", null);
        if (get_list != null) {
            vClass.notes = Notification_Holder.convert_from_jason(get_list);
        }
        //vClass.notes is initialized

        editor = shared.edit();
        id = shared.getInt("identifier", 1000);
        setToolbars();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setTabLayout(tabLayout);
        vClass.setStatusBar(getWindow(), getApplicationContext(),R.color.colorPrimaryDark);

    }

    void setTabLayout(TabLayout tabLayout){
        final int[] unselectedDrawables= new int[]{R.drawable.notselected_course_book,R.drawable.notselected_teacher,R.drawable.notselected_tasks};
        final int[] selectedDrawables = new int[]{R.drawable.selected_course_book,R.drawable.selected_teacher,R.drawable.selected_tasks};
        for(int i=1;i<3;i++){
            tabLayout.getTabAt(i).setIcon(unselectedDrawables[i]);
        }
        tabLayout.getTabAt(0).setIcon(R.drawable.selected_course_book);
        //TABICONS INITIALISED
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                tab.setIcon(selectedDrawables[position]);
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                tab.setIcon(unselectedDrawables[position]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    void setToolbars() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.workspacetoptoolbar);
        toolbar.inflateMenu(R.menu.menu_workspace_top);
        TextView title = (TextView)toolbar.findViewById(R.id.workSpace_title);
        title.setTypeface(vClass.fredoka);
        //setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();

                switch (id){
                    case R.id.toSchedule:
                        Intent i = new Intent(workSpace.this, schedule.class);
                        startActivity(i);
                        break;
                    case R.id.refresh:
                        scrapper.tryRefresh = true;
                        startActivity(new Intent(workSpace.this, scrapper.class));
                        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                        finish();
                        break;
                    case R.id.action_more_settings:
                        startActivity(new Intent(workSpace.this,settings.class));
                        break;
                    case  R.id.action_show_about:
                        startActivity(new Intent(workSpace.this,About.class));
                        break;
                }
                return true;
            }
        });
    }  //SET THE TOOLBARS FOR THE WORKSPACE CLASS


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        EditText teacherSearch;
        List<teacher> searchResult;

        public PlaceholderFragment(){
        }
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 final Bundle savedInstanceState) {
            switch (getArguments().getInt(ARG_SECTION_NUMBER) - 1) {
                case 0:
                    View rootViewCourse = inflater.inflate(R.layout.fragment_courses, container, false);
                    setOnTouchListener(rootViewCourse,getActivity());
                    ListView lview = (ListView) rootViewCourse.findViewById(R.id.course_listview);
                    listAdapter_courses cadd = new listAdapter_courses(context, vClass.subList);
                    lview.setAdapter(cadd);
                    lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Intent showSubjectIntent = new Intent(context, showSubject.class);
                            showSubjectIntent.putExtra("position", position);
                            context.startActivity(showSubjectIntent);
                        }
                    });
                    return rootViewCourse;
                case 1:
                    View rootViewteachers = inflater.inflate(R.layout.fragment_teachers, container, false);
                    setOnTouchListener(rootViewteachers,getActivity());
                    setSearcher(rootViewteachers);
                    FloatingActionButton fab = (FloatingActionButton) rootViewteachers.findViewById(R.id.teachers_add);
                    ListView lv = (ListView) rootViewteachers.findViewById(R.id.teachers_list);
                    final listAdapter_teachers mad = new listAdapter_teachers(context, vClass.cablist);
                    listAdapter_searchTeacher.teacherAdapter = mad;
                    lv.setAdapter(mad);
                    fab.setOnClickListener(new View.OnClickListener() { //Onclick Listener for floating action Button
                        @Override
                        public void onClick(View v) {
                            showCabinAlertDialog(mad);
                        }
                    });
                    return rootViewteachers;
                case 2:
                    View rootViewNotes = inflater.inflate(R.layout.fragment_notes, container, false);
                    setOnTouchListener(rootViewNotes,getActivity());
                    populateTaskGrid(rootViewNotes);
                    FloatingActionButton fb = (FloatingActionButton) rootViewNotes.findViewById(R.id.notes_add);
                    fb.setOnClickListener(new View.OnClickListener() { //Floating action button onclick listener
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alert;
                            View root = getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_todo, null);
                            final EditText title = (EditText) root.findViewById(R.id.title);
                            final EditText other = (EditText) root.findViewById(R.id.note);
                            final Switch reminderSwitch =(Switch)root.findViewById(R.id.add_todo_reminder_switch);
                            Toolbar addTaskToolbar=((Toolbar)root.findViewById(R.id.add_task_toolbar));
                            addTaskToolbar.inflateMenu(R.menu.menu_add_todo);
                            reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                                    if(checked) {
                                        c = null;
                                        showReminderSetter(reminderSwitch);
                                    }
                                    else {
                                        c = null;
                                        reminderSwitch.setText("Set Reminder");
                                    }
                                }
                            });
                            AlertDialog.Builder bui = new AlertDialog.Builder(context);
                            bui.setView(root);
                            alert = bui.create();
                            alert.show();
                            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    hideSoftKeyboard(getActivity());
                                }
                            });
                            addTaskToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    int id = item.getItemId();
                                    if(id==R.id.action_add_todo) {
                                        if (title.getText().toString() != null && title.getText().toString().equals("") != true & other.getText().toString() != null && other.getText().toString().equals("") != true) {
                                            Notification_Holder n;
                                            if(c!=null) {
                                                n = new Notification_Holder(c, title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                                schedule_todo_notification(n);
                                            }
                                            else
                                                n = new Notification_Holder(Calendar.getInstance(), title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                            vClass.notes.add(n);
                                            updateTaskGrid(vClass.notes.size() - 1);
                                            Gson json = new Gson();
                                            String temporary = json.toJson(vClass.notes);
                                            editor.putString("todolist", temporary);
                                            editor.apply();
                                            alert.cancel();
                                        } else
                                            Toast.makeText(getContext(), "Both title and note must contain some text", Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                    return false;
                                }
                            });
                        }
                    });
                    return rootViewNotes;
            }
            return null;
        }

        public void schedule_todo_notification(Notification_Holder n) {
            if (n.cal.getTimeInMillis() > System.currentTimeMillis()) {
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getActivity(), NotifyService.class);
                Gson js = new Gson();
                String f = js.toJson(n);
                intent.putExtra("notificationContent", f);
                intent.putExtra("fromClass","WorkSpace");
                id++;
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id, intent, 0);
                editor.putInt("identifier", id);
                editor.apply();
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, n.cal.getTimeInMillis(), pendingIntent);
            }
        }
        Calendar c;
        void showReminderSetter(final Switch reminderSwitch){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            View dateTimeView = getActivity().getLayoutInflater().inflate(R.layout.floatingview_set_datetime,null);
            alertBuilder.setView(dateTimeView);
            final TimePicker time = (TimePicker) dateTimeView.findViewById(R.id.timePicker);
            final DatePicker date = (DatePicker) dateTimeView.findViewById(R.id.datePicker);
            Button ok = (Button)dateTimeView.findViewById(R.id.datetime_ok);
            final AlertDialog alert = alertBuilder.create();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    c=Calendar.getInstance();
                    c.set(date.getYear(), date.getMonth(), date.getDayOfMonth(), time.getCurrentHour(), time.getCurrentMinute());
                    alert.cancel();
                }
            });
            (dateTimeView.findViewById(R.id.datetime_cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.cancel();
                }
            });
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if(c==null){
                        reminderSwitch.setChecked(false);
                    }else{
                        reminderSwitch.setText(getDateTimeString(c));

                    }
                }
            });
            alert.show();
        }


        void showCabinAlertDialog(final listAdapter_teachers cabinListAdapter) {
            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            final View alertCabinView = getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_cabin, null);
            alertBuilder.setView(alertCabinView);
            final AlertDialog alert = alertBuilder.create();
            Toolbar addCabinToolbar =(Toolbar)alertCabinView.findViewById(R.id.alert_cabin_toolbar);
            addCabinToolbar.inflateMenu(R.menu.menu_add_todo);
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    workSpace.hideSoftKeyboard(getActivity());
                }
            });
            addCabinToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if(id==R.id.action_add_todo){
                        String name = ((TextView) alertCabinView.findViewById(R.id.alert_cabin_teacherName)).getText().toString();
                        String cabin = ((TextView) alertCabinView.findViewById(R.id.alert_cabin_cabinAddress)).getText().toString();
                        String comment = ((TextView) alertCabinView.findViewById(R.id.alert_cabin_comment)).getText().toString();
                        if (name.trim().equals("") || cabin.trim().equals("")) {
                            Toast.makeText(context, "Invalid Data !", Toast.LENGTH_LONG).show();
                        }
                        else {
                            for (int i = 0; i < vClass.cablist.size(); i++) {
                                if (vClass.cablist.get(i).name.toLowerCase().equals(name.toLowerCase())) {
                                    vClass.cablist.get(i).cabin = cabin;
                                    vClass.cablist.get(i).others = comment;
                                    writeCabListToPrefs();
                                    cabinListAdapter.updatecontent(vClass.cablist);
                                    alert.cancel();
                                    return true;
                                }
                            }
                            Cabin_Details c = new Cabin_Details();
                            c.name = name;
                            c.cabin = cabin;
                            c.others = comment;
                            vClass.cablist.add(c);
                            writeCabListToPrefs();
                            cabinListAdapter.updatecontent(vClass.cablist);
                            alert.cancel();
                            return true;
                        }
                    }
                    return false;
                }
            });
            alert.show();
        }  //CREATE AND HANDLES THE ALERT DIALOG BOX TO ADD CABIN


        void setSearcher(View view) {
            searchResult = new ArrayList<>();
            teacherSearch = (EditText) view.findViewById(R.id.teachers_searchText);
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(teacherSearch.getWindowToken(), 0);
            teacherSearch.setTypeface(vClass.nunito_reg);
            resultList = (ListView) view.findViewById(R.id.teachers_search_list);
            final listAdapter_searchTeacher searchAdapter = new listAdapter_searchTeacher(context, searchResult,teacherSearch);
            resultList.setAdapter(searchAdapter);
            teacherSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    searchResult.clear();
                    String search = teacherSearch.getText().toString();
                    if (!(search.length() < 2)) {
                        resultList.setVisibility(View.VISIBLE);
                        for (int i = 0; i < vClass.teachers.size(); i++) {
                            teacher var = vClass.teachers.get(i);
                            if (var.name.toLowerCase().contains(search.toLowerCase())) {
                                searchResult.add(var);
                            }
                        }
                    } else {
                        resultList.setVisibility(View.INVISIBLE);
                    }
                    if(searchResult.size()==0)
                        resultList.setVisibility(View.INVISIBLE);
                    searchAdapter.update(searchResult);
                }
            });
        }

        LinearLayout taskGridLeft, taskGridRight;

        void populateTaskGrid(View root) {
            taskGridLeft = (LinearLayout) root.findViewById(R.id.task_grid_view_left);
            int taskViewWidth = ((int) (vClass.width * 0.492));
            taskGridLeft.getLayoutParams().width = taskViewWidth;
            taskGridRight = (LinearLayout) root.findViewById(R.id.task_grid_view_right);
            taskGridRight.getLayoutParams().width = taskViewWidth;
            if (vClass.notes.size() > 0) {
                int i = 0;
                taskGridLeft.removeAllViews();
                taskGridRight.removeAllViews();
                while (i < vClass.notes.size()) {
                    View LeftView = getTaskView(i);
                    setTaskOnClick(LeftView,i);
                    taskGridLeft.addView(LeftView);
                    i++;
                    if (i < vClass.notes.size()) {
                        View rightView = getTaskView(i);
                        setTaskOnClick(rightView, i);
                        taskGridRight.addView(rightView);
                    }
                    i++;
                }
            }
        }
        AlertDialog expanded;
        void setTaskOnClick(View view,final int index){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(getExpanded(index));
                    expanded= alertDialogBuilder.create();
                    expanded.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    expanded.show();
                    expanded.getWindow().setLayout(((int)(vClass.width*0.8)),RelativeLayout.LayoutParams.WRAP_CONTENT);
                }
            });
        }

        int[] colors = new int[]{R.color.dot_light_screen1,R.color.dot_light_screen5, R.color.dot_light_screen2,R.color.dot_light_screen3,R.color.dot_light_screen4};

        View getTaskView(final int index) {
            final Notification_Holder cTask = vClass.notes.get(index);
            final View taskView = getActivity().getLayoutInflater().inflate(R.layout.course_task_card_view, null);
            TextView title =((TextView) taskView.findViewById(R.id.task_title));
            ImageButton edit=(ImageButton) taskView.findViewById(R.id.task_edit);
            title.setTypeface(vClass.nunito_bold);
            title.setText(cTask.title);
            TextView deadLineTextView= (TextView) taskView.findViewById(R.id.task_deadLine);
            deadLineTextView.setTypeface(vClass.nunito_reg);
            TextView desc=((TextView) taskView.findViewById(R.id.task_desc));
            desc.setTypeface(vClass.nunito_reg);
            desc.setText(cTask.content);
            (taskView.findViewById(R.id.task_delete)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (index % 2 != 0) {
                        taskGridRight.removeViewAt((index/2));
                    } else {
                        taskGridLeft.removeViewAt(index/2);
                    }
                    delTask(cTask);
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Notification_Holder n=vClass.notes.get(index);
                    final AlertDialog alert;
                    View root = getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_todo, null);
                    final EditText title = (EditText) root.findViewById(R.id.title);
                    final EditText other = (EditText) root.findViewById(R.id.note);
                    final Switch reminderSwitch =(Switch)root.findViewById(R.id.add_todo_reminder_switch);
                    Toolbar addTaskToolbar=((Toolbar)root.findViewById(R.id.add_task_toolbar));
                    addTaskToolbar.inflateMenu(R.menu.menu_add_todo);
                    title.setText(n.title);
                    other.setText(n.content);


                    AlertDialog.Builder bui = new AlertDialog.Builder(context);
                    bui.setView(root);
                    alert = bui.create();
                    alert.show();

                    reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if(checked) {
                                c = null;
                                showReminderSetter(reminderSwitch);
                            }
                            else {
                                c = null;
                                reminderSwitch.setText("Set Reminder");
                            }
                        }
                    });
                    addTaskToolbar.setTitle("Edit Note");
                    addTaskToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if(id==R.id.action_add_todo) {
                                if (title.getText().toString() != null && title.getText().toString().equals("") != true & other.getText().toString() != null && other.getText().toString().equals("") != true) {
                                    Notification_Holder n;
                                    if(c!=null) {
                                        n = new Notification_Holder(c, title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                        schedule_todo_notification(n);
                                    }
                                    else
                                        n = new Notification_Holder(Calendar.getInstance(), title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                    updateTask(n,index);
                                    Gson json = new Gson();
                                    String temporary = json.toJson(vClass.notes);
                                    editor.putString("todolist", temporary);
                                    editor.apply();
                                    if (index % 2 != 0) {
                                        int i=taskGridRight.indexOfChild(taskView);
                                        taskGridRight.removeViewAt(i);
                                        View view = getTaskView(index);
                                        setTaskOnClick(view,index);
                                        taskGridRight.addView(view,i);
                                    } else {
                                        int i =taskGridLeft.indexOfChild(taskView);
                                        taskGridLeft.removeView(taskView);
                                        View view = getTaskView(index);
                                        setTaskOnClick(view,index);
                                        taskGridLeft.addView(view,i);
                                    }
                                    workSpace.hideSoftKeyboard(getActivity());
                                    alert.cancel();
                                } else
                                    Toast.makeText(getContext(), "Both title and note must contain some text", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            return false;
                        }
                    });
                }
            });
            Calendar deadLine = cTask.cal;
            deadLineTextView.setText(getDateTimeString(deadLine));
            //taskView.setBackground(getResources().getDrawable(R.drawable.soft_corner_taskview));
            GradientDrawable softShape = (GradientDrawable) taskView.getBackground();
            final int colorIndex = index % (colors.length);
            softShape.setColor(getResources().getColor(colors[colorIndex]));
            return taskView;
        }

        View getExpanded(final int index){
            final Notification_Holder cTask = vClass.notes.get(index);
            final View taskView = getActivity().getLayoutInflater().inflate(R.layout.expanded_task_card_view, null);
            TextView title =((TextView) taskView.findViewById(R.id.task_title));
            ImageButton edit=(ImageButton) taskView.findViewById(R.id.task_edit);
            title.setTypeface(vClass.nunito_bold);
            title.setText(cTask.title);
            TextView deadLineTextView= (TextView) taskView.findViewById(R.id.task_deadLine);
            deadLineTextView.setTypeface(vClass.nunito_reg);
            TextView desc=((TextView) taskView.findViewById(R.id.task_desc));
            desc.setTypeface(vClass.nunito_reg);
            desc.setText(cTask.content);
            (taskView.findViewById(R.id.task_delete)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (index % 2 != 0) {
                        taskGridRight.removeViewAt((index/2));
                    } else {
                        taskGridLeft.removeViewAt(index/2);
                    }
                    try{expanded.cancel();}
                    catch (Exception e){}
                    delTask(cTask);
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Notification_Holder n=vClass.notes.get(index);
                    final AlertDialog alert;
                    View root = getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_todo, null);
                    final EditText title = (EditText) root.findViewById(R.id.title);
                    final EditText other = (EditText) root.findViewById(R.id.note);
                    final Switch reminderSwitch =(Switch)root.findViewById(R.id.add_todo_reminder_switch);
                    Toolbar addTaskToolbar=((Toolbar)root.findViewById(R.id.add_task_toolbar));
                    addTaskToolbar.inflateMenu(R.menu.menu_add_todo);
                    title.setText(n.title);
                    other.setText(n.content);


                    AlertDialog.Builder bui = new AlertDialog.Builder(context);
                    bui.setView(root);
                    alert = bui.create();
                    alert.show();

                    reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if(checked) {
                                c = null;
                                showReminderSetter(reminderSwitch);
                            }
                            else {
                                c = null;
                                reminderSwitch.setText("Set Reminder");
                            }
                        }
                    });
                    addTaskToolbar.setTitle("Edit Note");
                    addTaskToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if(id==R.id.action_add_todo) {
                                if (title.getText().toString() != null && title.getText().toString().equals("") != true & other.getText().toString() != null && other.getText().toString().equals("") != true) {
                                    Notification_Holder n;
                                    if(c!=null) {
                                        n = new Notification_Holder(c, title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                        schedule_todo_notification(n);
                                    }
                                    else
                                        n = new Notification_Holder(Calendar.getInstance(), title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                    updateTask(n,index);
                                    Gson json = new Gson();
                                    String temporary = json.toJson(vClass.notes);
                                    editor.putString("todolist", temporary);
                                    editor.apply();
                                    int indexOfView=index/2;
                                    if (index % 2 != 0) {
                                        View view = getTaskView(index);
                                        setTaskOnClick(view,index);
                                        taskGridRight.removeViewAt(indexOfView);
                                        taskGridRight.addView(view,indexOfView);
                                    } else {
                                        View view = getTaskView(index);
                                        setTaskOnClick(view,index);
                                        taskGridLeft.removeViewAt(indexOfView);
                                        taskGridLeft.addView(view,indexOfView);
                                    }
                                    expanded.cancel();
                                    workSpace.hideSoftKeyboard(getActivity());
                                    alert.cancel();
                                } else
                                    Toast.makeText(getContext(), "Both title and note must contain some text", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            return false;
                        }
                    });
                }
            });
            Calendar deadLine = cTask.cal;
            deadLineTextView.setText(getDateTimeString(deadLine));
            //taskView.setBackground(getResources().getDrawable(R.drawable.soft_corner_taskview));
            GradientDrawable softShape = (GradientDrawable) taskView.getBackground();
            final int colorIndex = index % (colors.length);
            softShape.setColor(getResources().getColor(colors[colorIndex]));
            return taskView;
        }

        String getAMPM(int AMPM){
            if(AMPM==0)
                return "AM";
            else
                return "PM";
        }

        String getMinute(int minute){
            if(minute<10){
                return ("0"+minute);
            }
            return String.valueOf(minute);
        }

        String getHour(int hour){
            if(hour==0) return "12";
            else return String.valueOf(hour);
        }
        String getDateTimeString(Calendar deadLine){
            int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            int taskDay= deadLine.get(Calendar.DAY_OF_YEAR);
            String dateString;
            String timeString = getHour(deadLine.get(Calendar.HOUR))+":"+getMinute(deadLine.get(Calendar.MINUTE))+getAMPM(deadLine.get(Calendar.AM_PM));
            if(today==taskDay){
                dateString= "Today";
            }
            else if(today==taskDay-1){
                dateString="Tomorrow";
            }
            else if(today==taskDay+1){
                dateString="Yesterday";
            }
            else{
                dateString=deadLine.get(Calendar.DATE) + "/" + (deadLine.get(Calendar.MONTH) + 1) + "/" + deadLine.get(Calendar.YEAR);
            }

            return (dateString+" "+timeString);
        }

        void delTask(Notification_Holder task) {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), NotifyService.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), task.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
            vClass.notes.remove(task);
            editor.putString("todolist", Notification_Holder.convert_to_jason(vClass.notes));
            editor.commit();
        }

        void updateTask(Notification_Holder newtask, int index){
            Notification_Holder oldTask= vClass.notes.get(index);
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), NotifyService.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), oldTask.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
            vClass.notes.set(index,newtask);
            editor.putString("todolist", Notification_Holder.convert_to_jason(vClass.notes));
            editor.commit();
        }

        void updateTaskGrid(int index) {
            View view = getTaskView(index);
            setTaskOnClick(view,index);
            if ((index) % 2 == 0) {
                taskGridLeft.addView(view);
            } else {
                taskGridRight.addView(view);
            }
        } //UPDATE THE TASK GRID WHEN NEW TASK IS ADDED
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Courses";
                case 1:
                    return "Faculty";
                case 2:
                    return "Tasks";
            }
            return null;
        }



    }

    public static void writeCabListToPrefs() {
        Gson json=new Gson();
        String cabListJson=json.toJson(vClass.cablist);
        SharedPreferences.Editor cabListEditor= context.getSharedPreferences("teacherPrefs",MODE_PRIVATE).edit();
        cabListEditor.putString("customTeachers",cabListJson);
        cabListEditor.commit();
    } //SAVE THE CONTENT OF CABLIST TO THE PREFERENCES

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e){
            //KEYBOARD HIDE FAILED
        }
    }

    public static void setOnTouchListener(View view, final Activity activity) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    if (resultList.getVisibility()==View.VISIBLE){
                        resultList.setVisibility(View.INVISIBLE);
                    }
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setOnTouchListener(innerView,activity);
            }
        }
    }
}