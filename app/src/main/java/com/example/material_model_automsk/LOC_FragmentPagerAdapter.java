package com.example.material_model_automsk;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class LOC_FragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "auto.ru", "avito.ru", "drom.ru"};
    private Context context;
    String hrefAuto;
    String hrefAvito;
    String hrefDrom;

    public LOC_FragmentPagerAdapter(FragmentManager fm, Context context, Integer monitorID) {
        super(fm);
        this.context = context;

        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        Cursor cursorMonitors = db.query("monitors", new String[]{"id,href_auto,href_avito,href_drom"}, "id = ?", new String[]{String.valueOf(monitorID)}, null, null, null);

        int iHrefAuto = cursorMonitors.getColumnIndex("href_auto");
        int iHrefAvito = cursorMonitors.getColumnIndex("href_avito");
        int iHrefDrom = cursorMonitors.getColumnIndex("href_drom");

        if (cursorMonitors.moveToFirst()) {
            this.hrefAuto = cursorMonitors.getString(iHrefAuto);
            this.hrefAvito = cursorMonitors.getString(iHrefAvito);
            this.hrefDrom = cursorMonitors.getString(iHrefDrom);
        }
        else {
            this.hrefAuto = "###";
            this.hrefAvito = "###";
            this.hrefDrom = "###";
        }

        db.close();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return LOCfragment.newInstance(position, hrefAuto);
            case 1:
                return LOCfragment.newInstance(position, hrefAvito);
            case 2:
                return LOCfragment.newInstance(position, hrefDrom);
        }
        return LOCfragment.newInstance(position, "###");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
