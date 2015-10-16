package com.example.material_model_automsk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

/**
 * Created by Никита on 16.10.2015.
 */
public class EditMonitorActivity extends FragmentActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_monitor);

        TextView tvHeader = (TextView)findViewById(R.id.text_view_title);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1"))
            tvHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        else
            tvHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
    }
}
