package com.example.material_model_automsk;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.rey.material.widget.ProgressView;

import io.fabric.sdk.android.Fabric;

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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_of_cars);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager_LOC);
        viewPager.setAdapter(new LOC_FragmentPagerAdapter(getSupportFragmentManager(),
                ListOfCarsActivity.this, getIntent().getIntExtra("FilterID", 0)));
        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs_LOC);
        tabLayout.setupWithViewPager(viewPager);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setItemBackgroundTransparent();
        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
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
}
