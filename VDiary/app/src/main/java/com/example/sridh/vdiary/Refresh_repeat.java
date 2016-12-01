package com.example.sridh.vdiary;

import android.os.AsyncTask;

/**
 * Created by Sparsha Saha on 10/23/2016.
 */
public class Refresh_repeat extends AsyncTask<Void,Void,Void> {
    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(3*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        schedule.i=0;
        super.onPostExecute(aVoid);
    }
}
