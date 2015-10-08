package com.example.material_model_automsk;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.SnackBar;
import com.rey.material.widget.Switch;

import java.util.List;

/**
 * Created by Никита on 24.09.2015.
 */
class Filter {
    Integer id;
    String mark;
    String model;
    String priceFrom;
    String priceTo;
    String yearFrom;
    String yearTo;
    String milleageFrom;
    String milleageTo;
    String volumeFrom;
    String volumeTo;
    String transmission;
    String typeOfEngine;
    String typeOfCarcase;
    String typeOfWheelDrive;
    Boolean withPhoto;

    private String getRangeString(String name, String value, String from, String to)
    {
        String message = "";
        if(from != null && from == to)
            return name+":\t только " + from + " " + value + "\n";
        if(from != null) {
            message += name+":\t от " + from + " " + value;
            if (to != null)
                message += " до " + to + " " + value;
        }
        else if (to != null)
            message += name+":\t до " + to + " " + value;
        if(!message.isEmpty())
            message += "\n";
        return message;
    }
    String getMessage()
    {
        String message = "";
        message += getRangeString("Цена", "руб.", priceFrom, priceTo);
        message += getRangeString("Год", "г.", yearFrom, yearTo);
        message += getRangeString("Пробег", "км", milleageFrom, milleageTo);
        message += getRangeString("Объем", "л", volumeFrom, volumeTo);
        return message;
    }

    Filter() {
    }
    void setPrice(CharSequence from, CharSequence to){
        priceFrom = (String) from;
        priceTo = (String) to;
    }
    void setYear(CharSequence from, CharSequence to){
        yearFrom = (String) from;
        yearTo = (String) to;
    }
    void setMilleage(CharSequence from, CharSequence to){
        milleageFrom = (String) from;
        milleageTo = (String) to;
    }
    void setVolume(CharSequence from, CharSequence to){
        volumeFrom = (String) from;
        volumeTo = (String) to;
    }

    void insertToDb(Context context){

        final DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        //cv.put("id", models_split[i]);
        cv.put("marka", this.mark);
        cv.put("model", this.model);
        cv.put("yearFrom", this.yearFrom);
        cv.put("yearTo", this.yearTo);

        cv.put("priceFrom", this.priceFrom);
        cv.put("priceTo", this.priceTo);
        cv.put("milleageFrom", this.milleageFrom);
        cv.put("milleageTo", this.milleageTo);

        cv.put("volumeFrom", this.volumeFrom);
        cv.put("volumeTo", this.volumeTo);
        cv.put("transmission", this.transmission);
        cv.put("bodyType", this.typeOfCarcase);

        cv.put("engineType", this.typeOfEngine);
        cv.put("withPhoto", this.withPhoto ? 1 : 0);
        cv.put("driveType", this.typeOfWheelDrive);
        db.insert("filters", null, cv);
        db.close();
        db = dbHelper.getWritableDatabase();
        Cursor cur = db.query("filters", new String[]{"id"}, null, null, null, null, null, null);
        cur.moveToLast();
        id = cur.getInt(cur.getColumnIndex("id"));

        db.close();
    }
}

class Monitor {
    Integer id;
    Filter filter;
    Boolean isActive;
    Integer countOfNewCars;

    Monitor(Filter filter) {
        isActive = true;
        this.filter = filter;
        countOfNewCars = 0;
    }

    void insertToDb(Context context)
    {
        final DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("filter_id", filter.id);
        cv.put("count_of_new_cars", countOfNewCars);
        cv.put("is_active", isActive ? 1 : 0);
        db.insert("monitors", null, cv);
        db.close();
    }
}

public class MonitorCardAdapter extends RecyclerView.Adapter<MonitorCardAdapter.MonitorViewHolder>{

    public static class MonitorViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView monitorStatus;
        TextView monitorMarkAndModel;
        TextView monitorFilterInfo;
        TextView monitorCountOfNewCars;
        Switch monitorSwitch;
        ImageView iv;
        LinearLayout ll;

        MonitorViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_mon);
            monitorStatus = (TextView)itemView.findViewById(R.id.cv_mon_status);
            monitorMarkAndModel = (TextView)itemView.findViewById(R.id.cv_mon_mark_and_model);
            monitorFilterInfo = (TextView)itemView.findViewById(R.id.cv_mon_filter_info);
            monitorCountOfNewCars = (TextView)itemView.findViewById(R.id.cv_mon_count_of_new_cars);
            monitorSwitch = (Switch)itemView.findViewById(R.id.cv_mon_switch_status);
            ll = (LinearLayout)itemView.findViewById(R.id.cv_mon_lin_lay_clickable);
            iv = (ImageView)itemView.findViewById(R.id.cv_mon_popup);
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
        MonitorViewHolder mvh = new MonitorViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(final MonitorViewHolder monitorViewHolder, final int i) {
        if(i == getItemCount()-1) {
            monitorViewHolder.ll.setMinimumHeight(0);
            monitorViewHolder.ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
            monitorViewHolder.cv.setVisibility(View.INVISIBLE);
            return;
        }

        monitorViewHolder.cv.setVisibility(View.VISIBLE);
        monitorViewHolder.ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Resources resources = monitorViewHolder.monitorStatus.getContext().getResources();

        monitorViewHolder.monitorMarkAndModel.setText(monitors.get(i).filter.mark + " " + monitors.get(i).filter.model);
        monitorViewHolder.monitorMarkAndModel.setTypeface(null, Typeface.BOLD);
        monitorViewHolder.monitorFilterInfo.setText(monitors.get(i).filter.getMessage());

        monitorViewHolder.monitorSwitch.setChecked(monitors.get(i).isActive);
        monitorViewHolder.monitorSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch aSwitch, boolean b) {
                Resources resources = monitorViewHolder.monitorStatus.getContext().getResources();
                if (b) {
                    monitorViewHolder.monitorStatus.setTextColor(resources.getColor(R.color.myPrimaryDarkColor));
                    monitorViewHolder.monitorStatus.setText(resources.getText(R.string.monitor_is_active));
                } else {
                    monitorViewHolder.monitorStatus.setTextColor(resources.getColor(R.color.colorPrimaryQuarter));
                    monitorViewHolder.monitorStatus.setText(resources.getText(R.string.monitor_is_not_active));
                }
            }
        });


        monitorViewHolder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(monitorViewHolder.iv.getContext(), monitorViewHolder.iv);
                popup.getMenu().add(R.string.popup_edit);
                popup.getMenu().add(R.string.popup_delete);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });
            }
        });

        if(monitors.get(i).isActive) {
            monitorViewHolder.monitorStatus.setTextColor(resources.getColor(R.color.myPrimaryDarkColor));
            monitorViewHolder.monitorStatus.setText(resources.getText(R.string.monitor_is_active));
        }
        else {
            monitorViewHolder.monitorStatus.setTextColor(resources.getColor(R.color.colorPrimaryQuarter));
            monitorViewHolder.monitorStatus.setText(resources.getText(R.string.monitor_is_not_active));
        }

       if (monitors.get(i).countOfNewCars == 0)
            monitorViewHolder.monitorCountOfNewCars.setVisibility(View.INVISIBLE);
        else {
            monitorViewHolder.monitorCountOfNewCars.setVisibility(View.VISIBLE);
            if (monitors.get(i).countOfNewCars < 10)
                monitorViewHolder.monitorCountOfNewCars.setText(" " + String.valueOf(monitors.get(i).countOfNewCars) + " ");
            else if (monitors.get(i).countOfNewCars < 100)
                monitorViewHolder.monitorCountOfNewCars.setText(String.valueOf(monitors.get(i).countOfNewCars));
            else {
                monitorViewHolder.monitorCountOfNewCars.setTextSize(23);
                monitorViewHolder.monitorCountOfNewCars.setPadding(7, 10, 7, 10);
                monitorViewHolder.monitorCountOfNewCars.setText("99+");
            }
        }

        monitorViewHolder.ll.setOnClickListener(new View.OnClickListener() {
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
                    SnackBar mSnackBar = ((MainActivity)v.getContext()).getSnackBar();

                    if(v.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        mSnackBar.applyStyle(R.style.SnackBarSingleLine);
                        mSnackBar.show();
                    }
                    else
                    {
                        mSnackBar.applyStyle(R.style.Material_Widget_SnackBar_Tablet_MultiLine);
                        mSnackBar.text("Нет удалось подключиться к серверу. Проверьте соеденение с интернетом.")
                        .actionText("Ок")
                        .duration(4000)
                        .show();
                    }
                }
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
