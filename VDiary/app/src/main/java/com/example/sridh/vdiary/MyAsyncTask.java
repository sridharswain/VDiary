package com.example.sridh.vdiary;

import android.os.AsyncTask;

/**
 * Created by Sparsha Saha on 9/15/2016.
 */
public class MyAsyncTask extends AsyncTask<Void,Void,Void> {
    //diaryDash d;
    public MyAsyncTask(/*diaryDash f*/)
    {
        //d=f;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //d.changeAct();
        super.onPostExecute(aVoid);

    }
}
