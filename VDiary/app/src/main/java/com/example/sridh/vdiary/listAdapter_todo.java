package com.example.sridh.vdiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by sid on 12/22/16.
 */

class listAdapter_todo extends BaseAdapter
{
    LayoutInflater inflater=null;
    List<Notification_Holder> list;
    Context context;
    public View view1;

    public listAdapter_todo(Context con, List<Notification_Holder> n)
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
        holder.note.setText(list.get(position).content);

        holder.date.setText(list.get(position).cal.get(Calendar.DATE)+"/"+(list.get(position).cal.get(Calendar.MONTH)+1)+"/"+list.get(position).cal.get(Calendar.YEAR)+" ");
        int x=list.get(position).cal.get(Calendar.AM_PM);
        String ampm;
        if(x==1)
            ampm="PM";
        else
            ampm="AM";

        String time_mins;
        if(list.get(position).cal.get(Calendar.MINUTE)<10)
        {
            time_mins="0"+list.get(position).cal.get(Calendar.MINUTE);
        }
        else
            time_mins=list.get(position).cal.get(Calendar.MINUTE)+"";

        holder.time.setText(list.get(position).cal.get(Calendar.HOUR)+":"+time_mins+ " "+ampm+" ");
        return view1;
    }

    public void update(List<Notification_Holder> not)
    {
        list=not;
        notifyDataSetChanged();
    }
}
