package com.example.sridh.vdiary;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
static Context x;
    public static  List<Cabin_Details> cablist;
    NotificationCompat.Builder notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace);
        x=this;

        notification=new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.logo);
        notification.setContentTitle("TEST");
        notification.setContentText("This is a test");
        notification.setWhen(System.currentTimeMillis());
        Intent intent=new Intent(this,workSpace.class);
        PendingIntent pintent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pintent);
        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(2345,notification.build());


        shared=getSharedPreferences("todoshared",MODE_PRIVATE);




        //Get vClass.notes list from shared preferences
        String get_list=shared.getString("todolist","not present");
        if(get_list.equals("not present")==false) {
            Gson jeson = new Gson();
            Type ty = new TypeToken<List<Note>>() {}.getType();
            vClass.notes=jeson.fromJson(get_list,ty);
        }
        //vClass.notes is initialized

        editor=shared.edit();
        setToolbars();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        cablist=new ArrayList<>();
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        vClass.setStatBar(getWindow(),getApplicationContext());

    }
    void setToolbars(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.workspacetoptoolbar);
        toolbar.setTitle("Workspace");
        setSupportActionBar(toolbar);
    }  //SET THE TOOLBARS FOR THE WORKSPACE CLASS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workspace_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.toSchedule) {
            Intent i=new Intent(workSpace.this,schedule.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);

        }
        else if(id==R.id.refresh){
            scrapper.tryRefresh=true;
            startActivity(new Intent(workSpace.this,scrapper.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView=null;
            switch(getArguments().getInt(ARG_SECTION_NUMBER)-1)
            {
                case 1:

                    //Initializing cablist from shared preferences
                    String temp=shared.getString("list","");
                    Type type= new TypeToken<List<Cabin_Details>>(){}.getType();


                    if(!temp.equals("")) {
                        Gson json = new Gson();
                        cablist = json.fromJson(temp, type);
                    }
                    //cablist initialized

                    rootView=inflater.inflate(R.layout.fragment_notes,container,false);
                    FloatingActionButton fab=(FloatingActionButton)rootView.findViewById(R.id.add);
                    ListView lv=(ListView)rootView.findViewById(R.id.cabinview);
                    final cabinDetailAdapter mad=new cabinDetailAdapter(x,cablist);
                    lv.setAdapter(mad);
                    fab.setOnClickListener(new View.OnClickListener() { //Onclick Listener for floating action Button
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder builder=new AlertDialog.Builder(x);




                            final EditText name=new EditText(x);
                           LinearLayout rlay=new LinearLayout(x);
                            rlay.setOrientation(LinearLayout.VERTICAL);
                            name.setInputType(InputType.TYPE_CLASS_TEXT);
                            final EditText cabin=new EditText(x);
                            cabin.setInputType(InputType.TYPE_CLASS_TEXT);
                            final EditText branch=new EditText(x);
                            branch.setInputType(InputType.TYPE_CLASS_TEXT);
                            final TextView t1=new TextView(x);
                            t1.setText("Teacher's Name");
                            rlay.addView(t1);
                            rlay.addView(name);
                            final TextView t2=new TextView(x);
                            t2.setText("Cabin Details");
                            rlay.addView(t2);
                            rlay.addView(cabin);
                            final TextView t3=new TextView(x);
                            t3.setText("Other Details");
                            rlay.addView(t3);
                            rlay.addView(branch);
                            builder.setView(rlay);

                            Button b=new Button(x);
                            b.setText("Enter");
                            rlay.addView(b);
                            final AlertDialog alert=builder.create();
                            alert.show();
                            b.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Cabin_Details c=new Cabin_Details();
                                    c.name=name.getText().toString();
                                    c.cabin=cabin.getText().toString();
                                    c.others=branch.getText().toString();
                                    cablist.add(c);
                                    mad.updatecontent(cablist);
                                    alert.cancel();
                                    Gson json=new Gson();
                                    String k=json.toJson(cablist);
                                    editor.putString("list",k);
                                    editor.apply();


                                }
                            });


                        }
                    });
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            final AlertDialog.Builder build=new AlertDialog.Builder(x);
                            final AlertDialog aler;
                            final LinearLayout lin=new LinearLayout(x);
                            lin.setOrientation(LinearLayout.HORIZONTAL);
                            final Button delete=new Button(x);
                            delete.setText("Delete");
                            lin.addView(delete);
                            build.setView(lin);
                            aler=build.create();
                            aler.show();
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cablist.remove(position);
                                    mad.updatecontent(cablist);
                                    aler.cancel();
                                    Gson json=new Gson();
                                    String k=json.toJson(cablist);
                                    editor.putString("list",k);
                                    editor.apply();

                                }
                            });



                        }
                    });
                    break;
                case 0:
                    rootView=inflater.inflate(R.layout.fragment_courses,container,false);
                    ListView lview=(ListView)rootView.findViewById(R.id.menu2listview);
                    CustomAdapter cadd=new CustomAdapter(x,vClass.subList);
                    lview.setAdapter(cadd);
                    lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    });
                    break;
                case 2:
                    rootView=inflater.inflate(R.layout.fragment_notes,container,false);
                    ListView lview1=(ListView)rootView.findViewById(R.id.cabinview);
                    final todo_adapter adap=new todo_adapter(x,vClass.notes);
                    lview1.setAdapter(adap);
                    FloatingActionButton fb=(FloatingActionButton)rootView.findViewById(R.id.add);


                    //Enter each element of listview
                    fb.setOnClickListener(new View.OnClickListener() { //Floating action button onclick listener
                        @Override
                        public void onClick(View v) {

                            EditText title;
                            EditText other;
                            TimePicker time;
                            DatePicker date;
                            Button ok;
                            NotificationManager notificationmanager= (NotificationManager) x.getSystemService(Context.NOTIFICATION_SERVICE);
                            NotificationCompat.Builder notification=new NotificationCompat.Builder(x);
                            notification.setContentTitle("Test Content");
                            notification.setContentText("Test content text");
                            notification.setContentIntent(PendingIntent.getActivity(x,0,new Intent(),0));
                            notificationmanager.notify(0,notification.build());


                            final AlertDialog alert;
                            View root=getActivity().getLayoutInflater().inflate(R.layout.floatingview_todo,null);
                            title=(EditText) root.findViewById(R.id.title);
                            other=(EditText)root.findViewById(R.id.note);
                            time=(TimePicker)root.findViewById(R.id.timePicker);
                            date=(DatePicker)root.findViewById(R.id.datePicker);
                            ok=(Button)root.findViewById(R.id.enterbutton);
                            AlertDialog.Builder bui=new AlertDialog.Builder(x);
                            bui.setView(root);
                            alert=bui.create();
                            alert.show();

                            //onlick of button ok
                            final DatePicker finalDate = date;
                            final TimePicker finalTime = time;
                            final EditText finalTitle = title;
                            final EditText finalOther = other;
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Note n=new Note();
                                    n.date= finalDate.getDayOfMonth()+"/"+ finalDate.getMonth()+"/"+ finalDate.getYear();
                                    n.time= finalTime.getCurrentHour()+":"+ finalTime.getCurrentMinute();
                                    n.title= finalTitle.getText().toString();
                                    n.note= finalOther.getText().toString();
                                    vClass.notes.add(n);
                                    Gson json=new Gson();
                                    String temporary=json.toJson(vClass.notes);
                                    editor.putString("todolist",temporary);
                                    editor.apply();
                                    adap.update(vClass.notes);
                                    alert.cancel();

                                }
                            });



                        }
                    });

                    // OnItemclicklistener for Click for each item for listview1
                     lview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                         @Override
                         public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                             final LinearLayout layouts=new LinearLayout(x);
                             final Button del=new Button(x);
                             del.setText("Delete");
                             layouts.addView(del);
                             final AlertDialog ale;
                             AlertDialog.Builder buil=new AlertDialog.Builder(x);
                             buil.setView(layouts);
                             ale=buil.create();
                             ale.show();

                             del.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     vClass.notes.remove(position);
                                     adap.update(vClass.notes);
                                     ale.cancel();
                                     Gson jason=new Gson();
                                     editor.putString("todolist",jason.toJson(vClass.notes));
                                     editor.apply();
                                 }
                             });

                         }
                     });


            }

            return rootView;
        }
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All Subjects";
                case 1:
                    return "Cabin Details";
                case 2:
                    return "To-Do";
            }
            return null;
        }
    }

}

