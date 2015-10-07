package com.example.material_model_automsk;

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
        /*Filter f = new Filter(1,"Audi","A3");
        f.setPrice(100, 10000);
        f.setYear(2010, null);
        f.setMilleage(null, 100000);
        f.setVolume(5, 5);
        monitors.add(new Monitor(f, false, 0));
        f = new Filter(1,"Audi","A4");
        f.setYear(2010, 2012);
        monitors.add(new Monitor(f, true, 4));
        f = new Filter(1,"Audi","A5");
        monitors.add(new Monitor(f,true,12));
        f = new Filter(1,"Audi","A6");
        f.setPrice(100, 1300000);
        f.setYear(2010, 2012);
        f.setMilleage(42000, 100000);
        f.setVolume(3, 5);
        monitors.add(new Monitor(f, true, 0));
        monitors.add(new Monitor());*/
    }



}
