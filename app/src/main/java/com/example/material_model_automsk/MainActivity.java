package com.example.material_model_automsk;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.SnackBar;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    ViewPager viewPager;
    TabLayout tabLayout;
    private SnackBar mSnackBar;
    Button addMonitorButton;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_list_of_monitors);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        mSnackBar = (SnackBar)findViewById(R.id.main_sn);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MonitorFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        addMonitorButton = (Button)findViewById(R.id.toolbar_add_monitor_button);
        addMonitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                ((SimpleDialog.Builder)builder).message("Будет создан новый монитор с текущими настройками поиска. " +
                        "Мониторы помогают сохранять настройки поиска и отслеживать поступление новых объявлений по этим настройкам.")
                        .title("Создать новый монитор?")
                        .positiveAction("Создать")
                        .negativeAction("Нет");
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), null);
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_translate_right);
                    addMonitorButton.setVisibility(View.INVISIBLE);
                    addMonitorButton.startAnimation(anim);

                }
                else {
                    Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_translate_left);
                    addMonitorButton.setVisibility(View.VISIBLE);
                    addMonitorButton.startAnimation(anim);
                }
                mSnackBar.dismiss();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });




        if(viewPager.getCurrentItem()==0)
            addMonitorButton.setVisibility(View.INVISIBLE);

        viewPager.setCurrentItem(1);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
     }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
    }

    public SnackBar getSnackBar(){
        return mSnackBar;
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

    public void onClickHandlerHidden(View v){
        LinearLayout ll;
        switch (v.getId()){
            case R.id.search_ll_engine_type:
                ll = (LinearLayout)findViewById(R.id.search_ll_engine_type_hidden);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            case R.id.search_ll_price:
                ll = (LinearLayout)findViewById(R.id.search_ll_price_hidden);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
        }
        return;
    }

}
