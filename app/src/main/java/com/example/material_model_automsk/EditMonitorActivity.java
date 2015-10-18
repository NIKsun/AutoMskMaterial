package com.example.material_model_automsk;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.Switch;

/**
 * Created by Никита on 16.10.2015.
 */
public class EditMonitorActivity extends FragmentActivity {
    SearchFragment searchFragment;
    Integer filterID;

    protected void onCreate(Bundle savedInstanceState) {
        Log.d("lis","createEdit");
        SharedPreferences pref2 = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
        pref2.edit().putInt("isEditFilter",1).commit();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_monitor);

        FrameLayout header = (FrameLayout)findViewById(R.id.title);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1"))
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        else
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));

        Log.d("filterID", String.valueOf(getIntent().getIntExtra("filterID", -1)));
        searchFragment = (SearchFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
        pref.edit().putInt("isEditFilter",1).commit();
        filterID = getIntent().getIntExtra("filterID",-1);
        if(filterID!=-1){
            final DbHelper dbHelper = new DbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursorMark = db.query("filters", null, "id=?", new String[]{filterID.toString()}, null, null, null);
            cursorMark.moveToFirst();

            String yearFrom = cursorMark.getString(cursorMark.getColumnIndex("yearFrom"));
            String yearTo = cursorMark.getString(cursorMark.getColumnIndex("yearTo"));

            String priceFrom = cursorMark.getString(cursorMark.getColumnIndex("priceFrom"));
            String priceTo = cursorMark.getString(cursorMark.getColumnIndex("priceTo"));

            String milleageFrom = cursorMark.getString(cursorMark.getColumnIndex("milleageFrom"));
            String milleageTo = cursorMark.getString(cursorMark.getColumnIndex("milleageTo"));

            String volumeFrom = cursorMark.getString(cursorMark.getColumnIndex("volumeFrom"));
            String volumeTo = cursorMark.getString(cursorMark.getColumnIndex("volumeTo"));

            String mark = cursorMark.getString(cursorMark.getColumnIndex("marka"));
            String model = cursorMark.getString(cursorMark.getColumnIndex("model"));

            String transmission = cursorMark.getString(cursorMark.getColumnIndex("transmission"));
            String bodyType = cursorMark.getString(cursorMark.getColumnIndex("bodyType"));
            String engineType = cursorMark.getString(cursorMark.getColumnIndex("engineType"));
            String driveType = cursorMark.getString(cursorMark.getColumnIndex("driveType"));

            Integer withPhoto = cursorMark.getInt(cursorMark.getColumnIndex("withPhoto"));

            String[] data;
            View v;
            Spinner sp1;
            Spinner sp2;
            ImageView iv;
            LinearLayout ll;
            Button b;

            //photo
            if(withPhoto==1)
                ((Switch) findViewById(R.id.search_ll_withPhoto)).setChecked(true);


            // engine
            if(!engineType.equals("")) {
                v = (View) findViewById(R.id.search_ll_engine_type);
                v.setClickable(false);
                ll = (LinearLayout) findViewById(R.id.search_ll_engine_type_hidden);
                b = (Button) findViewById(R.id.search_ll_engine_type_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_engine_type);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);

                if (engineType.contains("1"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_gasoline)).setChecked(true);
                if (engineType.contains("2"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_diesel)).setChecked(true);
                if (engineType.contains("3"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_hybrid)).setChecked(true);
                if (engineType.contains("4"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_gas)).setChecked(true);
                if (engineType.contains("5"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_electro)).setChecked(true);
                MainActivity.expand(ll);
            }

            //trans
            if(!transmission.equals("")) {
                v = (View) findViewById(R.id.search_ll_trans);
                v.setClickable(false);
                ll = (LinearLayout) findViewById(R.id.search_ll_trans_hidden);
                b = (Button) findViewById(R.id.search_ll_trans_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_trans);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);

                if (transmission.contains("2"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_trans_man)).setChecked(true);
                if (transmission.contains("1"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_trans_auto)).setChecked(true);
                if (transmission.contains("3"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_trans_robot)).setChecked(true);
                if (transmission.contains("4"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_trans_var)).setChecked(true);
                MainActivity.expand(ll);
            }

            //body
            if(!bodyType.equals("")) {
                v = (View) findViewById(R.id.search_ll_body_type);
                v.setClickable(false);
                ll = (LinearLayout) findViewById(R.id.search_ll_body_type_hidden);
                b = (Button) findViewById(R.id.search_ll_body_type_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_body_type);
                iv.setVisibility(View.INVISIBLE);
                ll.setVisibility(View.VISIBLE);

                if (bodyType.contains("1"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_sed)).setChecked(true);
                if (bodyType.contains("2"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_hatch)).setChecked(true);
                if (bodyType.contains("3"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_univ)).setChecked(true);
                if (bodyType.contains("5"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_minivan)).setChecked(true);
                if (bodyType.contains("4"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_offroad)).setChecked(true);
                if (bodyType.contains("7"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_coupe)).setChecked(true);
                if (bodyType.contains("9"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_van)).setChecked(true);
                if (bodyType.contains("6"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_limus)).setChecked(true);
                if (bodyType.contains("0"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_picap)).setChecked(true);
                if (bodyType.contains("8"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_cabrio)).setChecked(true);

                MainActivity.expand(ll);
            }

            //drive
            if(!driveType.equals("")) {
                v = (View) findViewById(R.id.search_ll_drive);
                v.setClickable(false);
                ll = (LinearLayout) findViewById(R.id.search_ll_drive_hidden);
                b = (Button) findViewById(R.id.search_ll_drive_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_drive);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);

                if (driveType.contains("1"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_drive_forward)).setChecked(true);
                if (driveType.contains("2"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_drive_backward)).setChecked(true);
                if (driveType.contains("3"))
                    ((com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_drive_full)).setChecked(true);

                MainActivity.expand(ll);
            }
