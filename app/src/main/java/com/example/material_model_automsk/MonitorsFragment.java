package com.example.material_model_automsk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.rey.material.app.BottomSheetDialog;
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
    LinearLayoutManager llm;
    View savedView;
    boolean isHidden;
    private int activeMonitorCounter;
    AlarmWaiter alarmWaiter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedView = inflater.inflate(R.layout.fragment_monitors, container, false);
        final RecyclerView rv = (RecyclerView)savedView.findViewById(R.id.rv);
        llm = new LinearLayoutManager(savedView.getContext());
        rv.setLayoutManager(llm);
        alarmWaiter = new AlarmWaiter();

        update();

        fab = (FloatingActionButton)savedView.findViewById(R.id.fab_line);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditMonitorActivity.class);
                intent.putExtra("filterID", -1);
                getContext().startActivity(intent);
            }
        });
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 15 || dy < -15) {
                    if (!isHidden) {
                        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_translate_buttom);
                        fab.startAnimation(anim);
                        fab.setVisibility(View.INVISIBLE);
                        isHidden = true;
                    }
                    alarmWaiter.cancel(true);
                    alarmWaiter = new AlarmWaiter();
                    alarmWaiter.execute(400);
                }
                super.onScrolled(recyclerView, dx, dy);
            }

        });

        return savedView;
    }


    class AlarmWaiter extends AsyncTask<Integer, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(final Integer... params) {
            try {
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(Void isNotFound) {
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_translate_top);
            fab.startAnimation(anim);
            fab.setVisibility(View.VISIBLE);
            isHidden = false;
        }
    }

    public void update()
    {
        RecyclerView rv = (RecyclerView)savedView.findViewById(R.id.rv);
        activeMonitorCounter = 0;
        initializeData();
        if(monitors.size() != 1) {
            TextView message =(TextView) savedView.findViewById(R.id.message_about_empty);
            message.setVisibility(View.GONE);
            MonitorCardAdapter adapter = new MonitorCardAdapter(monitors, getActivity(), rv, activeMonitorCounter);
            rv.setAdapter(adapter);
        }
        else {
            TextView message =(TextView) savedView.findViewById(R.id.message_about_empty);
            message.setVisibility(View.VISIBLE);
        }
        if(fab != null) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_simple_grow);
            fab.startAnimation(animation);
            fab.setVisibility(View.VISIBLE);
        }

    }
    public void hideFAB()
    {
        if(fab != null)
            fab.setVisibility(View.INVISIBLE);
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

                if(cursorMonitors.getInt(isActive) == 1) {
                    activeMonitorCounter++;
                    elem.isActive = true;
                }
                else
                    elem.isActive = false;

                SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                sPref.edit().putInt("numberOfActiveMonitors", activeMonitorCounter).commit();

                monitors.add(elem);
            } while (cursorMonitors.moveToNext());
        }
        db.close();
        monitors.add(new Monitor());
    }

    public void removeLastItemFromDb() {
        if(savedView != null) {
            RecyclerView rv = (RecyclerView) savedView.findViewById(R.id.rv);
            if(rv.getAdapter() != null)
                ((MonitorCardAdapter) rv.getAdapter()).finableRemove();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            removeLastItemFromDb();
        }
    }

    @Override
    public void onDestroy() {
        removeLastItemFromDb();
        super.onDestroy();
    }
}
