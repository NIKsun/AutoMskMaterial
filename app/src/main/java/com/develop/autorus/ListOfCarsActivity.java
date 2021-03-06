package com.develop.autorus;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.rey.material.widget.SnackBar;

/**
 * Created by Никита on 27.09.2015.
 */
public class ListOfCarsActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    private Boolean isFirstLaunch = true;
    Integer monitorID, filterID;
    Boolean monitorWasAdded = false;
    SnackBar snackBar;
    InterstitialAd mInterstitialAd = new InterstitialAd(this);
    Tracker mTracker;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sPref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ListOfCarsActivity.this);
        if(!(pref.getBoolean("TAG_BUY_ALL", false) || pref.getBoolean("TAG_DISABLED_ADS", false))) {
            int adMobCounter = sPref.getInt("AdMobCounter", 1);
            if (adMobCounter == 3) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(adRequest);
                sPref.edit().putInt("AdMobCounter", 1).commit();
            } else
                sPref.edit().putInt("AdMobCounter", adMobCounter + 1).commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("List of cars");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        setContentView(R.layout.activity_list_of_cars);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        snackBar = (SnackBar)findViewById(R.id.loc_sn);
        mInterstitialAd.setAdUnitId(getString(R.string.banner_id));

        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_actionbar);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final android.support.design.widget.TabLayout tabLayout2 = (android.support.design.widget.TabLayout) findViewById(R.id.sliding_tabs_LOC);

        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1")) {
            tabLayout2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window statusBar = getWindow();
                statusBar.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                statusBar.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                statusBar.setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
            }
        }
        else {
            tabLayout2.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window statusBar = getWindow();
                statusBar.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                statusBar.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                statusBar.setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor2));
            }
        }


        filterID = getIntent().getIntExtra("filterID",-1);
        monitorID = getIntent().getIntExtra("monitorID",-1);

        viewPager = (ViewPager) findViewById(R.id.viewpager_LOC);
        viewPager.setAdapter(new LOC_FragmentPagerAdapter(getSupportFragmentManager(),
                ListOfCarsActivity.this, monitorID));
        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs_LOC);
        tabLayout.setupWithViewPager(viewPager);

        final android.widget.Button addMonitorButton = (android.widget.Button)findViewById(R.id.toolbar_add_monitor_button);
        if(monitorID == -1)
        {
            addMonitorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteDatabase db = new DbHelper(ListOfCarsActivity.this).getWritableDatabase();
                    Cursor cursorMonitors = db.query("monitors", null, null, null, null, null, null);
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ListOfCarsActivity.this);
                    if(pref.getBoolean("TAG_BUY_ALL", false) || pref.getBoolean("TAG_MONITOR", false) || cursorMonitors.getCount() <= 6) {
                        Monitor monitor = new Monitor();
                        SharedPreferences sPref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
                        monitor.hrefAuto = sPref.getString("hrefAutoRu", "###");
                        monitor.hrefAvito = sPref.getString("hrefAvitoRu", "###");
                        monitor.hrefDrom = sPref.getString("hrefDromRu", "###");
                        Filter filter = new Filter();
                        filter.id = filterID;
                        monitor.filter = filter;
                        monitor.insertToDb(v.getContext());
                        monitorWasAdded = true;

                        mTracker.send(new HitBuilders.EventBuilder().setCategory("Create monitor").setAction("from list of cars").setValue(1).build());
                        Toast.makeText(ListOfCarsActivity.this, "Монитор с текущими параметрами создан", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        snackBar.applyStyle(R.style.Material_Widget_SnackBar_Mobile_MultiLine);
                        snackBar.text("Купите опцию для возможности добавлять более 7 мониторов")
                                .lines(3)
                                .actionText("\nКупить")
                                .duration(4000)
                                .actionClickListener(new SnackBar.OnActionClickListener() {
                                    @Override
                                    public void onActionClick(SnackBar snackBar, int i) {
                                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ListOfCarsActivity.this);
                                        pref.edit().putInt("NumberOfCallingFragment", 5).commit();
                                        finish();
                                    }
                                });
                        snackBar.actionTextColor(getResources().getColor(R.color.myPrimaryColor));
                        snackBar.show();
                    }
                    Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.anim_translate_right);
                    addMonitorButton.setVisibility(View.INVISIBLE);
                    addMonitorButton.startAnimation(anim);
                }
            });
        } else
            addMonitorButton.setVisibility(View.INVISIBLE);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setItemBackgroundTransparent();
        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
        mNavigationDrawerFragment.closeDrawer();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if(isFirstLaunch)
        {
            isFirstLaunch = false;
            return;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putInt("NumberOfCallingFragment",position).commit();
        finish();
    }

    @Override
     public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        if(monitorID == -1 && !monitorWasAdded){
            SQLiteDatabase db = new DbHelper(this).getWritableDatabase();
            db.delete("filters", "id = ?", new String[]{String.valueOf(filterID)});
            db.close();
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().remove("NumberOfCallingFragment").commit();

        super.onDestroy();
    }

    public SnackBar getSnackBar()
    {
        return snackBar;
    }
    public InterstitialAd getAds()
    {
        return mInterstitialAd;
    }
    public Tracker getTracker(){return mTracker;}
}
