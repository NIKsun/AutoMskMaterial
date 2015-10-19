package com.example.material_model_automsk;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.FragmentManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.rey.material.widget.FloatingActionButton;

import java.io.Serializable;

/**
 * Created by Никита on 05.10.2015.
 */
public class SearchAndMonitorsFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;
    private MainActivity myContext;
    View savedView;

    public static SearchAndMonitorsFragment newInstance(int page) {
        SearchAndMonitorsFragment f = new SearchAndMonitorsFragment();
        Bundle args = new Bundle();
        args.putInt("pageNumber", page);
        f.setArguments(args);
        return f;
    }


    @Override
    public void onAttach(Activity activity) {
        myContext=(MainActivity) activity;
        super.onAttach(activity);
    }

    public void setPage(int page)
    {
        viewPager.setCurrentItem(page);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedView != null)
            return savedView;

        savedView =  inflater.inflate(R.layout.fragment_search_and_monitors, container, false);
        viewPager = (ViewPager) savedView.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MonitorFragmentPagerAdapter(myContext.getSupportFragmentManager(), myContext));

        tabLayout = (TabLayout) savedView.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                myContext.setNavigationDrawerItem(position);
                Button addMonitorButton = myContext.getAddMonitorButton();
                MonitorsFragment mf = ((MonitorFragmentPagerAdapter)viewPager.getAdapter()).monitorsFragment;
                SearchFragment sf = ((MonitorFragmentPagerAdapter)viewPager.getAdapter()).searchFragment;
                if (position == 0)
                {
                    if(mf != null)
                        mf.update();
                    if(sf != null)
                        sf.hideFAB();
                    Animation anim = AnimationUtils.loadAnimation(myContext, R.anim.anim_translate_right);
                    addMonitorButton.setVisibility(View.INVISIBLE);
                    addMonitorButton.startAnimation(anim);
                } else
                {
                    if(mf != null)
                        mf.hideFAB(-1);
                    if(sf != null)
                        sf.showFAB();

                    Animation anim = AnimationUtils.loadAnimation(myContext, R.anim.anim_translate_left);
                    addMonitorButton.setVisibility(View.VISIBLE);
                    addMonitorButton.startAnimation(anim);
                }
                myContext.getSnackBar().dismiss();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if(viewPager.getCurrentItem()==0)
            myContext.getAddMonitorButton().setVisibility(View.INVISIBLE);

        viewPager.setCurrentItem(getArguments().getInt("pageNumber", 0));
        return savedView;

    }

    public SearchFragment getSearchFragment()
    {
        return ((MonitorFragmentPagerAdapter)viewPager.getAdapter()).searchFragment;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Button addMonitorButton = myContext.getAddMonitorButton();
        if(getSelectedTabPosition()==1){
            if(hidden) {
                Animation anim = AnimationUtils.loadAnimation(myContext, R.anim.anim_translate_right);
                addMonitorButton.setVisibility(View.INVISIBLE);
                addMonitorButton.startAnimation(anim);
            } else
            {
                Animation anim = AnimationUtils.loadAnimation(myContext, R.anim.anim_translate_left);
                addMonitorButton.setVisibility(View.VISIBLE);
                addMonitorButton.startAnimation(anim);
            }
        }
        super.onHiddenChanged(hidden);
    }

    public Integer getSelectedTabPosition()
    {
        return tabLayout.getSelectedTabPosition();
    }

    public void updateMonitorsFragment() {
        ((MonitorFragmentPagerAdapter)viewPager.getAdapter()).updateMonitorsFragment();
    }
}
