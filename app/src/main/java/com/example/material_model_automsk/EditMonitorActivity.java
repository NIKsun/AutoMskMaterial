package com.example.material_model_automsk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Никита on 16.10.2015.
 */
public class EditMonitorActivity extends FragmentActivity {
    SearchFragment searchFragment;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_monitor);

        FrameLayout header = (FrameLayout)findViewById(R.id.title);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1"))
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        else
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));

        Log.d("filterID", String.valueOf(getIntent().getIntExtra("filterID",-1)));
        searchFragment = (SearchFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

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
        finish();
    }
}
