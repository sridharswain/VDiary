package com.example.sridh.vdiary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

/**
 * Created by Sparsha Saha on 1/4/2017.
 */

public class service extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Vibrator vib=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        if(vib.hasVibrator()) {
            vib.vibrate(400);
        }
    }
}
