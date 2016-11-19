package com.ontherunvaro.onoclient;

import android.app.Application;
import android.util.Log;

import com.ontherunvaro.onoclient.util.ConfigUtil;

/**
 * Created by varo on 19/11/16.
 */

public class MONOApp extends Application {

    private static final String TAG = "MONOApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: MONO started");
        //initialize config
        ConfigUtil.load(this);
    }
}
