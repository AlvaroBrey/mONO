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
import android.webkit.WebView

private val TAG = "WebViewUtils"

/**
 * Loads the JavaScript code contained in [additional] into this WebView.
 *
 * @param firstArg a [Pair] of  a [JavascriptFunctions] and the [String]s that serve as arguments for it
 * @param additional [Pair]s of [JavascriptFunctions] and the [String]s that serve as arguments for them (optional)
 */
fun WebView.loadJavaScript(firstArg: Pair<JavascriptFunctions, Array<String>?>, vararg additional: Pair<JavascriptFunctions, Array<String>?>) {

    var inner = firstArg.first.format(firstArg.second)
    inner = additional.fold(inner, {
        str, (function, args) ->
        str + function.format(args)
    })

    val wrappedCode = "(function(){$inner})()"

    LogUtil.d(TAG, "loadJavaScript: loading JS: $wrappedCode")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        evaluateJavascript(wrappedCode, null)
    } else {
        loadUrl("javascript: $wrappedCode")
    }
}