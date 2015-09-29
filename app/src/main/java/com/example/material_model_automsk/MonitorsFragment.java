package com.example.material_model_automsk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rey.material.widget.Button;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitors, container, false);
        RecyclerView rv = (RecyclerView)view.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        rv.setLayoutManager(llm);
        initializeData();
        MonitorCardAdapter adapter = new MonitorCardAdapter(monitors);
        rv.setAdapter(adapter);
        return view;
    }


    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeData(){
        monitors = new ArrayList<>();
        monitors.add(new Monitor(new Filter("Audi","A3", null, 300000),true,4));
        monitors.add(new Monitor(new Filter("Audi","A4", 150000, 250000),true,0));
        monitors.add(new Monitor(new Filter("Ford","Focus", null, null),true,12));
        monitors.add(new Monitor(new Filter("ВАЗ","Granta", null, 200000),true,42));
        monitors.add(new Monitor(new Filter("Audi","A3", null, 300000)));
        monitors.add(new Monitor(new Filter("Audi","A4", 150000, 250000)));
        monitors.add(new Monitor(new Filter("Ford","Focus", null, null),true,142));
        monitors.add(new Monitor(new Filter("ВАЗ","Granta", null, 200000)));
    }



}
