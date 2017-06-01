/*
 * mONO is a free app for a telephony provider's client area.
 * Copyright (C) 2017 Álvaro Brey Vilas <alvaro.brv@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0>.
 */

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