/*
            //mark and model
            TextView t = (TextView) findViewById(R.id.search_ll_mark_text);
            t.setText(mark);

            TextView t2 = (TextView) findViewById(R.id.search_ll_model_text);
            t2.setText(model);

            CardView ll = (CardView) findViewById(R.id.search_ll_model_cardview);
            if(!mark.equals("Любая"))
            {
                Button b = (Button) findViewById(R.id.search_ll_mark_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_mark);
                iv.setVisibility(View.INVISIBLE);
                ll.setVisibility(View.VISIBLE);
            }
            else
                ll.setVisibility(View.GONE);
            if(!model.equals("Любая"))
            {
                Button b2 = (Button) findViewById(R.id.search_ll_model_clear);
                b2.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_model);
                iv.setVisibility(View.INVISIBLE);
            }
            else
            {
                Button b3 = (Button) findViewById(R.id.search_ll_model_clear);
                b3.setVisibility(View.INVISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_model);
                iv.setVisibility(View.VISIBLE);
            }
*/
            pref2.edit().putString("SelectedMark", mark).commit();
            pref2.edit().putString("SelectedModel", model).commit();
            //year
            if(!yearTo.equals("") || !yearFrom.equals("")) {
                data = new String[36];
                for (int i = 0; i < data.length; i++)
                    data[i] = "" + String.valueOf(i + 1980);

                v = (View) findViewById(R.id.search_ll_year);
                searchFragment.initSpinners("Year", v);
                if (!yearFrom.equals("")) {
                    sp1 = (Spinner) findViewById(R.id.spinner_label_year_from);
                    sp1.setSelection(java.util.Arrays.asList(data).indexOf(yearFrom));
                }
                if (!yearTo.equals("")) {
                    sp2 = (Spinner) findViewById(R.id.spinner_label_year_to);
                    sp2.setSelection(java.util.Arrays.asList(data).indexOf(yearTo));
                }
            }
            //price
            if(!priceTo.equals("") || !priceFrom.equals("")) {
                data = new String[176];
                for (int i = 0; i < data.length; ++i) {
                    if (i < 51) {
                        data[i] = String.valueOf(i * 10000);
                    } else if (i < 76) {
                        data[i] = String.valueOf(Integer.parseInt(data[i - 1].replace(" ", "")) + 20000);
                    } else if (i < 96) {
                        data[i] = String.valueOf(Integer.parseInt(data[i - 1].replace(" ", "")) + 50000);
                    } else
                        data[i] = String.valueOf(Integer.parseInt(data[i - 1].replace(" ", "")) + 100000);

                    int len = data[i].length(), counter;
                    String result = "";
                    if (len % 3 != 0)
                        result = data[i].substring(0, len % 3) + " ";
                    counter = len % 3;
                    while (counter < len) {
                        result += data[i].substring(counter, counter + 3) + " ";
                        counter += 3;
                    }
                    data[i] = result.substring(0, result.length() - 1);
                }
                v = (View) findViewById(R.id.search_ll_price);
                searchFragment.initSpinners("Price", v);
                if (!priceFrom.equals("")) {
                    sp1 = (Spinner) findViewById(R.id.spinner_label_price_from);
                    sp1.setSelection(java.util.Arrays.asList(data).indexOf(priceFrom));
                }
                if (!priceTo.equals("")) {
                    sp2 = (Spinner) findViewById(R.id.spinner_label_price_to);
                    sp2.setSelection(java.util.Arrays.asList(data).indexOf(priceTo));
                }
            }
            //milleage
            if(!milleageTo.equals("") || !milleageFrom.equals("")) {
                data = new String[]{"0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90", "95", "100", "110", "120", "130", "140", "150", "160", "170", "180", "190", "200", "210", "220", "230", "240", "250", "260", "270", "280", "290", "300", "310", "320", "330", "340", "350", "360", "370", "380", "390", "400", "410", "420", "430", "440", "450", "460", "470", "480", "490", "500", "600"};
                String[] data_view = new String[data.length];
                for (int n = 1; n < data.length - 1; ++n) {
                    data_view[n] = data[n] + " 000";
                }
                data_view[0] = "0";
                data_view[data.length - 1] = "500 000+";

                v = (View) findViewById(R.id.search_ll_mileage);
                searchFragment.initSpinners("Milleage", v);
                if (!milleageFrom.equals("")) {
                    sp1 = (Spinner) findViewById(R.id.spinner_label_mileage_from);
                    sp1.setSelection(java.util.Arrays.asList(data_view).indexOf(milleageFrom));
                }
                if (!milleageTo.equals("")) {
                    sp2 = (Spinner) findViewById(R.id.spinner_label_mileage_to);
                    sp2.setSelection(java.util.Arrays.asList(data_view).indexOf(milleageTo));
                }
            }
            //volume
            if(!volumeTo.equals("") || !volumeFrom.equals("")) {
                data = new String[]{"0.0", "0.6", "0.7", "0.8", "0.9", "1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9", "2.0", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "2.8", "2.9", "3.0", "3.1", "3.2", "3.3", "3.4", "3.5", "4.0", "4.5", "5.0", "5.5", "6.0", "6.0+"};

                v = (View) findViewById(R.id.search_ll_engine_volume);
                searchFragment.initSpinners("Volume", v);

                if (!volumeFrom.equals("")) {
                    sp1 = (Spinner) findViewById(R.id.spinner_label_engine_volume_from);
                    sp1.setSelection(java.util.Arrays.asList(data).indexOf(volumeFrom));
                }
                if (!volumeTo.equals("")) {
                    sp2 = (Spinner) findViewById(R.id.spinner_label_engine_volume_to);
                    sp2.setSelection(java.util.Arrays.asList(data).indexOf(volumeTo));
                }
            }
            db.close();
        }

    }
    public void onResume(){
        Log.d("lis", "resumeEdit");
        super.onResume();

    }
    public void onDestroy(){
        super.onDestroy();
     /*   SharedPreferences pref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
        pref.edit().putInt("isEditFilter",0).commit();
        pref.edit().putString("SelectedMark", "Любая").commit();
        pref.edit().putString("SelectedModel", "Любая").commit();*/
        SharedPreferences pref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
        pref.edit().putInt("isEditFilter",0).commit();
        //searchFragment.
        Log.d("lis", "destroyEdit");
    }