class cabinDetailAdapter extends BaseAdapter// LIST ADAPTER FOR CABIN VIEW
{
    LayoutInflater inflater=null;
    List<Cabin_Details> cab;
    Context context;
    public View view;

    cabinDetailAdapter(Context c, List<Cabin_Details> lis)
    {
        cab=lis;
        context=c;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cab.size();
    }

    @Override
    public Object getItem(int position) {
        return cab.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        public TextView name,cabin,others;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        view=inflater.inflate(R.layout.rowview_cabin,null);
        Holder holder=new Holder();
        //Initializing
        holder.name=(TextView)view.findViewById(R.id.newname);
        holder.cabin=(TextView)view.findViewById(R.id.newcabin);
        holder.others=(TextView)view.findViewById(R.id.newOthers);
        //Setting Data
        holder.name.setText(cab.get(position).name);
        holder.cabin.setText(cab.get(position).cabin);
        holder.others.setText(cab.get(position).others);

        return view;
    }

    public void updatecontent(List<Cabin_Details> listt)
    {
        cab=listt;
        notifyDataSetChanged();
    }
}
 class todo_adapter extends BaseAdapter
 {
     LayoutInflater inflater=null;
     List<Note> list;
     Context context;
     public View view1;

     public todo_adapter(Context con,List<Note> n)
     {
         context=con;
         list=n;
         inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     }


     @Override
     public int getCount() {
         return list.size();
     }

     @Override
     public Object getItem(int position) {
         return list.get(position);
     }

     @Override
     public long getItemId(int position) {
         return position;
     }

     class Holder
     {
        TextView title,note,time,date;
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
         view1=inflater.inflate(R.layout.rowview_todo,null);
         Holder holder=new Holder();
         holder.title=(TextView)view1.findViewById(R.id.titleview);
         holder.note=(TextView)view1.findViewById(R.id.noteview);
         holder.time=(TextView)view1.findViewById(R.id.timeview);
         holder.date=(TextView)view1.findViewById(R.id.dateview);
         holder.title.setText(list.get(position).title);
         holder.note.setText(list.get(position).note);
         holder.date.setText(list.get(position).date);
         holder.time.setText(list.get(position).time);
         return view1;
     }

     public void update(List<Note> not)
     {
         list=not;
         notifyDataSetChanged();
     }
 }