package com.ontherunvaro.onoclient.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ontherunvaro.onoclient.R;
import com.ontherunvaro.onoclient.util.JavascriptFunctions;
import com.ontherunvaro.onoclient.util.OnoURL;
import com.ontherunvaro.onoclient.util.OnoURL.OnoPage;
import com.ontherunvaro.onoclient.util.PrefConstants;
import com.ontherunvaro.onoclient.util.WebViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_USERNAME = "main_extra_username";
    public final static String EXTRA_PASSWORD = "main_extra_password";
    private final static String TAG = "MainActivity";
    @BindView(R.id.main_webview)
    WebView webView;
    private ProgressDialog progressDialog;
    private boolean doLogin = false;

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_about:
                Intent i = new Intent(this, AboutActivity.class);
                startActivityForResult(i, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        webView.saveState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        webView.restoreState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        if (savedInstanceState == null) {
            handleIntent();
        } else {
            webView.restoreState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    private void handleIntent() {
        final String user = getIntent().getStringExtra(EXTRA_USERNAME);
        final String pass = getIntent().getStringExtra(EXTRA_PASSWORD);

        setupWebView();
        if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pass)) {
            final String loginAction = OnoURL.builder().withPage(OnoPage.LOGIN).toString() + "/";
            doLogin = true;
            webView.loadUrl(loginAction);
        } else {
            webView.loadUrl(OnoURL.builder().withPage(OnoPage.CLIENT_AREA).toString() + "/");
        }

    }

    @SuppressWarnings("deprecation")
    private void setupWebView() {

        Log.d(TAG, "Setting up webview...");
        webView.setWebViewClient(new MONOWebClient());
        webView.setWebChromeClient(new WebChromeClient());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.getSettings().setJavaScriptEnabled(true);

        CookieManager.getInstance().setAcceptCookie(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            CookieManager.setAcceptFileSchemeCookies(true);
        } else {
            CookieSyncManager.getInstance().startSync();
        }

    }

    private void showLoading() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            webView.setVisibility(View.INVISIBLE);
            progressDialog = ProgressDialog.show(this, getString(R.string.dialog_loading_title), getString(R.string.dialog_loading_message));
            progressDialog.setCancelable(false);
        }
    }

    private void hideLoading() {
        if (progressDialog != null) {
            webView.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    // helper classes
    @SuppressWarnings("deprecation")
    class MONOWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            showLoading();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            SharedPreferences prefs = getSharedPreferences(PrefConstants.Files.MAIN_PREFS, MODE_PRIVATE);
            if (doLogin) {
                Log.d(TAG, "onPageFinished: Inserting credentials...");
                final String user = getIntent().getStringExtra(EXTRA_USERNAME);
                final String pass = getIntent().getStringExtra(EXTRA_PASSWORD);
                String js = String.format(JavascriptFunctions.INSERT_PASSWORD, pass);
                js += String.format(JavascriptFunctions.INSERT_USERNAME, user);
                js += JavascriptFunctions.PRESS_LOGIN_BUTTON;
                WebViewUtils.loadJavaScript(webView, js);
                doLogin = false;
                prefs.edit().putBoolean(PrefConstants.Keys.LOGGED_IN, true).apply();
            } else if (prefs.getBoolean(PrefConstants.Keys.LOGGED_IN, false) && webView.getUrl().contains(OnoPage.LOGIN.toString())) {
                //client area returns to login page without us asking for it. Session has expired.
                Log.d(TAG, "onPageFinished: Login needed. Forwarding to LoginActivity");
                prefs.edit().putBoolean(PrefConstants.Keys.LOGGED_IN, false).apply();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(i);
                MainActivity.this.finish();
            }


            //sync cookies
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().flush();
            } else {
                CookieSyncManager.getInstance().sync();
            }

            //hide loading dialog
            hideLoading();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.e(TAG, "onReceivedError: " + error.toString());
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            Log.e(TAG, "onReceivedHttpError: " + errorResponse.toString());
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.e(TAG, "onReceivedSslError: " + error.toString());
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return !(url.contains(OnoURL.getBaseUrl()) || url.contains("javascript"));
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return !(url.contains(OnoURL.getBaseUrl()) || url.contains("javascript"));
        }
    }


}
