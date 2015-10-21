package com.develop.autorus;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.RadioButton;

/**
 * Created by Alex on 03.10.2015.
 */

public class SettingsFragment extends Fragment
{
    int radio_button_checked = 1;
    boolean visible = false;
    boolean visibleNotification = false;
    View savedView;
    RadioButton rb1,rb2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.init(getActivity(), 2, 0, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1")) {
            getActivity().setTheme(R.style.AppTheme);
            radio_button_checked = 1;
        }
        else if (themeName.equals("2")) {
            getActivity().setTheme(R.style.AppTheme2);
            radio_button_checked = 2;
        }

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedView = inflater.inflate(R.layout.fragment_settings, container, false);
        LinearLayout changeTheme = (LinearLayout)savedView.findViewById(R.id.change_theme);

        setRadio_button_checked(radio_button_checked);
        rb1 = (RadioButton)savedView.findViewById(R.id.radio_button_1);
        rb2 = (RadioButton)savedView.findViewById(R.id.radio_button_2);

        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //showDialogTheme(1);
            }
        });
        rb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //showDialogTheme(2);
            }

        });

        final com.rey.material.widget.Switch changeNotification = (com.rey.material.widget.Switch) savedView.findViewById(R.id.cv_notification_switch);
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        changeNotification.setChecked(sPref.getBoolean("notificationIsActive", true));
        final LinearLayout notificationLayout = (LinearLayout) savedView.findViewById(R.id.notification_hidden);
        if (changeNotification.isChecked())
            notificationLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        else
            notificationLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));

        changeNotification.setOnCheckedChangeListener(new com.rey.material.widget.Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(com.rey.material.widget.Switch aSwitch, boolean b) {
                AlarmManager am = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
                Intent serviceIntent = new Intent(getActivity().getApplicationContext(), MonitoringWork.class);
                PendingIntent pIntent = PendingIntent.getService(getActivity().getApplicationContext(), 0, serviceIntent, 0);

                if (changeNotification.isChecked()) {
                    int period = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("numberOfActiveMonitors", 0) * 180000;
                    if(period != 0)
                        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + period, period, pIntent);
                    SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean("notificationIsActive", true);
                    ed.commit();

                    MainActivity.expand(notificationLayout);
                } else {
                    am.cancel(pIntent);
                    SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean("notificationIsActive", false);
                    ed.commit();

                    MainActivity.collapse(notificationLayout);
                }
            }

        });

        final com.rey.material.widget.Switch switchVibration= (com.rey.material.widget.Switch) savedView.findViewById(R.id.cv_vibration_switch_status);
        switchVibration.setChecked(sPref.getBoolean("vibrationIsActive", false));
        switchVibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchVibration.isChecked())
                {
                    SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean("vibrationIsActive", true);
                    ed.commit();
                }
                else {
                    SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean("vibrationIsActive", false);
                    ed.commit();
                }
            }
        });

        final com.rey.material.widget.Switch switchSound= (com.rey.material.widget.Switch) savedView.findViewById(R.id.cv_sound_switch);
        switchSound.setChecked(sPref.getBoolean("soundIsActive", false));
        switchSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchSound.isChecked())
                {
                    SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean("soundIsActive", true);
                    ed.commit();
                }
                else {
                    SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean("soundIsActive", false);
                    ed.commit();
                }
            }
        });
        /*
        Switch sw = (Switch)savedView.findViewById(R.id.cv_notification_switch_status);
        sw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch aSwitch, boolean b) {
                if (aSwitch.isChecked()==true)
                    //Включть нотификацию
                    return;
                else
                    //Отключить нотификацию.
                    return;
            }
        });
        */
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (rb1 == buttonView)
                    {
                        if (radio_button_checked != 1)
                            showDialogTheme(1);
                    }

                    if (rb2 == buttonView)
                    {
                        if (radio_button_checked != 2)
                            showDialogTheme(2);
                    }
                    rb1.setChecked(rb1 == buttonView);
                    rb2.setChecked(rb2 == buttonView);
                }

            }

        };

        rb1.setOnCheckedChangeListener(listener);
        rb2.setOnCheckedChangeListener(listener);

        return savedView;
    }
    public void setRadio_button_checked(int i)
    {
        ((com.rey.material.widget.RadioButton) savedView.findViewById(R.id.radio_button_1)).setCheckedImmediately(false);
        ((com.rey.material.widget.RadioButton) savedView.findViewById(R.id.radio_button_2)).setCheckedImmediately(false);
        if (i==1)
            ((com.rey.material.widget.RadioButton) savedView.findViewById(R.id.radio_button_1)).setCheckedImmediately(true);
        if (i== 2)
            ((com.rey.material.widget.RadioButton) savedView.findViewById(R.id.radio_button_2)).setCheckedImmediately(true);
    }

    public void showDialogTheme(final int themeNumber) {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor ed = pref.edit();

        switch (themeNumber) {
            case 1: {
                if(radio_button_checked == themeNumber)
                    break;
                ((com.rey.material.widget.RadioButton) savedView.findViewById(R.id.radio_button_2)).setChecked(false);
                ed.putString("theme", "1"); //etText.getText().toString()
                break;
            }
            case 2: {
                if(radio_button_checked == themeNumber)
                    break;
                ((com.rey.material.widget.RadioButton) savedView.findViewById(R.id.radio_button_1)).setChecked(false);
                ed.putString("theme", "2"); //etText.getText().toString()
                break;
            }
        }
        radio_button_checked=themeNumber;
        ((MainActivity)getActivity()).getTracker().send(new HitBuilders.EventBuilder().setCategory("Change theme").setAction(String.valueOf(themeNumber)).setValue(1).build());

        Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ed.putInt("NumberOfCallingFragment", 3);
        ed.commit();
        startActivity(i);
    }
}
