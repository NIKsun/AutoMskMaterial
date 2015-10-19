package com.develop.autorus;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_of_cars);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_actionbar);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final android.support.design.widget.TabLayout tabLayout2 = (android.support.design.widget.TabLayout) findViewById(R.id.sliding_tabs_LOC);

        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1")) {
            tabLayout2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else {
            tabLayout2.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
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
                    Monitor monitor = new Monitor();
                    SharedPreferences sPref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
                    monitor.hrefAuto = sPref.getString("hrefAutoRu","###");
                    monitor.hrefAvito = sPref.getString("hrefAvitoRu","###");
                    monitor.hrefDrom = sPref.getString("hrefDromRu","###");
                    Filter filter = new Filter();
                    filter.id = filterID;
                    monitor.filter = filter;
                    monitor.insertToDb(v.getContext());
                    monitorWasAdded = true;

                    Toast.makeText(ListOfCarsActivity.this, "Монитор с текущими параметрами создан", Toast.LENGTH_SHORT).show();
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

        super.onDestroy();
    }
}
