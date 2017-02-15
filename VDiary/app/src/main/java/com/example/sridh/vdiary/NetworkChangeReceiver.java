package com.example.sridh.vdiary;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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
        getFromFirebase(context);
    }

    void updateWidget(){
        (new widgetServiceReceiver()).onReceive(context,(new Intent(context,widgetServiceReceiver.class)));
    }

    void getFromFirebase(final Context context){
        Firebase.setAndroidContext(context);
        final Firebase database= new Firebase(vClass.FIREBASE_URL);
        requestToDatabase(database,context);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //CHECK IF CHANGES IN DATABASE
                DataSnapshot rawDataVersion=dataSnapshot.child("dataVersion");
                Log.d("ischanged",rawDataVersion.getValue().toString());
                int mydataVersion = get(context,dataVersion,0);
                Log.d("myDataVersion",String.valueOf(mydataVersion));
                int DataVersion=Integer.parseInt(rawDataVersion.getValue().toString());
                if(DataVersion>mydataVersion){
                    Log.d("Fetching","Fetching");
                    vClass.teachers.clear();
                    //FETCH TEACHERS
                    DataSnapshot teachersData=dataSnapshot.child("teachers");
                    for(DataSnapshot snapshot:teachersData.getChildren()){
                        try {
                            teacher newTeacher = snapshot.getValue(teacher.class);
                            vClass.teachers.add(newTeacher);
                        }
                        catch (Exception e){
                            //DO NOT ADD THE CHANGE REQUESTED TEACHER DETAILS
                        }
                    }
                    String teacherJsonTest=(new Gson()).toJson(vClass.teachers);
                    put(context,teachers,teacherJsonTest);//editor.putString("teachers",teacherJsonTest);

                    vClass.holidays.clear();
                    //FETCH HOLIDAYS
                    DataSnapshot holiday=dataSnapshot.child("Holidays");
                    for (DataSnapshot snapshot : holiday.getChildren()){
                        String dateString = snapshot.getValue().toString();
                        Calendar c = Calendar.getInstance();
                        c.set(Integer.parseInt(dateString.substring(6)),Integer.parseInt(dateString.substring(3,5))-1,Integer.parseInt(dateString.substring(0,2)));
                        vClass.holidays.add(new holiday(c,snapshot.getKey()));
                    }
                    Gson serializer = new Gson();
                    String holidayJson = serializer.toJson(vClass.holidays);
                    put(context,holidays,holidayJson);//holidays.putString("holidays",holidayJson);
                    updateWidget();
                    put(context,dataVersion,DataVersion);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //DO NOTHING
            }
        });
    }  //GET THE CABIN DETAILS OF TEACHERS FORM FIREBASE DATABASE
    void requestToDatabase(Firebase database,Context context){
        //REQUEST TO DATABASE
        String teacherJson = get(context,toUpdate,null);//teacherPrefs.getString("toUpdate",null);
        if(teacherJson!=null){
            List<Cabin_Details> cabin_detailsList = (new Gson()).fromJson(teacherJson,new TypeToken<List<Cabin_Details>>(){}.getType());
            if (cabin_detailsList.size() > 0) {
                for (Cabin_Details editedTeacher : cabin_detailsList) {
                    try {
                        database.child("custom").child(editedTeacher.name).setValue(editedTeacher.cabin);
                        cabin_detailsList.remove(editedTeacher);
                    }
                    catch (Exception e){
                        break;
                    }
                }
                put(context,toUpdate,(new Gson()).toJson(cabin_detailsList));
            }
        }
    }
}
