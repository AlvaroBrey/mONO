package com.ontherunvaro.onoclient.util

import android.os.Build
import android.util.Log
import android.webkit.WebView

/**
 * Created by varo on 5/25/17.
 */


private val TAG = "WebViewUtils"

/**
 * Loads the JavaScript code contained in [jsCode] into this WebView.
 *
 * @param jsCode the JavaScript to load
 */
fun WebView.loadJavaScript(jsCode: String) {
    val jscode = "(function(){" +
            jsCode +
            "})()"
    Log.d(TAG, "loadJavaScript: loading JS: " + jscode)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        evaluateJavascript(jscode, null)
    } else {
        loadUrl("javascript:" + jscode)
    }
}