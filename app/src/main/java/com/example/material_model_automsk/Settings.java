package com.example.material_model_automsk;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.SnackBar;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Alex on 03.10.2015.
 */

public class Settings extends ActionBarActivity implements View.OnClickListener, NavigationDrawerCallbacks
{

    private NavigationDrawerFragment mNavigationDrawerFragment;
    ViewPager viewPager;
    final String LOG_TAG = "myLogs";
    Dialog dialog;
    TabLayout tabLayout;
    private SnackBar mSnackBar;
    com.rey.material.widget.Button addMonitorButton;
    int radio_button_checked = 1;
    boolean visible = false;
    private android.support.v7.widget.Toolbar mToolbar;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.init(this, 2, 0, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1")) {
            setTheme(R.style.AppTheme);
            radio_button_checked = 1;
        }
        else if (themeName.equals("2")) {
            setTheme(R.style.AppTheme2);
            radio_button_checked = 2;
        }

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.settings);
    }

    public void setRadio_button_checked(int i)
    {
        ((com.rey.material.widget.RadioButton) findViewById(R.id.radio_button_1)).setChecked(false);
        ((com.rey.material.widget.RadioButton) findViewById(R.id.radio_button_2)).setChecked(false);
        if (i==1)
            ((com.rey.material.widget.RadioButton) findViewById(R.id.radio_button_1)).setChecked(true);
        if (i== 2)
            ((com.rey.material.widget.RadioButton) findViewById(R.id.radio_button_2)).setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_theme:
                LinearLayout Theme = (LinearLayout) findViewById(R.id.change_theme_hidden);
                if (visible == false)  // Если тема первый раз открывается
                {
                    visible=true;
                    Theme.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    setRadio_button_checked(radio_button_checked);
                }
                else
                {
                    visible=false;
                    setRadio_button_checked(radio_button_checked);
                    Theme.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                }
                break;

            case R.id.radio_button_1:
                if (radio_button_checked == 1)
                    break;
                showDialogTheme(1);
                break;

            case R.id.radio_button_2:
                if (radio_button_checked == 2)
                    break;
                showDialogTheme(2);
                break;
        }
        return;
    }

    public void showDialogTheme(final int themeNumber) {
        AlertDialog.Builder ad;
        setRadio_button_checked(themeNumber);
        ad = new AlertDialog.Builder(Settings.this);
        setRadio_button_checked(themeNumber);
        ad.setTitle("Изменение темы");
        ad.setMessage("Для измененения темы необходимо перезапустить приложение.");
        ad.setPositiveButton("Перезапустить приложение", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //finish();
                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(Settings.this);
                SharedPreferences.Editor ed = pref.edit();

                switch (themeNumber) {
                    case 1: {
                        if(radio_button_checked == themeNumber)
                            break;
                        ((com.rey.material.widget.RadioButton) findViewById(R.id.radio_button_2)).setChecked(false);
                        ed.putString("theme", "1"); //etText.getText().toString()
                        break;
                    }
                    case 2: {
                        if(radio_button_checked == themeNumber)
                            break;
                        ((com.rey.material.widget.RadioButton) findViewById(R.id.radio_button_1)).setChecked(false);
                        ed.putString("theme", "2"); //etText.getText().toString()
                        break;
                    }
                }
                ed.commit();
                System.exit(0);
            }
        });
        ad.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                setRadio_button_checked(radio_button_checked);
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                setRadio_button_checked(radio_button_checked);
            }
        });

        ad.show();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
    }

    public SnackBar getSnackBar(){
        return mSnackBar;
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }


}
