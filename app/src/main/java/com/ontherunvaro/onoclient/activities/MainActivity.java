package com.ontherunvaro.onoclient.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ontherunvaro.onoclient.R;
import com.ontherunvaro.onoclient.util.OnoURL;

public class MainActivity extends AppCompatActivity {


    private WebView webView;

    private ProgressDialog progressDialog;
    private Context context;

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        setupWebView();
    }

    private void setupWebView() {
        final String startURL = OnoURL.builder().withPage(OnoURL.OnoPage.CLIENT_AREA).toString();

        webView = (WebView) findViewById(R.id.mainWebView);

        Log.d(TAG, "onCreate: loading URL " + startURL);
        webView.setWebViewClient(new MONOWebClient());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(startURL);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    class MONOWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (progressDialog == null) {
                progressDialog = ProgressDialog.show(context, getString(R.string.dialog_loading_title), getString(R.string.dialog_loading_message));
                progressDialog.setCancelable(false);
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
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
            return !url.contains(OnoURL.getBaseUrl());
        }
    }


}
