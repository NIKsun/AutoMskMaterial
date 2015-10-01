package com.example.material_model_automsk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.rey.material.widget.ProgressView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;


public class CarWebPage extends Activity{
    private WebView mWebView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_web_page);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setInitialScale(50);
        mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        mWebView.getSettings().setUseWideViewPort(true);

        mWebView.clearCache(true);


        class MyWebViewClient extends WebViewClient {
            private final long LOADING_ERROR_TIMEOUT = TimeUnit.SECONDS.toMillis(45);

            // WebView instance is kept in WeakReference because of mPageLoadingTimeoutHandlerTask
            private WeakReference<WebView> mReference;
            private boolean mLoadingFinished = false;
            private boolean mLoadingError = false;
            private long mLoadingStartTime = 0;

            // Helps to handle case when onReceivedError get called before onPageStarted
            // Problem cached on Nexus 7; Android 5
            private String mOnErrorUrl;

            // Helps to know what page is loading in the moment
            // Allows check url to prevent onReceivedError/onPageFinished calling for wrong url
            // Helps to prevent double call of onPageStarted
            // These problems cached on many devices
            private String mUrl;

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String url) {
                if (mUrl != null && !mLoadingError) {
                    mLoadingError = true;
                } else {
                    mOnErrorUrl = removeLastSlash(url);
                }
            }

            // We need startsWith because some extra characters like ? or # are added to the url occasionally
            // However it could cause a problem if your server load similar links, so fix it if necessary
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                url = removeLastSlash(url);
                if (!startsWith(url, mUrl) && !mLoadingFinished) {
                    mUrl = null;
                    onPageStarted(view, url, null);
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favIcon) {
                url = removeLastSlash(url);
                if (startsWith(url, mOnErrorUrl)) {
                    mUrl = url;
                    mLoadingError = true;
                    mLoadingFinished = false;
                    onPageFinished(view, url);
                }
                if (mUrl == null) {
                    mUrl = url;
                    mLoadingError = false;
                    mLoadingFinished = false;
                    mLoadingStartTime = System.currentTimeMillis();
                    view.removeCallbacks(mPageLoadingTimeoutHandlerTask);
                    view.postDelayed(mPageLoadingTimeoutHandlerTask, LOADING_ERROR_TIMEOUT);
                    mReference = new WeakReference<>(view);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                url = removeLastSlash(url);
                if (startsWith(url, mUrl) && !mLoadingFinished) {
                    mLoadingFinished = true;
                    view.removeCallbacks(mPageLoadingTimeoutHandlerTask);

                    long loadingTime = System.currentTimeMillis() - mLoadingStartTime;
                    if (mLoadingError)  {
                        Toast.makeText(CarWebPage.this, "Ошибка загрузки страницы, время загрузки: " + loadingTime, Toast.LENGTH_SHORT).show();
                    }

                    mOnErrorUrl = null;
                    mUrl = null;
                } else if (mUrl == null) {
                    // On some devices (e.g. Lg Nexus 5) onPageStarted sometimes not called at all
                    // The only way I found to fix it is to reset WebViewClient
                    view.setWebViewClient(new MyWebViewClient());
                    mLoadingFinished = true;
                }
                if(mLoadingFinished) {
                    ProgressView pb = (ProgressView) findViewById(R.id.progress_web_page);
                    pb.stop();
                }

            }

            private String removeLastSlash(String url) {
                while (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                return url;
            }

            // We need startsWith because some extra characters like ? or # are added to the url occasionally
            // However it could cause a problem if your server load similar links, so fix it if necessary
            private boolean startsWith(String str, String prefix) {
                return str != null && prefix != null && str.startsWith(prefix);
            }

            private final Runnable mPageLoadingTimeoutHandlerTask = new Runnable() {
                @Override
                public void run() {
                    mUrl = null;
                    mLoadingFinished = true;
                    long loadingTime = System.currentTimeMillis() - mLoadingStartTime;
                    if (mReference != null) {
                        WebView webView = mReference.get();
                        if (webView != null) {
                            webView.stopLoading();
                        }
                    }
                }
            };
        }

        mWebView.setWebViewClient(new MyWebViewClient());
        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {

        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
