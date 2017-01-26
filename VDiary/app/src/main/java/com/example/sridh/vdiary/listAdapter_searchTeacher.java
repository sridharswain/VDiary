package com.example.sridh.vdiary;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.List;

/**
 * Created by sid on 12/22/16.
 */

public class listAdapter_searchTeacher extends BaseAdapter {
    Context context;
    List<teacher> searchResult;
    LayoutInflater inflater;
    EditText searchBox;
    static listAdapter_teachers teacherAdapter;
    TextView editcabin;

    public listAdapter_searchTeacher(Context context, List<teacher> results,EditText searchBox){
        this.context=context;
        this.searchResult=results;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.searchBox=searchBox;
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
        TextView teacherName = ((TextView)teacherView.findViewById(R.id.search_teacher_name));
        teacherName.setTypeface(vClass.nunito_reg);
        teacherName.setText(searchResult.get(position).name);
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
        final AlertDialog.Builder alertBuilder= new AlertDialog.Builder(context);;
        final teacher found=searchResult.get(position);
        searchBox.setText(found.getName());
        final View view= inflater.inflate(R.layout.floatingview_show_teacher,null);
        TextView teachername=((TextView)view.findViewById(R.id.show_teacher_name));
        teachername.setText(found.name);
        teachername.setTypeface(vClass.nunito_bold);
        final TextView cabin =(TextView)view.findViewById(R.id.show_teacher_cabin);
        cabin.setTypeface(vClass.nunito_reg);
        cabin.setText(found.cabin);
        alertBuilder.setView(view);
        final AlertDialog alertDialog = alertBuilder.create();
        (view.findViewById(R.id.show_teacher_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View Clickedview) {
                (view.findViewById(R.id.wrong_information_tap)).setVisibility(View.INVISIBLE);
            }
        });
        (view.findViewById(R.id.show_teacher_no)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClicked) {
                //ACTIVITY TO EXECUTE IF THE GIVEN INFORMATION IS WRONG
                view.findViewById(R.id.layout_edit_teacher).setVisibility(View.VISIBLE);
                cabin.setVisibility(View.INVISIBLE);
                editcabin=((TextView)view.findViewById(R.id.edit_teacher_cabin));
                editcabin.setText(found.cabin);
                if(editcabin.requestFocus()) {
                    InputMethodManager iMM = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    iMM.showSoftInput(editcabin, InputMethodManager.SHOW_IMPLICIT);
                }
                (view.findViewById(R.id.wrong_information_tap)).setVisibility(View.INVISIBLE);
            }
        });
        (view.findViewById(R.id.save_teacher)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CODE TO EXECUTE TO SAVE THE TEAHCER INFORMATION.
                //SHOW EDITED ADDRESS TO THE TEACHER LISTVIEW
                Cabin_Details editedTeacher= new Cabin_Details();
                editedTeacher.cabin=editcabin.getText().toString();
                editedTeacher.name=found.name;
                vClass.cablist.add(editedTeacher);
                editedTeacher.others="Custom";
                teacherAdapter.updatecontent(vClass.cablist);
                workSpace.writeCabListToPrefs();
                alertDialog.cancel();
                //SHOW THAT WE WILL UPDATE THE DATABASE SOON
            }
        });
        alertDialog.show();
    }
}

