package com.example.material_model_automsk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.Button;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.Spinner;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SnackBar mSnackBar;
    private Button addMonitorButton;
    private Boolean isFirstLaunch = true, itemSelectFromTabLayout = false;
    private Toolbar mToolbar;
    private Fragment secondFragment;
    private SearchAndMonitorsFragment mainFragment;
    private Toast backToast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        super.onCreate(savedInstanceState);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1"))
            setTheme(R.style.AppTheme);
         else if (themeName.equals("2"))
            setTheme(R.style.AppTheme2);

        ThemeManager.init(this, 2, 0, null);


        if(isFirstLaunch) {
            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            mainFragment = SearchAndMonitorsFragment.newInstance(1);
            fTrans.add(R.id.container, mainFragment, "MAIN").commit();
        }
        backToast = Toast.makeText(this,"Нажмите еще раз для выхода",Toast.LENGTH_SHORT);
        setContentView(R.layout.main_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        mSnackBar = (SnackBar)findViewById(R.id.main_sn);

        addMonitorButton = (Button)findViewById(R.id.toolbar_add_monitor_button);
        addMonitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                ((SimpleDialog.Builder) builder).message("Будет создан новый монитор с текущими настройками поиска. " +
                        "Мониторы помогают сохранять настройки поиска и отслеживать поступление новых объявлений по этим настройкам.")
                        .title("Создать новый монитор?")
                        .positiveAction("Создать")
                        .negativeAction("Нет");
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), null);
            }
        });


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int numberOfCallingFragment = pref.getInt("NumberOfCallingFragment", -1);
        if(numberOfCallingFragment != -1) {
            onNavigationDrawerItemSelected(numberOfCallingFragment);
            setNavigationDrawerItem(numberOfCallingFragment);
            pref.edit().remove("NumberOfCallingFragment").commit();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if(itemSelectFromTabLayout)
        {
            itemSelectFromTabLayout = false;
            return;
        }
        if(isFirstLaunch)
        {
            isFirstLaunch = false;
            return;
        }

        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        switch (position){
            case 0:
                mToolbar.setTitle("Авто Москва");
                mainFragment.setPage(0);
                if(secondFragment != null) {
                    fTrans.remove(secondFragment);
                    fTrans.show(mainFragment);
                }
                break;
            case 1:
                mToolbar.setTitle("Авто Москва");
                mainFragment.setPage(1);
                if(secondFragment != null) {
                    fTrans.remove(secondFragment);
                    fTrans.show(mainFragment);
                }
                break;
            case 2:
                mToolbar.setTitle("Избранное");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new FavoritesFragment();
                fTrans.add(R.id.container, secondFragment);
                break;
            case 3:
                mToolbar.setTitle("Настройки");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new SettingsFragment();
                fTrans.add(R.id.container, secondFragment);
                break;
            case 4:
                mToolbar.setTitle("Обратная связь");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new FeedbackFragment();
                fTrans.add(R.id.container, secondFragment);
                break;
            case 5:
                mToolbar.setTitle("Справка");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new ReferenceFragment();
                fTrans.add(R.id.container, secondFragment);
                break;

        }
        fTrans.commit();
    }

    public SnackBar getSnackBar(){
        return mSnackBar;
    }
    public Button getAddMonitorButton(){
        return addMonitorButton;
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else{
            if(backToast.getView().getWindowVisibility() == View.VISIBLE) {
                backToast.getView().setVisibility(View.INVISIBLE);
                super.onBackPressed();
            }
            else
                backToast.show();
        }
    }

    public void onClickHandlerHidden(View v){
        LinearLayout ll;
        Button b;
        switch (v.getId()){
            case R.id.search_ll_engine_type:
                ll = (LinearLayout)findViewById(R.id.search_ll_engine_type_hidden);
                b = (Button) findViewById(R.id.search_ll_engine_type_clear);
                b.setVisibility(View.VISIBLE);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            case R.id.search_ll_price:
                ll = (LinearLayout)findViewById(R.id.search_ll_price_hidden);
                b = (Button) findViewById(R.id.search_ll_price_clear);
                b.setVisibility(View.VISIBLE);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            case R.id.search_ll_year:


                String[] data = new String[36];
                for(int i = 0; i < data.length; i++)
                    data[i] = "" + String.valueOf(i + 1980);
                fillSpinner((Spinner) findViewById(R.id.spinner_label_year_from),data);
                fillSpinner((Spinner) findViewById(R.id.spinner_label_year_to),data);



                ll = (LinearLayout)findViewById(R.id.search_ll_year_hidden);
                b = (Button) findViewById(R.id.search_ll_year_clear);
                b.setVisibility(View.VISIBLE);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            case R.id.search_ll_mileage:
                ll = (LinearLayout)findViewById(R.id.search_ll_mileage_hidden);
                b = (Button) findViewById(R.id.search_ll_mileage_clear);
                b.setVisibility(View.VISIBLE);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            case R.id.search_ll_engine_volume:
                ll = (LinearLayout)findViewById(R.id.search_ll_engine_volume_hidden);
                b = (Button) findViewById(R.id.search_ll_engine_volume_clear);
                b.setVisibility(View.VISIBLE);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            case R.id.search_ll_trans:
                ll = (LinearLayout)findViewById(R.id.search_ll_trans_hidden);
                b = (Button) findViewById(R.id.search_ll_trans_clear);
                b.setVisibility(View.VISIBLE);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            case R.id.search_ll_body_type:
                ll = (LinearLayout)findViewById(R.id.search_ll_body_type_hidden);
                b = (Button) findViewById(R.id.search_ll_body_type_clear);
                b.setVisibility(View.VISIBLE);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            case R.id.search_ll_drive:
                ll = (LinearLayout)findViewById(R.id.search_ll_drive_hidden);
                b = (Button) findViewById(R.id.search_ll_drive_clear);
                b.setVisibility(View.VISIBLE);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                break;

        }
        return;
    }

    public void onClickClearSelection(View v){
        LinearLayout ll;
        Button b;
        com.rey.material.widget.CheckBox ch;
        android.support.v7.widget.AppCompatTextView t;
        SharedPreferences sPref;
        SharedPreferences.Editor ed;
        switch (v.getId()){
            case R.id.search_ll_year_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_year_hidden);
                b = (Button) findViewById(R.id.search_ll_year_clear);
                b.setVisibility(View.INVISIBLE);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                break;
            case R.id.search_ll_price_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_price_hidden);
                b = (Button) findViewById(R.id.search_ll_price_clear);
                b.setVisibility(View.INVISIBLE);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                break;
            case R.id.search_ll_mileage_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_mileage_hidden);
                b = (Button) findViewById(R.id.search_ll_mileage_clear);
                b.setVisibility(View.INVISIBLE);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                break;
            case R.id.search_ll_engine_volume_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_engine_volume_hidden);
                b = (Button) findViewById(R.id.search_ll_engine_volume_clear);
                b.setVisibility(View.INVISIBLE);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                break;
            case R.id.search_ll_engine_type_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_engine_type_hidden);
                b = (Button) findViewById(R.id.search_ll_engine_type_clear);
                b.setVisibility(View.INVISIBLE);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_diesel);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_electro);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_gas);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_gasoline);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_hybrid);
                ch.setChecked(false);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                break;
            case R.id.search_ll_trans_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_trans_hidden);
                b = (Button) findViewById(R.id.search_ll_trans_clear);
                b.setVisibility(View.INVISIBLE);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_trans_auto);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_trans_man);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_trans_robot);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_trans_var);
                ch.setChecked(false);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                break;
            case R.id.search_ll_body_type_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_body_type_hidden);
                b = (Button) findViewById(R.id.search_ll_body_type_clear);
                b.setVisibility(View.INVISIBLE);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_cabrio);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_coupe);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_hatch);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_limus);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_minivan);
                ch.setChecked(false);


                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_offroad);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_picap);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_sed);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_univ);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_van);
                ch.setChecked(false);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                break;
            case R.id.search_ll_drive_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_drive_hidden);
                b = (Button) findViewById(R.id.search_ll_drive_clear);
                b.setVisibility(View.INVISIBLE);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_drive_backward);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_drive_forward);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_drive_full);
                ch.setChecked(false);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                break;
            case R.id.search_ll_mark_clear:
                b = (Button) findViewById(R.id.search_ll_mark_clear);
                b.setVisibility(View.INVISIBLE);

                t = (android.support.v7.widget.AppCompatTextView) findViewById(R.id.search_ll_mark_text);
                t.setText("Любая");

                sPref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
                ed = sPref.edit();
                ed.putString("SelectedMark","Любая").commit();
                ed.putString("SelectedModel","Любая").commit();

                CardView cv = (CardView) findViewById(R.id.search_ll_model_cardview);
                cv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                break;
            case R.id.search_ll_model_clear:
                b = (Button) findViewById(R.id.search_ll_model_clear);
                b.setVisibility(View.INVISIBLE);

                t = (android.support.v7.widget.AppCompatTextView) findViewById(R.id.search_ll_model_text);
                t.setText("Любая");

                sPref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
                ed = sPref.edit();
                ed.putString("SelectedModel","Любая").commit();
                break;
        }
        return;
    }
    public void onClickMarkorModel(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.search_ll_mark_cardview :
                //Button b = (Button) findViewById(R.id.search_ll_mark_clear);
                //b.setVisibility(View.VISIBLE); //в новое активити перенести это
                intent = new Intent(this, MarkFilter.class);
                String[] marks_arr = {"Ауди", "БМВ"};
                intent.putExtra("Marks",marks_arr);
                startActivity(intent);
                break;
            case R.id.search_ll_model_cardview :
                //Button b = (Button) findViewById(R.id.search_ll_mark_clear);
                //b.setVisibility(View.VISIBLE); //в новое активити перенести это
                intent = new Intent(this, MarkFilter.class);
                String[] models_arr = {"A4", "A6"};
                intent.putExtra("Models",models_arr);
                startActivity(intent);
                break;
        }
    }

    public void onClickSpinner(View v){
        Spinner sp;
        switch (v.getId()){
            case R.id.spinner_label_year_from :


                break;
        }
    }

    public void fillSpinner(Spinner sp,String[] data){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_spn, data);
        adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
        sp.setAdapter(adapter);
        sp.setEnabled(true);


        sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner spinner, View view, int i, long l) {
                Log.d("spinner", (String) spinner.getSelectedItem().toString());
            }
        });


    }


    public void setNavigationDrawerItem(int itemNumber) {
        itemSelectFromTabLayout = true;
        mNavigationDrawerFragment.selectItem(itemNumber);
    }
}
