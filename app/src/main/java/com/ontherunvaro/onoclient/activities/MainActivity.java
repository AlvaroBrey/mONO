package com.ontherunvaro.onoclient.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.ontherunvaro.onoclient.util.WebViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    @BindView(R.id.main_webview)
    WebView webView;

    private ProgressDialog progressDialog;

    public final static String EXTRA_USERNAME = "main_extra_username";
    public final static String EXTRA_PASSWORD = "main_extra_password";

    private boolean insertCredentials = false;

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

    private void handleIntent() {
        final String user = getIntent().getStringExtra(EXTRA_USERNAME);
        final String pass = getIntent().getStringExtra(EXTRA_PASSWORD);

        if (user != null && pass != null) {
            final String loginAction = OnoURL.builder().withPage(OnoPage.LOGIN).toString() + "/";
            insertCredentials = true;
            setupWebView();
            webView.loadUrl(loginAction);
        } else {
            Log.e(TAG, "handleIntent: pasword or user is null. Pass=" + pass + ", user=" + user);
            Intent k = new Intent(this, LoginActivity.class);
            startActivity(k);
            finish();
        }

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

            if (insertCredentials) {
                Log.d(TAG, "onPageFinished: Inserting credentials...");
                final String user = getIntent().getStringExtra(EXTRA_USERNAME);
                final String pass = getIntent().getStringExtra(EXTRA_PASSWORD);
                String js = String.format(JavascriptFunctions.INSERT_PASSWORD, pass);
                js += String.format(JavascriptFunctions.INSERT_USERNAME, user);
                WebViewUtils.loadJavaScript(webView, js);
                insertCredentials = false;
            }

            //sync cookies
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().flush();
            } else {
                CookieSyncManager.getInstance().sync();
            }

            //check menu items
            invalidateOptionsMenu();
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
