package com.example.material_model_automsk;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class LOC_FragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "auto.ru", "avito.ru", "drom.ru"};
    private Context context;
    private int filterID;

    public LOC_FragmentPagerAdapter(FragmentManager fm, Context context, int filterID) {
        super(fm);
        this.context = context;
        this.filterID = filterID;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }


    @Override
    public Fragment getItem(int position) {
        return LOCfragment.newInstance(position, filterID);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
