package com.example.sridh.vdiary;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Sparsha Saha on 10/23/2016.
 */
class Alarm_refresh extends WakefulBroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {
        schedule.i=0;
        Log.d("Sparsha","Success");
        schedule.fortoast();
    }
}