/*
    public void onPause(){
        super.onPause();
        SharedPreferences pref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
        //pref.edit().putInt("isEditFilter",0).commit();
        pref.edit().putString("SelectedMark", "Любая").commit();
        pref.edit().putString("SelectedModel", "Любая").commit();
        Log.d("lis", "pauseEdit");
    }
*/

    public void onClickHandlerHidden(View v){
        searchFragment.onClickHandlerHidden(v);
    }

    public void onClickClearSelection(View v){
        searchFragment.onClickClearSelection(v);
    }
    public void onClickMarkorModel(View v){
        searchFragment.onClickMarkorModel(v);
    }
    public void onCheckClicker(View v){
        Filter filter = new Filter();
        filter.fillFilter(searchFragment.view);
        filter.getHref(this);
        Monitor monitor = new Monitor(filter, this);
        if(filterID==-1) {
            filter.insertToDb(this);

            monitor.insertToDb(this);
        }
        else
        {
            final DbHelper dbHelper = new DbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("marka", filter.mark);
            cv.put("model", filter.model);
            cv.put("yearFrom", filter.yearFrom);
            cv.put("yearTo", filter.yearTo);

            cv.put("priceFrom", filter.priceFrom);
            cv.put("priceTo", filter.priceTo);
            cv.put("milleageFrom", filter.milleageFrom);
            cv.put("milleageTo", filter.milleageTo);

            cv.put("volumeFrom", filter.volumeFrom);
            cv.put("volumeTo", filter.volumeTo);
            cv.put("transmission", filter.transmission);
            cv.put("bodyType", filter.typeOfCarcase);

            cv.put("engineType", filter.typeOfEngine);
            cv.put("withPhoto", filter.withPhoto ? 1 : 0);
            cv.put("driveType", filter.typeOfWheelDrive);
            db.update("filters", cv, "id=?", new String[]{filterID.toString()});

            cv = new ContentValues();
            cv.put("filter_id", filterID);
            cv.put("count_of_new_cars", monitor.countOfNewCars);
            cv.put("is_active", monitor.isActive ? 1 : 0);

            cv.put("href_auto", monitor.hrefAuto);
            cv.put("href_avito", monitor.hrefAvito);
            cv.put("href_drom", monitor.hrefDrom);

            //db.insert("monitors", null, cv);
            db.update("monitors", cv, "filter_id=?", new String[]{filterID.toString()});
            db.close();

        }
        // тут надо изменить текущий фильтр точнее добавить новый на его место
        finish();
    }
}
