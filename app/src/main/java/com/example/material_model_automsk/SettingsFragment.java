package com.example.material_model_automsk;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.rey.material.app.ThemeManager;
import com.rey.material.widget.RadioButton;

/**
 * Created by Alex on 03.10.2015.
 */

public class SettingsFragment extends Fragment
{
    int radio_button_checked = 1;
    boolean visible = false;
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
        changeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout Theme = (LinearLayout) savedView.findViewById(R.id.change_theme_hidden);
                if (!visible)  // Если тема первый раз открывается
                {
                    visible = true;
                    Theme.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    setRadio_button_checked(radio_button_checked);
                } else {
                    visible = false;
                    setRadio_button_checked(radio_button_checked);
                    Theme.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                }
            }
        });
        rb1 = (RadioButton)savedView.findViewById(R.id.radio_button_1);
        rb2 = (RadioButton)savedView.findViewById(R.id.radio_button_2);
        /*rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (radio_button_checked == 1)
                    return;
                showDialogTheme(1);
            }
        });
        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (radio_button_checked == 2)
                    return;
                showDialogTheme(2);
            }
        });
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
        });*/
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
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
        ((com.rey.material.widget.RadioButton) savedView.findViewById(R.id.radio_button_1)).setChecked(false);
        ((com.rey.material.widget.RadioButton) savedView.findViewById(R.id.radio_button_2)).setChecked(false);
        if (i==1)
            ((com.rey.material.widget.RadioButton) savedView.findViewById(R.id.radio_button_1)).setChecked(true);
        if (i== 2)
            ((com.rey.material.widget.RadioButton) savedView.findViewById(R.id.radio_button_2)).setChecked(true);
    }

    public void showDialogTheme(final int themeNumber) {
        AlertDialog.Builder ad;
        setRadio_button_checked(themeNumber);
        ad = new AlertDialog.Builder(getActivity());
        setRadio_button_checked(themeNumber);
        ad.setTitle("Изменение темы");
        ad.setMessage("Для измененения темы необходимо перезапустить приложение.");
        ad.setPositiveButton("Перезапустить приложение", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //finish();
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
}
