package com.ontherunvaro.onoclient.util

import android.util.Log

import com.ontherunvaro.onoclient.BuildConfig


object LogUtil {

    private val ENABLE_LOGS = !BuildConfig.BUILD_TYPE.equals("release", ignoreCase = true)


    fun d(tag: String, message: String) {
        if (ENABLE_LOGS) {
            Log.d(tag, message)
        }
    }

    fun d(tag: String, message: String, cause: Throwable) {
        if (ENABLE_LOGS) {
            Log.d(tag, message, cause)
        }
    }

    fun v(tag: String, message: String) {
        if (ENABLE_LOGS) {
            Log.v(tag, message)
        }
    }

    fun v(tag: String, message: String, cause: Throwable) {
        if (ENABLE_LOGS) {
            Log.v(tag, message, cause)
        }
    }

    fun i(tag: String, message: String) {
        if (ENABLE_LOGS) {
            Log.i(tag, message)
        }
    }

    fun i(tag: String, message: String, cause: Throwable) {
        if (ENABLE_LOGS) {
            Log.i(tag, message, cause)
        }
    }

    fun w(tag: String, message: String) {
        Log.w(tag, message)
    }

    fun w(tag: String, message: String, cause: Throwable) {
        Log.w(tag, message, cause)
    }

    fun wtf(tag: String, message: String) {
        Log.wtf(tag, message)
    }

    fun wtf(tag: String, message: String, cause: Throwable) {
        Log.wtf(tag, message, cause)
    }

    fun e(tag: String, message: String) {
        Log.e(tag, message)
    }

    fun e(tag: String, message: String, cause: Throwable) {
        Log.e(tag, message, cause)
    }
}