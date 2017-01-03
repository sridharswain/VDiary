package com.example.sridh.vdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by sridh on 10/25/2016.
 */

 public class widgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return(new widgetListFactory(this.getApplicationContext(),intent));
    }

}
