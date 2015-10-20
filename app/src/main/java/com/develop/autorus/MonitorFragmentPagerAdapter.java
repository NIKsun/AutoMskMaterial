package com.develop.autorus;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MonitorFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Мониторы", "Поиск"};
    private Context context;
    MonitorsFragment monitorsFragment;
    SearchFragment searchFragment;

    public MonitorFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
    public void updateMonitorsFragment() {
        if(monitorsFragment != null)
            monitorsFragment.update();
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            monitorsFragment = MonitorsFragment.newInstance(position + 1);
            return monitorsFragment;
        }
        else
            searchFragment = SearchFragment.newInstance();
            return searchFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
