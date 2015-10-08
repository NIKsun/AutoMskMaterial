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
    private List<Filter> filters;
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
        SQLiteDatabase db = new DbHelper(getActivity()).getWritableDatabase();
        Cursor cursorFilters = db.query("filters", null, null, null, null, null, null);

        int iMark = cursorFilters.getColumnIndex("marka");
        int iModel = cursorFilters.getColumnIndex("model");
        int iYearFrom = cursorFilters.getColumnIndex("yearFrom");
        int iYearTo = cursorFilters.getColumnIndex("yearTo");
        int iPriceFrom = cursorFilters.getColumnIndex("priceFrom");
        int iPriceTo = cursorFilters.getColumnIndex("priceTo");
        int iMilleageFrom = cursorFilters.getColumnIndex("milleageFrom");
        int iMilleageTo = cursorFilters.getColumnIndex("milleageTo");
        int iVolumeFrom = cursorFilters.getColumnIndex("volumeFrom");
        int iVolumeTo = cursorFilters.getColumnIndex("volumeTo");
        int iTransmission = cursorFilters.getColumnIndex("transmission");
        int iBodyType = cursorFilters.getColumnIndex("bodyType");
        int iEngineType = cursorFilters.getColumnIndex("engineType");
        int iDriveType = cursorFilters.getColumnIndex("driveType");
        int iWithPhoto = cursorFilters.getColumnIndex("withPhoto");

        filters = new ArrayList<>();
        if (cursorFilters.moveToFirst()) {
            do {
                Filter elem = new Filter();
                elem.id = cursorFilters.getInt(cursorFilters.getColumnIndex("id"));
                elem.mark = cursorFilters.getString(iMark);
                elem.model = cursorFilters.getString(iModel);

                elem.setYear(cursorFilters.getString(iYearFrom), cursorFilters.getString(iYearTo));
                elem.setMilleage(cursorFilters.getString(iMilleageFrom), cursorFilters.getString(iMilleageTo));
                elem.setPrice(cursorFilters.getString(iPriceFrom), cursorFilters.getString(iPriceTo));
                elem.setVolume(cursorFilters.getString(iVolumeFrom), cursorFilters.getString(iVolumeTo));

                elem.transmission = cursorFilters.getString(iTransmission);
                elem.typeOfCarcase = cursorFilters.getString(iBodyType);
                elem.typeOfEngine = cursorFilters.getString(iEngineType);
                elem.typeOfWheelDrive = cursorFilters.getString(iDriveType);
                if(cursorFilters.getInt(iWithPhoto) == 1)
                    elem.withPhoto = true;
                else
                    elem.withPhoto = false;

                filters.add(elem);
            } while (cursorFilters.moveToNext());
        }

        Cursor cursorMonitors = db.query("monitors", null, null, null, null, null, null);

        int idFilter = cursorMonitors.getColumnIndex("filter_id");
        int iCountOfNewCars = cursorMonitors.getColumnIndex("count_of_new_cars");
        int isActive = cursorMonitors.getColumnIndex("is_active");

        monitors = new ArrayList<>();
        if (cursorMonitors.moveToFirst()) {
            do {
                Monitor elem = new Monitor();
                elem.id = cursorMonitors.getInt(cursorMonitors.getColumnIndex("id"));

                int i=0, filterID = cursorMonitors.getInt(idFilter);
                while(i != filters.size() && filters.get(i).id != filterID)
                    i++;

                if(i == filters.size())
                    continue;

                elem.filter = filters.get(i);

                elem.countOfNewCars = cursorMonitors.getInt(iCountOfNewCars);
                if(cursorMonitors.getInt(isActive) == 1)
                    elem.isActive = true;
                else
                    elem.isActive = false;

                monitors.add(elem);
            } while (cursorMonitors.moveToNext());
        }

        db.close();
    }



}
