package com.example.harry.nc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.RemoteViews;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.List;

/**
 * The entry point to the BasicNotification sample.
 */
public class MainActivity extends Activity implements OnClickListener {


    private  Client client;
    /*
    * Contruct the Activity
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        client = new Client(this);
        new Thread(networkTask).start();

    }
    /*
    * Run a new thread
    * */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            client.run();
        }
    };
    private void initView() {


    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        System.exit(0);
        super.onBackPressed();
    }

}