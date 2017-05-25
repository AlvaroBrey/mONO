package com.ontherunvaro.onoclient

import android.app.Application
import android.util.Log

class MONOApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: mONO started")
    }

    companion object {
        private val TAG = "MONOApp"
    }
}
