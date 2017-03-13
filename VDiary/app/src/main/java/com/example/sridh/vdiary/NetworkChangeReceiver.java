package com.example.sridh.vdiary;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.List;

import static com.example.sridh.vdiary.prefs.*;
import static com.example.sridh.vdiary.prefs.dataVersion;

/**
 * Created by Sparsha Saha on 1/15/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    Context context;
    @Override
    public void onReceive(Context ctxt, Intent intent) {
        context=ctxt;
        attachFirebaseListener(context);
        requestToDatabase(context);
    }
    void requestToDatabase(Context context){
        //REQUEST TO DATABASE
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String teacherJson = get(context,toUpdate,null);//teacherPrefs.getString("toUpdate",null);
        if(teacherJson!=null){
            List<Cabin_Details> cabin_detailsList = (new Gson()).fromJson(teacherJson,new TypeToken<List<Cabin_Details>>(){}.getType());
            if (cabin_detailsList.size() > 0) {
                for (int i=0;i<cabin_detailsList.size();i++) {
                    try {
                        Cabin_Details editedTeacher= cabin_detailsList.get(i);
                        String name= editedTeacher.name.replace(".","");
                        database.child("custom").child(name).setValue(editedTeacher.cabin);
                        cabin_detailsList.remove(editedTeacher);
                    }
                    catch (Exception e){
                        Log.d("Request",e.getMessage());
                        //break;
                    }
                }
                put(context,toUpdate,(new Gson()).toJson(cabin_detailsList));
            }
        }
    }

    public static void attachFirebaseListener(final Context context){
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("dataVersion").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot rawDataVersion) {
                Log.d("ischanged", rawDataVersion.getValue().toString());
                int mydataVersion = get(context, dataVersion, 0);
                Log.d("myDataVersion", String.valueOf(mydataVersion));
                int DataVersion = Integer.parseInt(rawDataVersion.getValue().toString());
                if(DataVersion>mydataVersion){
                    put(context, dataVersion, DataVersion);
                    Log.d("Fetching","Fetching from database");
                    database.child("teachers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            vClass.teachers.clear();
                            for (DataSnapshot teacher : dataSnapshot.getChildren()) {
                                try {
                                    teacher newTeacher = teacher.getValue(teacher.class);
                                    vClass.teachers.add(newTeacher);//editor.putString("teachers",teacherJsonTest);
                                } catch (Exception e) {
                                    //DO NOT ADD THE CHANGE REQUESTED TEACHER DETAILS
                                }
                            }
                            String teacherJsonTest = (new Gson()).toJson(vClass.teachers);
                            put(context, teachers, teacherJsonTest);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    database.child("Holidays").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            vClass.holidays.clear();
                            //FETCH HOLIDAYS
                            for (DataSnapshot holiday : dataSnapshot.getChildren()) {
                                String dateString = holiday.getValue().toString();
                                Calendar c = Calendar.getInstance();
                                c.set(Integer.parseInt(dateString.substring(6)), Integer.parseInt(dateString.substring(3, 5)) - 1, Integer.parseInt(dateString.substring(0, 2)));
                                vClass.holidays.add(new holiday(c, holiday.getKey()));
                            }
                            Gson serializer = new Gson();
                            String holidayJson = serializer.toJson(vClass.holidays);
                            put(context, holidays, holidayJson);//holidays.putString("holidays",holidayJson);
                            updateWidget(context);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }  //REQUEST TO DATABASE FOR CHANGE IN TEACHERS

    static void updateWidget(Context context){
        (new widgetServiceReceiver()).onReceive(context,(new Intent(context,widgetServiceReceiver.class)));
    }  //UPDATE THE WIDGET TO SHOW TODAYS SCHEDULE
}
