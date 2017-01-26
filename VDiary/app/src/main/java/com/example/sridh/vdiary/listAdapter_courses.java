package com.example.sridh.vdiary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.graphics.Typeface;

import java.util.List;

/**
 * Created by Sparsha Saha on 9/11/2016.
 */
public class listAdapter_courses extends BaseAdapter {
    List<subject> list;
    Context context;
    public static View rowview;
    public static LayoutInflater inflater=null;

    //Constructor
    listAdapter_courses(Context t, List<subject> l)
    {
        context=t;
        list=l;
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
        return 0;
    }

    //Class to hold objects in listview1

    public class Holder
    {
        TextView subname;
        TextView teachername;
        TextView attendance;
        TextView type;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        subject sub=list.get(position);
        rowview=inflater.inflate(R.layout.rowview_course,null);
        final Holder holder=new Holder();//object of holder class
        holder.subname=(TextView)rowview.findViewById(R.id.course_title);
        holder.attendance=(TextView)rowview.findViewById(R.id.course_attendance);
        holder.teachername=(TextView)rowview.findViewById(R.id.course_teacher);
        holder.type=(TextView)rowview.findViewById(R.id.course_type);

        holder.subname.setTypeface(vClass.nunito_bold);
        holder.teachername.setTypeface(vClass.nunito_reg);
        holder.attendance.setTypeface(vClass.nunito_reg);
        holder.type.setTypeface(vClass.nunito_reg);
        //set items
        holder.subname.setText(sub.title);
        holder.teachername.setText(sub.teacher);
        holder.attendance.setText(sub.attString);
        holder.type.setText(sub.type);
        int attendance=Integer.parseInt(sub.attString.substring(0,sub.attString.indexOf('%')));
        if(attendance<75){
            holder.attendance.setTextColor(Color.RED);
        }
        return rowview;
    }
}
