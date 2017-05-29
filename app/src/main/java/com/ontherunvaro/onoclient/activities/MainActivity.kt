package com.ontherunvaro.onoclient.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import com.ontherunvaro.onoclient.R
import com.ontherunvaro.onoclient.util.JavascriptFunctions
import com.ontherunvaro.onoclient.util.OnoURL
import com.ontherunvaro.onoclient.util.OnoURL.OnoPage
import com.ontherunvaro.onoclient.util.PrefConstants
import com.ontherunvaro.onoclient.util.loadJavaScript
import kotlinx.android.synthetic.main.activity_main.*

@SuppressLint("SetJavaScriptEnabled")
class MainActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null
    private var doLogin = false

    override fun onBackPressed() {
        if (main_webview.canGoBack()) {
            main_webview.goBack()
        } else {
            super.onBackPressed()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuitem_about -> {
                val i = Intent(this, AboutActivity::class.java)
                startActivityForResult(i, 0)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        main_webview.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle, persistentState: PersistableBundle) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        main_webview.restoreState(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (savedInstanceState == null) {
            handleIntent()
        } else {
            main_webview.restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        main_webview.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        main_webview.restoreState(savedInstanceState)
    }

    private fun handleIntent() {
        val user = intent.getStringExtra(EXTRA_USERNAME)
        val pass = intent.getStringExtra(EXTRA_PASSWORD)

        setupWebView()
        if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pass)) {
            val loginAction = OnoURL.builder().withPage(OnoPage.LOGIN).toString() + "/"
            doLogin = true
            main_webview.loadUrl(loginAction)
        } else {
            main_webview.loadUrl(OnoURL.builder().withPage(OnoPage.CLIENT_AREA).toString() + "/")
        }

    }

    private fun setupWebView() {

        Log.d(TAG, "Setting up webview...")
        main_webview.setWebViewClient(MONOWebClient())
        main_webview.setWebChromeClient(WebChromeClient())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            main_webview.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        main_webview.settings.javaScriptEnabled = true

        CookieManager.getInstance().setAcceptCookie(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(main_webview, true)
            CookieManager.setAcceptFileSchemeCookies(true)
        } else {
            CookieSyncManager.getInstance().startSync()
        }

    }

    private fun showLoading() {
        if (progressDialog == null || !progressDialog!!.isShowing) {
            main_webview.visibility = View.INVISIBLE
            progressDialog = ProgressDialog.show(this, getString(R.string.dialog_loading_title), getString(R.string.dialog_loading_message))
            progressDialog!!.setCancelable(false)
        }
    }

    private fun hideLoading() {
        if (progressDialog != null) {
            main_webview.visibility = View.VISIBLE
            progressDialog!!.dismiss()
            progressDialog = null
        }
    }

    // helper classes
    internal inner class MONOWebClient : WebViewClient() {

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            showLoading()
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView, url: String) {

            val prefs = getSharedPreferences(PrefConstants.Files.MAIN_PREFS, Context.MODE_PRIVATE)
            if (doLogin) {
                Log.d(TAG, "onPageFinished: Inserting credentials...")
                val user = intent.getStringExtra(EXTRA_USERNAME)
                val pass = intent.getStringExtra(EXTRA_PASSWORD)
                var js = String.format(JavascriptFunctions.INSERT_PASSWORD, pass)
                js += String.format(JavascriptFunctions.INSERT_USERNAME, user)
                js += JavascriptFunctions.PRESS_LOGIN_BUTTON
                main_webview.loadJavaScript(js)
                doLogin = false
                prefs.edit().putBoolean(PrefConstants.Keys.LOGGED_IN, true).apply()
            } else if (prefs.getBoolean(PrefConstants.Keys.LOGGED_IN, false) && main_webview.url.contains(OnoPage.LOGIN.toString())) {
                //client area returns to login page without us asking for it. Session has expired.
                Log.d(TAG, "onPageFinished: Login needed. Forwarding to LoginActivity")
                prefs.edit().putBoolean(PrefConstants.Keys.LOGGED_IN, false).apply()
                val i = Intent(this@MainActivity, LoginActivity::class.java)
                this@MainActivity.startActivity(i)
                this@MainActivity.finish()
            }


            //sync cookies
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().flush()
            } else {
                CookieSyncManager.getInstance().sync()
            }

            //hide loading dialog
            hideLoading()
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            Log.e(TAG, "onReceivedError: " + error.toString())
        }

        override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
            Log.e(TAG, "onReceivedHttpError: " + errorResponse.toString())
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            Log.e(TAG, "onReceivedSslError: " + error.toString())
        }


        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return !(url.contains(OnoURL.BASE_URL) || url.contains("javascript"))
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            return !(url.contains(OnoURL.BASE_URL) || url.contains("javascript"))
        }
    }

    companion object {

        val EXTRA_USERNAME = "main_extra_username"
        val EXTRA_PASSWORD = "main_extra_password"
        private val TAG = "MainActivity"
    }


}
