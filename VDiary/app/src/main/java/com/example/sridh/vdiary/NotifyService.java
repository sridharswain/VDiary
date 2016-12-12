package com.example.sridh.vdiary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Sparsha Saha on 12/12/2016.
 */

public class NotifyService extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Notification_Holder notifholder;
        Type t=new TypeToken<Notification_Holder>(){}.getType();

        Gson js=new Gson();
        notifholder=js.fromJson(intent.getStringExtra("one"),t);
        Toast.makeText(context, notifholder.title+"  "+notifholder.content, Toast.LENGTH_SHORT).show();
        Notification_Creator notifcreator=new Notification_Creator(notifholder.title,notifholder.content,notifholder.cal,context);
        notifcreator.create_notification();
    }
}
