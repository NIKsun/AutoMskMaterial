package com.example.material_model_automsk;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.rey.material.widget.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Никита on 24.09.2015.
 */
public class MonitorsFragment extends Fragment {

    public static MonitorsFragment newInstance(int page) {
        MonitorsFragment fragment = new MonitorsFragment();
        return fragment;
    }

    private List<Monitor> monitors;
    FloatingActionButton fab;
    Boolean isHidden = false;
    LinearLayoutManager llm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitors, container, false);
        final RecyclerView rv = (RecyclerView)view.findViewById(R.id.rv);
        llm = new LinearLayoutManager(view.getContext());
        rv.setLayoutManager(llm);
        initializeData();
        MonitorCardAdapter adapter = new MonitorCardAdapter(monitors);
        rv.setAdapter(adapter);

        fab = (FloatingActionButton)view.findViewById(R.id.fab_line);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonitorCardAdapter mca = (MonitorCardAdapter)rv.getAdapter();
                mca.setVisibility();
                for(int i=0;i<mca.getItemCount();i++)
                    mca.notifyItemChanged(i);
            }
        });
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged (RecyclerView recyclerView, int newState){
                if(newState==0 && isHidden) {
                    Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_translate_top);
                    fab.startAnimation(anim);
                    fab.setVisibility(View.VISIBLE);

                    isHidden = false;
                }
                else if(!isHidden) {
                    Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_translate_buttom);
                    fab.startAnimation(anim);
                    fab.setVisibility(View.INVISIBLE);
                    isHidden = true;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled (RecyclerView recyclerView,int dx, int dy){
                super.onScrolled(recyclerView,dx,dy);
            }

        });

        return view;
    }



    private void initializeData(){
        monitors = new ArrayList<>();

        SQLiteDatabase db = new DbHelper(getActivity()).getWritableDatabase();
        Cursor cursor = db.query("filters", null, null, null, null, null, null);

        int iMark = cursor.getColumnIndex("marka");
        int iModel = cursor.getColumnIndex("model");
        int iYearFrom = cursor.getColumnIndex("yearFrom");
        int iYearTo = cursor.getColumnIndex("yearTo");
        int iPriceFrom = cursor.getColumnIndex("priceFrom");
        int iPriceTo = cursor.getColumnIndex("priceTo");
        int iMilleageFrom = cursor.getColumnIndex("milleageFrom");
        int iMilleageTo = cursor.getColumnIndex("milleageTo");
        int iVolumeFrom = cursor.getColumnIndex("volumeFrom");
        int iVolumeTo = cursor.getColumnIndex("volumeTo");
        int iTransmission = cursor.getColumnIndex("transmission");
        int iBodyType = cursor.getColumnIndex("bodyType");
        int iEngineType = cursor.getColumnIndex("engineType");
        int iDriveType = cursor.getColumnIndex("driveType");
        int iWithPhoto = cursor.getColumnIndex("withPhoto");

        monitors = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Filter elem = new Filter();
                elem.mark = cursor.getString(iMark);
                elem.model = cursor.getString(iModel);

                elem.setYear(cursor.getString(iYearFrom), cursor.getString(iYearTo));
                elem.setMilleage(cursor.getString(iMilleageFrom), cursor.getString(iMilleageTo));
                elem.setPrice(cursor.getString(iPriceFrom), cursor.getString(iPriceTo));
                elem.setVolume(cursor.getString(iVolumeFrom), cursor.getString(iVolumeTo));

                elem.transmission = cursor.getString(iTransmission);
                elem.typeOfCarcase = cursor.getString(iBodyType);
                elem.typeOfEngine = cursor.getString(iEngineType);
                elem.typeOfWheelDrive = cursor.getString(iDriveType);
                if(cursor.getInt(iWithPhoto) == 1)
                    elem.withPhoto = true;
                else
                    elem.withPhoto = false;

            } while (cursor.moveToNext());
        }
        db.close();
    }



}
