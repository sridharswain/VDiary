package com.example.sridh.vdiary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.firebase.client.Firebase;

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
