package com.example.sridh.vdiary;

import android.content.Intent;

import android.widget.RemoteViewsService;

/**
 * Created by sridh on 10/25/2016.
 */

 public class widgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return(new widgetListFactory(this.getApplicationContext(),intent));
    }

}
