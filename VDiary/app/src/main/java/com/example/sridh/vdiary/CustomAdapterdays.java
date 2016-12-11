package com.example.sridh.vdiary;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sparsha Saha on 9/12/2016.
 */

public class CustomAdapterdays extends BaseAdapter {
    List<subject> lis;
    Context context;

    public static View rowview;
    public static LayoutInflater inflater=null;
    //parameterized constructor
    public CustomAdapterdays(Context c,List<subject> j)
    {
        lis=j;
        context=c;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return lis.size();
    }

    @Override
    public Object getItem(int position) {
        return lis.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView subname,attend,teachrname,grnbar,timedisplayer;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        rowview=inflater.inflate(R.layout.listviewdays,null);
        Holder holder=new Holder();
        holder.subname=(TextView)rowview.findViewById(R.id.subjectnamedays);
        holder.teachrname=(TextView)rowview.findViewById(R.id.teachrnm);
        holder.attend=(TextView)rowview.findViewById(R.id.attendancepercentage);
        holder.grnbar=(TextView)rowview.findViewById(R.id.colorbar);
        holder.subname.setText(lis.get(position).title+"  "+lis.get(position).code+"  "+lis.get(position).type);
        holder.teachrname.setText(lis.get(position).teacher);
        holder.attend.setText(lis.get(position).attString);
        holder.timedisplayer=(TextView)rowview.findViewById(R.id.timedisplayer);

        String temporary=lis.get(position).time;
        String temp1=temporary.substring(0,5);
        char ampm=temporary.charAt(6);
        String ampmset;
        if(ampm=='A')
            ampmset="AM";
        else
            ampmset="PM";

        String temp2=temporary.substring(9);

        holder.timedisplayer.setText(temp1+" "+ampmset+" "+"-"+" "+temp2+" ");

        if(!lis.get(position).code.equals("")){
            String temp=holder.attend.getText().toString().substring(0,holder.attend.getText().toString().length()-1);
            if(Integer.parseInt(temp)<75)
            {
                holder.grnbar.setBackgroundColor(Color.RED);
            }
        }
        return rowview;
    }
}
