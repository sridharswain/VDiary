package com.example.sridh.vdiary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Sparsha Saha on 1/15/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    Firebase database;
    @Override
    public void onReceive(final Context context, Intent intent) {
        Firebase.setAndroidContext(context);
        database= new Firebase(vClass.FIREBASE_URL);
        scrapper.getHolidays(database,context);
    }
}
