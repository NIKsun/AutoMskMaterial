package com.example.material_model_automsk;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.Button;
import com.rey.material.widget.SnackBar;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SnackBar mSnackBar;
    private Button addMonitorButton;
    private Boolean isFirstLaunch = true, itemSelectFromTabLayout = false;
    private Toolbar mToolbar;
    private Fragment secondFragment;
    private SearchAndMonitorsFragment mainFragment;
    private Toast backToast = null;
    final String SAVED_TEXT_WITH_VERSION = "checkVersion";
    final String DO_NOT_REMIND = "DontRemind";
    private AlarmManager am;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        super.onCreate(savedInstanceState);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        if(pref.getBoolean("notificationIsActive",true)) {
            Intent checkIntent = new Intent(getApplicationContext(), MonitoringWork.class);
            Boolean alrarmIsActive = false;
            if (PendingIntent.getService(getApplicationContext(), 0, checkIntent, PendingIntent.FLAG_NO_CREATE) != null)
                alrarmIsActive = true;
            am = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (!alrarmIsActive) {
                Intent serviceIntent = new Intent(getApplicationContext(), MonitoringWork.class);
                PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, 0);

                int period = pref.getInt("numberOfActiveMonitors", 0) * 180000;
                Toast.makeText(this, "Текущий период: " + period, Toast.LENGTH_SHORT).show();

                if(period != 0)
                    am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + period, period, pIntent);
            }
        }

        String themeName = pref.getString("theme", "1");
        View decorView = getWindow().getDecorView();

        if (themeName.equals("1")) {
            setTheme(R.style.AppTheme);
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window statusBar = getWindow();
                statusBar.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                statusBar.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                statusBar.setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
            }
        }
        else if (themeName.equals("2")) {
            setTheme(R.style.AppTheme2);
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window statusBar = getWindow();
                statusBar.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                statusBar.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                statusBar.setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor2));
            }
        }
        ThemeManager.init(this, 2, 0, null);

        if(isFirstLaunch) {
            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            mainFragment = SearchAndMonitorsFragment.newInstance(1);
            fTrans.add(R.id.container, mainFragment, "MAIN").commit();
        }
        backToast = Toast.makeText(this,"Нажмите еще раз для выхода",Toast.LENGTH_SHORT);
        setContentView(R.layout.main_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        mSnackBar = (SnackBar)findViewById(R.id.main_sn);

        addMonitorButton = (Button)findViewById(R.id.toolbar_add_monitor_button);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        Thread threadAvito = new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void run() {
                Document doc;
                SharedPreferences sPref;
                try {
                    doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.develop.searchmycarandroid").userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; ru-RU; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").timeout(12000).get();

                    PackageManager packageManager;
                    PackageInfo packageInfo;
                    packageManager=getPackageManager();

                    packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
                    Element mainElems = doc.select("#body-content > div > div > div.main-content > div.details-wrapper.apps-secondary-color > div > div.details-section-contents > div:nth-child(4) > div.content").first();

                    if (!packageInfo.versionName.equals(mainElems.text()))
                    {
                        sPref = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putBoolean(SAVED_TEXT_WITH_VERSION, false);
                        ed.commit();
                    }
                    else
                    {
                        sPref = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putBoolean(SAVED_TEXT_WITH_VERSION, true);
                        ed.commit();

                    }
                    //SharedPreferences sPrefRemind;
                    //sPrefRemind = getPreferences(MODE_PRIVATE);
                    //sPrefRemind.edit().putBoolean(DO_NOT_REMIND, false).commit();
                }
                catch (HttpStatusException e)
                {
                    //bulAvito[0] =false;
                    return;
                }
                catch (IOException e)
                {
                    //connectionAvitoSuccess[0] = false;
                    return;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });


        SharedPreferences sPrefVersion;
        sPrefVersion = getPreferences(MODE_PRIVATE);
        Boolean isNewVersion;
        isNewVersion = sPrefVersion.getBoolean(SAVED_TEXT_WITH_VERSION, true);
        threadAvito.start();
        boolean remind=true;
        if (!isNewVersion)
        {

            Log.d("aaffa", "isNewVersion= "+isNewVersion);
            Log.d("aaffa", "не новая версия!!! Так записано. Возможно, поток еще не отработал");
            SharedPreferences sPref12;
            sPref12 = getPreferences(MODE_PRIVATE);
            String isNewVersion12;

            PackageManager packageManager;
            PackageInfo packageInfo;
            packageManager=getPackageManager();

            try {
                packageInfo=packageManager.getPackageInfo(getPackageName(), 0);
                isNewVersion12 = sPref12.getString("OldVersionName", packageInfo.versionName);

                if (!isNewVersion12.equals(packageInfo.versionName))
                {
                    Log.d("aaffa", "записанная в shared запись версии НЕ совпадает с действительной=" + isNewVersion12);

                    SharedPreferences sPref;
                    sPref = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean(SAVED_TEXT_WITH_VERSION, false);
                    ed.commit();

                    SharedPreferences sPrefRemind;
                    sPrefRemind = getPreferences(MODE_PRIVATE);
                    sPrefRemind.edit().putBoolean(DO_NOT_REMIND, false).commit();
                }
                else
                {
                    remind = false;
                    Log.d("aaffa", "записанная в shared запись версии  совпадает с действительной");
                }


                SharedPreferences sPrefRemind;
                sPrefRemind = getPreferences(MODE_PRIVATE);
                sPrefRemind.edit().putString("OldVersionName", packageInfo.versionName).commit();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            SharedPreferences sPrefRemind;
            sPrefRemind = getPreferences(MODE_PRIVATE);
            Boolean dontRemind;
            dontRemind = sPrefRemind.getBoolean(DO_NOT_REMIND, false);
            Log.d("aaffa", "dontRemind= "+dontRemind.toString());
            Log.d("aaffa", "remind= "+remind);

            if ((!dontRemind) && (!remind)) {
                AlertDialog.Builder ad;
                ad = new AlertDialog.Builder(this);
                ad.setTitle("Обновление");
                ad.setMessage("Вы хотите обновить приложение?");
                ad.setPositiveButton("Обновить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.develop.searchmycarandroid"));
                        startActivity(intent);
                    }
                });
                ad.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                    }
                });
                ad.setNeutralButton("Не напоминать", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        SharedPreferences sPrefRemind;
                        sPrefRemind = getPreferences(MODE_PRIVATE);
                        sPrefRemind.edit().putBoolean(DO_NOT_REMIND, true).commit();
                    }
                });
                ad.setCancelable(true);
                ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                    }
                });

                ad.show();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int numberOfCallingFragment = pref.getInt("NumberOfCallingFragment", -1);
        if(getIntent().hasExtra("isFromNotification") && getIntent().getBooleanExtra("isFromNotification",false))
            numberOfCallingFragment = 0;
        if(numberOfCallingFragment != -1) {
            if((mNavigationDrawerFragment.getCurrentItemSelected() == 0 && numberOfCallingFragment == 1) ||
                    (mNavigationDrawerFragment.getCurrentItemSelected() == 1 && numberOfCallingFragment == 0))
            {
                Waiter waiter = new Waiter();
                if (Build.VERSION.SDK_INT>=11)
                        waiter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, numberOfCallingFragment, 400);
                else
                {
                    onNavigationDrawerItemSelected(numberOfCallingFragment);
                    setNavigationDrawerItem(numberOfCallingFragment);

                }
            }
            else {
                onNavigationDrawerItemSelected(numberOfCallingFragment);
                setNavigationDrawerItem(numberOfCallingFragment);
            }
            pref.edit().remove("NumberOfCallingFragment").commit();
        }

        if(mNavigationDrawerFragment.getCurrentItemSelected() == 0)
            mainFragment.updateMonitorsFragment();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if(itemSelectFromTabLayout)
        {
            itemSelectFromTabLayout = false;
            return;
        }
        if(isFirstLaunch)
        {
            isFirstLaunch = false;
            return;
        }

        if(mSnackBar!=null)
            mSnackBar.dismiss();


        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        switch (position){
            case 0:
                mToolbar.setTitle("Авто Москва");
                mainFragment.setPage(0);
                if(secondFragment != null) {
                    fTrans.remove(secondFragment);
                    fTrans.show(mainFragment);
                }
                break;
            case 1:
                mToolbar.setTitle("Авто Москва");
                mainFragment.setPage(1);
                if(secondFragment != null) {
                    fTrans.remove(secondFragment);
                    fTrans.show(mainFragment);
                }
                break;
            case 2:
                mToolbar.setTitle("Избранное");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new FavoritesFragment();
                fTrans.add(R.id.container, secondFragment);
                break;
            case 3:
                mToolbar.setTitle("Настройки");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new SettingsFragment();
                fTrans.add(R.id.container, secondFragment);
                break;
            case 4:
                mToolbar.setTitle("Обратная связь");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new FeedbackFragment();
                fTrans.add(R.id.container, secondFragment);
                break;
            case 5:
                mToolbar.setTitle("Справка");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new ReferenceFragment();
                fTrans.add(R.id.container, secondFragment);
                break;

        }
        fTrans.commit();
    }

    public SnackBar getSnackBar(){
            return mSnackBar;
    }
    public Button getAddMonitorButton(){
        return addMonitorButton;
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else{
            if(backToast.getView().getWindowVisibility() == View.VISIBLE) {
                backToast.getView().setVisibility(View.INVISIBLE);
                super.onBackPressed();
            }
            else
                backToast.show();
        }
    }

    public void onClickHandlerHidden(View v){
        mainFragment.getSearchFragment().onClickHandlerHidden(v);
    }

    public void onClickClearSelection(View v){
        mainFragment.getSearchFragment().onClickClearSelection(v);
    }
    public void onClickMarkorModel(View v){
        mainFragment.getSearchFragment().onClickMarkorModel(v);
    }


    public void setNavigationDrawerItem(int itemNumber) {
        itemSelectFromTabLayout = true;
        mNavigationDrawerFragment.selectItem(itemNumber);
    }


    class Waiter extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(final Integer... params) {
            try {
                Thread.sleep(params[1]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params[0];
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(Integer values) {
            super.onPostExecute(values);
            onNavigationDrawerItemSelected(values);
            setNavigationDrawerItem(values);
        }
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

}
