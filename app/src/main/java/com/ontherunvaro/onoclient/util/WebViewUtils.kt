package com.ontherunvaro.onoclient.util

import android.os.Build
import android.util.Log
import android.webkit.WebView

/**
 * Created by varo on 5/25/17.
 */


private val TAG = "WebViewUtils"

/**
 * Loads the JavaScript code contained in [functions] into this WebView.
 *
 * @param functions [Pair]s of [JavascriptFunctions] and the [String]s that serve as arguments for them.
 */
fun WebView.loadJavaScript(vararg functions: Pair<JavascriptFunctions, Array<String>?>) {

    val inner = functions.fold("", {
        str, (function, args) ->
        if (function.argn > 0) {
            when {
                args == null || args.isEmpty() -> throw IllegalArgumentException("Args expected for JS function")
                else -> str + function.function.format(*args)
            }
        } else str + function.function
    })

    val wrappedCode = "(function(){$inner})()"

    Log.d(TAG, "loadJavaScript: loading JS: $wrappedCode")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        evaluateJavascript(wrappedCode, null)
    } else {
        loadUrl("javascript: $wrappedCode")
    }
}