package com.example.material_model_automsk;

import android.content.Context;
import android.content.SharedPreferences;
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
    String lastCarDateAuto;
    String lastCarDateAvito;
    String lastCarIdDrom;
    Integer monitorID;

    public LOC_FragmentPagerAdapter(FragmentManager fm, Context context, Integer monitorID) {
        super(fm);
        this.monitorID = monitorID;
        this.context = context;

        if(monitorID == -1)
        {
            SharedPreferences sPref = context.getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
            hrefAuto = sPref.getString("hrefAutoRu","###");
            hrefAvito = sPref.getString("hrefAvitoRu","###");
            hrefDrom = sPref.getString("hrefDromRu","###");
            this.lastCarDateAuto = "ItIsJUST_search";
            this.lastCarDateAvito = "ItIsJUST_search";
            this.lastCarIdDrom = "ItIsJUST_search";
        }
        else {
            SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
            Cursor cursorMonitors = db.query("monitors",
                    new String[]{"id,href_auto,href_avito,href_drom, date_auto, date_avito, id_drom"},
                    "id = ?", new String[]{String.valueOf(monitorID)}, null, null, null);

            int iHrefAuto = cursorMonitors.getColumnIndex("href_auto");
            int iHrefAvito = cursorMonitors.getColumnIndex("href_avito");
            int iHrefDrom = cursorMonitors.getColumnIndex("href_drom");
            int iLastCarDateAuto = cursorMonitors.getColumnIndex("date_auto");
            int iLastCarDateAvito = cursorMonitors.getColumnIndex("date_avito");
            int iLastCarIdDrom = cursorMonitors.getColumnIndex("id_drom");

            if (cursorMonitors.moveToFirst()) {
                this.hrefAuto = cursorMonitors.getString(iHrefAuto);
                this.hrefAvito = cursorMonitors.getString(iHrefAvito);
                this.hrefDrom = cursorMonitors.getString(iHrefDrom);
                this.lastCarDateAuto = cursorMonitors.getString(iLastCarDateAuto) != null ? cursorMonitors.getString(iLastCarDateAuto) : "###";
                this.lastCarDateAvito = cursorMonitors.getString(iLastCarDateAvito) != null ? cursorMonitors.getString(iLastCarDateAvito) : "###";
                this.lastCarIdDrom = cursorMonitors.getString(iLastCarIdDrom) != null ? cursorMonitors.getString(iLastCarIdDrom) : "###";
            } else {
                this.hrefAuto = "###";
                this.hrefAvito = "###";
                this.hrefDrom = "###";
                this.lastCarDateAuto = "###";
                this.lastCarDateAvito = "###";
                this.lastCarIdDrom = "###";
            }
            db.close();
        }
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
                return LOCfragment.newInstance(position, hrefAuto, lastCarDateAuto, monitorID);
            case 1:
                return LOCfragment.newInstance(position, hrefAvito, lastCarDateAvito, monitorID);
            case 2:
                return LOCfragment.newInstance(position, hrefDrom, lastCarIdDrom, monitorID);
        }
        return LOCfragment.newInstance(position, "###", "###", 0);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
