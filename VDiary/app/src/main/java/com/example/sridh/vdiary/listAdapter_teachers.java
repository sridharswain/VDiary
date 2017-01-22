package com.example.sridh.vdiary;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sid on 12/22/16.
 */

class listAdapter_teachers extends BaseAdapter// LIST ADAPTER FOR CABIN VIEW
{
    LayoutInflater inflater=null;
    List<Cabin_Details> cab;
    Context context;
    public View view;
    Typeface nunito_bold,nunito_reg;

    listAdapter_teachers(Context c, List<Cabin_Details> lis)
    {
        context=c;
        nunito_reg=Typeface.createFromAsset(context.getAssets(),"fonts/Nunito-Regular.ttf");
        nunito_bold=Typeface.createFromAsset(context.getAssets(),"fonts/Nunito-Bold.ttf");
        cab=lis;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        view=inflater.inflate(R.layout.rowview_cabin,null);
        Holder holder=new Holder();
        //Initializing
        holder.name=(TextView)view.findViewById(R.id.newname);
        holder.cabin=(TextView)view.findViewById(R.id.newcabin);
        holder.others=(TextView)view.findViewById(R.id.newOthers);

        holder.name.setTypeface(nunito_bold);
        holder.cabin.setTypeface(nunito_reg);
        holder.others.setTypeface(nunito_reg);
        //Setting Data
        holder.name.setText(cab.get(position).name);
        holder.cabin.setText(cab.get(position).cabin);
        holder.others.setText(cab.get(position).others);
        (view.findViewById(R.id.del_teacher)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vClass.cablist.remove(position);
                updatecontent(vClass.cablist);
                workSpace.writeCabListToPrefs();
            }
        });
        return view;
    }

    public void updatecontent(List<Cabin_Details> listt)
    {
        cab=listt;
        notifyDataSetChanged();
    }

}
