package com.develop.autorus;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.ProgressView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;


public class CarWebPage extends Activity{
    private WebView mWebView;
    private Toast toastAdd;
    Tracker mTracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_web_page);
        String url = getIntent().getStringExtra("url");

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Web Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if(!getIntent().getBooleanExtra("isFromFavorites",true)) {
            final DbHelper dbHelper = new DbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query("favorites", new String[]{"href"}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex("href");
                do {
                    if (url.equals(cursor.getString(index)))
                        break;
                } while (cursor.moveToNext());
            }

            toastAdd = Toast.makeText(this, "Авто добавлено в избранное", Toast.LENGTH_SHORT);
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(CarWebPage.this);
            if(sPref.getBoolean("TAG_BUY_ALL", false) || sPref.getBoolean("TAG_FAVORITES", false) || cursor.getCount() <= 11) {
                if (!cursor.isLast() && !cursor.moveToNext()) {
                    final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_favorites);
                    ThemeManager.init(this, 2, 0, null);
                    Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_translate_top);
                    fab.startAnimation(anim);
                    fab.setVisibility(View.VISIBLE);
                    fab.setIcon(getResources().getDrawable(R.drawable.ic_star_border_white_48dp), true);
                    fab.setBackgroundColor(getResources().getColor(R.color.orange));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("message", getIntent().getStringExtra("message"));
                            cv.put("href", getIntent().getStringExtra("url"));
                            cv.put("image", getIntent().getStringExtra("image"));
                            cv.put("dateTime", getIntent().getStringExtra("dateTime"));
                            db.insert("favorites", null, cv);
                            toastAdd.show();
                            db.close();

                            Animation anim = AnimationUtils.loadAnimation(CarWebPage.this, R.anim.anim_translate_buttom);
                            fab.startAnimation(anim);
                            fab.setVisibility(View.INVISIBLE);
                            mTracker.send(new HitBuilders.EventBuilder().setCategory("Favorites").setAction("click to star").setValue(1).build());
                        }
                    });
                }
            }
            db.close();
        }
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
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }else if(url.startsWith("http:") || url.startsWith("https:")) {
                    url = removeLastSlash(url);
                    if (!startsWith(url, mUrl) && !mLoadingFinished) {
                        mUrl = null;
                        onPageStarted(view, url, null);
                    }
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
        if (Build.VERSION.SDK_INT>=21){
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebView.loadUrl(url);
        if(url.contains("drom.ru"))
            mTracker.send(new HitBuilders.EventBuilder().setCategory("Web page").setAction("Drom.ru").setValue(1).build());
        else if(url.contains("avito.ru"))
            mTracker.send(new HitBuilders.EventBuilder().setCategory("Web page").setAction("Avito.ru").setValue(1).build());
        else
            mTracker.send(new HitBuilders.EventBuilder().setCategory("Web page").setAction("Auto.ru").setValue(1).build());

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
