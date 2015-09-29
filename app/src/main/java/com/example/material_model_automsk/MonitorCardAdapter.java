package com.example.material_model_automsk;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rey.material.widget.SnackBar;

import java.util.List;

/**
 * Created by Никита on 24.09.2015.
 */
class Filter {
    Integer id;
    String mark;
    String model;
    Integer priceFrom;
    Integer priceTo;

    Filter(String mark, String model, Integer priceFrom, Integer priceTo) {
        this.mark = mark;
        this.model = model;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
    }
}

class Monitor {
    Integer id;
    Filter filter;
    Boolean isActive;
    Integer countOfNewCars;

    Monitor(Filter filter) {
        this.filter = filter;
        this.isActive = false;
        this.countOfNewCars = 0;
    }
    Monitor(Filter filter, Boolean isActive, Integer numberOfNewCars) {
        this.filter = filter;
        this.isActive = isActive;
        this.countOfNewCars = numberOfNewCars;
    }
}

public class MonitorCardAdapter extends RecyclerView.Adapter<MonitorCardAdapter.MonitorViewHolder>{

    public static class MonitorViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView monitorMark;
        TextView monitorModel;
        TextView monitorCountOfNewCars;

        MonitorViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            monitorMark = (TextView)itemView.findViewById(R.id.mark);
            monitorModel = (TextView)itemView.findViewById(R.id.model);
            monitorCountOfNewCars = (TextView)itemView.findViewById(R.id.count_of_new_cars);
        }
    }

    List<Monitor> monitors;

    MonitorCardAdapter(List<Monitor> monitors){
        this.monitors = monitors;
    }

    @Override
    public int getItemCount() {
        return monitors.size();
    }

    @Override
    public MonitorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_monitor, viewGroup, false);
        MonitorViewHolder pvh = new MonitorViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final MonitorViewHolder monitorViewHolder, final int i) {
        monitorViewHolder.monitorMark.setText(monitors.get(i).filter.mark);
        monitorViewHolder.monitorModel.setText(monitors.get(i).filter.model);

        if(monitors.get(i).countOfNewCars == 0)
            monitorViewHolder.monitorCountOfNewCars.setVisibility(View.INVISIBLE);
        else {
            monitorViewHolder.monitorCountOfNewCars.setVisibility(View.VISIBLE);
            if(monitors.get(i).countOfNewCars<10)
                monitorViewHolder.monitorCountOfNewCars.setText(" " + String.valueOf(monitors.get(i).countOfNewCars)+" ");
            else if(monitors.get(i).countOfNewCars<100)
                monitorViewHolder.monitorCountOfNewCars.setText(String.valueOf(monitors.get(i).countOfNewCars));
            else {
                monitorViewHolder.monitorCountOfNewCars.setTextSize(23);
                monitorViewHolder.monitorCountOfNewCars.setPadding(7,10,7,10);
                monitorViewHolder.monitorCountOfNewCars.setText("99+");
            }
        }

        monitorViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm =
                        (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if(netInfo != null && netInfo.isConnectedOrConnecting()) {
                    Intent intent = new Intent(v.getContext(), ListOfCarsActivity.class);
                    intent.putExtra("FilterID", i);
                    v.getContext().startActivity(intent);
                }
                else
                {
                    SnackBar mSnackBar = ((ListOfMonitorsActivity)v.getContext()).getSnackBar();

                    if(v.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                        mSnackBar.applyStyle(R.style.SnackBarSingleLine).show();
                    else
                        mSnackBar.applyStyle(R.style.Material_Widget_SnackBar_Tablet_MultiLine)
                                .text("Нет удалось подключиться к серверу. Проверьте соеденение с интернетом.")
                                .actionText("Ок")
                                .duration(4000)
                                .show();
                }
            }
        });


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
