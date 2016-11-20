package com.ontherunvaro.onoclient.activities;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Toast;

import com.ontherunvaro.onoclient.R;
import com.ontherunvaro.onoclient.util.ConfigUtil;
import com.ontherunvaro.onoclient.util.ConfigUtil.ConfigKey;
import com.ontherunvaro.onoclient.util.JavascriptFunctions;
import com.ontherunvaro.onoclient.util.OnoURL;
import com.ontherunvaro.onoclient.util.OnoURL.OnoPage;
import com.ontherunvaro.onoclient.util.WebViewUtils;

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
        if (savedInstanceState == null)
            setupWebView();
        else
            webView.restoreState(savedInstanceState);
    }

    private void setupWebView() {
        final String startURL = OnoURL.builder().withPage(OnoPage.CLIENT_AREA).toString();

        webView = (WebView) findViewById(R.id.main_webview);

        Log.d(TAG, "onCreate: loading URL " + startURL);
        webView.setWebViewClient(new MONOWebClient());
        webView.setWebChromeClient(new WebChromeClient());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        webView.getSettings().setJavaScriptEnabled(true);


        webView.loadUrl(startURL);
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            CookieManager.setAcceptFileSchemeCookies(true);
        } else {
            CookieSyncManager.getInstance().startSync();
        }

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // options menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String url = webView.getUrl();
        if (url == null || !url.contains(OnoPage.LOGIN.toString())) {
            menu.removeItem(R.id.menuitem_paste_password);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_paste_password:
                Toast.makeText(this, R.string.toast_inserting_credentials, Toast.LENGTH_SHORT).show();
                String js = String.format(JavascriptFunctions.INSERT_USERNAME, ConfigUtil.getProp(ConfigKey.USERNAME));
                js += String.format(JavascriptFunctions.INSERT_PASSWORD, ConfigUtil.getProp(ConfigKey.PASSWORD));
                WebViewUtils.loadJavaScript(webView, js);
                Log.d(TAG, "onOptionsItemSelected: credentials inserted");
                return true;
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        webView.restoreState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }


    // helper classes
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
            //sync cookies
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().flush();
            } else {
                CookieSyncManager.getInstance().sync();
            }
            //check menu items
            invalidateOptionsMenu();
            //hide loading dialog
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
