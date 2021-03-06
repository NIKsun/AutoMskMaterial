package com.develop.autorus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.analytics.HitBuilders;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.FloatingActionButton;

/**
 * Created by Alex on 05.10.2015.
 */
public class FeedbackFragment extends android.support.v4.app.Fragment {
    View savedView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.init(getActivity(), 2, 0, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1")) {
            getActivity().setTheme(R.style.AppTheme);
        }
        else if (themeName.equals("2")) {
            getActivity().setTheme(R.style.AppTheme2);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedView = inflater.inflate(R.layout.fragment_feedback, container, false);

        final FloatingActionButton fab = (FloatingActionButton) savedView.findViewById(R.id.send_feedback);
        final Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_simple_grow);
        fab.startAnimation(anim);
        fab.setVisibility(View.VISIBLE);
        fab.setIcon(getResources().getDrawable(R.drawable.ic_mode_edit_white_24dp), false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1")) {
            fab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else
            fab.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).getTracker().send(new HitBuilders.EventBuilder().setCategory("Feedback").setAction("open").setValue(1).build());

                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "room530support@yandex.ru", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Поддержке Авто Русь");
                startActivity(Intent.createChooser(intent, "Выберите почтового клиента:"));
            }
        });

        return savedView;
    }

}
