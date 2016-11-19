package com.ontherunvaro.onoclient.util;

import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by varo on 13/11/16.
 */

public class WebViewUtils {

    private final static String TAG = "WebViewUtils";

    public static void loadJavaScript(WebView view, String js) {
        String jscode = "(function(){" +
                js +
                "})()";
        Log.d(TAG, "loadJavaScript: loading JS: " + jscode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.evaluateJavascript(jscode, null);
        } else {
            view.loadUrl("javascript:" + jscode);
        }

    }
}
