package com.example.sridh.vdiary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class allsubs extends Fragment {

TextView name;
    ListView lv;
    Context c;
    public allsubs() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rowview=inflater.inflate(R.layout.fragment_allsubs,container,false);
        name=(TextView)rowview.findViewById(R.id.name);
        lv=(ListView)rowview.findViewById(R.id.lv);
        c=this.getActivity();
        CustomAdapter cv=new CustomAdapter(this.getActivity(),vClass.subList);
        name.setText("All Subjects");
        lv.setAdapter(cv);


        // Inflate the layout for this fragment
        return rowview;
    }




}
