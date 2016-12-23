package com.example.sridh.vdiary;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sid on 12/22/16.
 */

public class listAdapter_searchTeacher extends BaseAdapter {
    Context context;
    List<teacher> searchResult;
    LayoutInflater inflater;
    public listAdapter_searchTeacher(Context context, List<teacher> results){
        this.context=context;
        this.searchResult=results;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return searchResult.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        View teacherView = inflater.inflate(R.layout.rowview_search_teacher,null);
        ((TextView)teacherView.findViewById(R.id.search_teacher_name)).setText(searchResult.get(position).name);
        teacherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTeacher(position);
            }
        });
        return teacherView;
    }
    void update(List<teacher> newResults){
        searchResult=newResults;
        notifyDataSetChanged();
    }
    void showTeacher(int position){
        AlertDialog.Builder alertBuilder= new AlertDialog.Builder(context);
        teacher found=searchResult.get(position);
        View view= inflater.inflate(R.layout.floatingview_show_teacher,null);
        ((TextView)view.findViewById(R.id.show_teacher_name)).setText(found.name);
        ((TextView)view.findViewById(R.id.show_teacher_cabin)).setText(found.cabin);
        alertBuilder.setView(view);
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }
}
