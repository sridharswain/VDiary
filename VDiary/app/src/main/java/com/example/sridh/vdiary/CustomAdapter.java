package com.example.sridh.vdiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sparsha Saha on 9/11/2016.
 */
public class CustomAdapter extends BaseAdapter {
    List<subject> list;
    Context context;
    public static View rowview;
    public static LayoutInflater inflater=null;

    //Parameterized Constructor
    CustomAdapter(Context t,List<subject> l)
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
        TextView greenbar;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        rowview=inflater.inflate(R.layout.listview1,null);

        final Holder holder=new Holder();//object of holder class

        holder.subname=(TextView)rowview.findViewById(R.id.nameofsub);
        holder.attendance=(TextView)rowview.findViewById(R.id.attendance);
        holder.teachername=(TextView)rowview.findViewById(R.id.teachersname);
        holder.greenbar=(TextView)rowview.findViewById(R.id.greenbar);

        //set items
        holder.subname.setText(list.get(position).title + "  " +list.get(position).code+"  "+list.get(position).type);
        holder.teachername.setText(list.get(position).teacher);
        holder.attendance.setText(list.get(position).attString);
        return rowview;
    }
}
