package com.example.material_model_automsk;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.Switch;

/**
 * Created by Никита on 24.09.2015.
 */
public class SearchFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    public static SearchFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        SharedPreferences sPref = getActivity().getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("SelectedMark","Любая").commit();
        ed.putString("SelectedModel","Любая").commit();
    }

    public void onDestroy(){
        super.onDestroy();


    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sPref = getActivity().getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);

        String mark = sPref.getString("SelectedMark", "Любая");
        String model = sPref.getString("SelectedModel", "Любая");

        TextView t = (TextView) getActivity().findViewById(R.id.search_ll_mark_text);
        t.setText(mark);

        TextView t2 = (TextView) getActivity().findViewById(R.id.search_ll_model_text);
        t2.setText(model);

        CardView ll = (CardView) getActivity().findViewById(R.id.search_ll_model_cardview);
        if(!mark.equals("Любая"))
        {
            Button b = (Button) getActivity().findViewById(R.id.search_ll_mark_clear);
            b.setVisibility(View.VISIBLE);
            ll.setVisibility(View.VISIBLE);
        }
        else
            ll.setVisibility(View.GONE);
        if(!model.equals("Любая"))
        {
            Button b2 = (Button) getActivity().findViewById(R.id.search_ll_model_clear);
            b2.setVisibility(View.VISIBLE);
        }
        else
        {
            Button b3 = (Button) getActivity().findViewById(R.id.search_ll_model_clear);
            b3.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        android.widget.Button addMonitorButton = ((MainActivity)getActivity()).getAddMonitorButton();
        addMonitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        super.onPositiveActionClicked(fragment);
                        Filter filter = new Filter();
                        String from = "";
                        String to = "";
                        Log.d("sp", String.valueOf(((Spinner) view.findViewById(R.id.spinner_label_year_from)).getSelectedItemPosition()));

                        if((((Spinner) view.findViewById(R.id.spinner_label_year_from)).getSelectedItemPosition()==-1))
                                    filter.setYear(from,to);
                        else
                        {
                            if((((Spinner) view.findViewById(R.id.spinner_label_year_from)).getSelectedItemPosition()!= 0))
                                from = ((Spinner) view.findViewById(R.id.spinner_label_year_from)).getSelectedItem().toString();
                            if((((Spinner) view.findViewById(R.id.spinner_label_year_to)).getSelectedItemPosition()!= 0))
                                to = ((Spinner) view.findViewById(R.id.spinner_label_year_to)).getSelectedItem().toString();
                        }
                        filter.setYear(from,to);

                        from = "";
                        to = "";
                        if((((Spinner) view.findViewById(R.id.spinner_label_mileage_from)).getSelectedItemPosition()==-1))
                            filter.setMilleage(from,to);
                        else
                        {
                            if((((Spinner) view.findViewById(R.id.spinner_label_mileage_from)).getSelectedItemPosition()!= 0))
                                from = ((Spinner) view.findViewById(R.id.spinner_label_mileage_from)).getSelectedItem().toString();
                            if((((Spinner) view.findViewById(R.id.spinner_label_mileage_to)).getSelectedItemPosition()!= 0))
                                to = ((Spinner) view.findViewById(R.id.spinner_label_mileage_to)).getSelectedItem().toString();
                        }
                        filter.setMilleage(from, to);

                        from = "";
                        to = "";
                        if((((Spinner) view.findViewById(R.id.spinner_label_price_from)).getSelectedItemPosition()==-1))
                            filter.setPrice(from,to);
                        else
                        {
                            if((((Spinner) view.findViewById(R.id.spinner_label_price_from)).getSelectedItemPosition()!= 0))
                                from = ((Spinner) view.findViewById(R.id.spinner_label_price_from)).getSelectedItem().toString();
                            if((((Spinner) view.findViewById(R.id.spinner_label_price_to)).getSelectedItemPosition()!= 0))
                                to = ((Spinner) view.findViewById(R.id.spinner_label_price_to)).getSelectedItem().toString();
                        }
                        filter.setPrice(from,to);

                        from = "";
                        to = "";
                        if((((Spinner) view.findViewById(R.id.spinner_label_engine_volume_from)).getSelectedItemPosition()==-1))
                            filter.setVolume(from,to);
                        else
                        {
                            if((((Spinner) view.findViewById(R.id.spinner_label_engine_volume_from)).getSelectedItemPosition()!= 0))
                                from = ((Spinner) view.findViewById(R.id.spinner_label_engine_volume_from)).getSelectedItem().toString();
                            if((((Spinner) view.findViewById(R.id.spinner_label_engine_volume_to)).getSelectedItemPosition()!= 0))
                                to = ((Spinner) view.findViewById(R.id.spinner_label_engine_volume_to)).getSelectedItem().toString();
                        }
                        filter.setVolume(from,to);

                        filter.mark = ((TextView) view.findViewById(R.id.search_ll_mark_text)).getText().toString();
                        if(!filter.mark.equals("Любая"))
                            filter.model = ((TextView) view.findViewById(R.id.search_ll_model_text)).getText().toString();
                        else
                            filter.model = "Любая";

                        filter.withPhoto = ((Switch) view.findViewById(R.id.search_ll_withPhoto)).isChecked();

                        filter.transmission="";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_trans_man)).isChecked())
                            filter.transmission+="2";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_trans_auto)).isChecked())
                            filter.transmission+="1";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_trans_robot)).isChecked())
                            filter.transmission+="3";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_trans_var)).isChecked())
                            filter.transmission+="4";

                        filter.typeOfEngine="";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_engine_type_gasoline)).isChecked())
                            filter.typeOfEngine+="1";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_engine_type_diesel)).isChecked())
                            filter.typeOfEngine+="2";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_engine_type_hybrid)).isChecked())
                            filter.typeOfEngine+="3";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_engine_type_gas)).isChecked())
                            filter.typeOfEngine+="4";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_engine_type_electro)).isChecked())
                            filter.typeOfEngine+="5";

                        filter.typeOfWheelDrive="";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_drive_forward)).isChecked())
                            filter.typeOfWheelDrive+="1";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_drive_backward)).isChecked())
                            filter.typeOfWheelDrive+="2";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_drive_full)).isChecked())
                            filter.typeOfWheelDrive+="3";

                        filter.typeOfCarcase="";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_sed)).isChecked())
                            filter.typeOfCarcase+="1";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_hatch)).isChecked())
                            filter.typeOfCarcase+="2";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_univ)).isChecked())
                            filter.typeOfCarcase+="3";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_minivan)).isChecked())
                            filter.typeOfCarcase+="5";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_offroad)).isChecked())
                            filter.typeOfCarcase+="4";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_coupe)).isChecked())
                            filter.typeOfCarcase+="7";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_van)).isChecked())
                            filter.typeOfCarcase+="9";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_limus)).isChecked())
                            filter.typeOfCarcase+="6";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_picap)).isChecked())
                            filter.typeOfCarcase+="0";
                        if(((com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_cabrio)).isChecked())
                            filter.typeOfCarcase+="8";

                        filter.insertToDb(getContext());

                        filter.getHref(getContext());

                        Monitor monitor = new Monitor(filter,getContext());
                        monitor.insertToDb(getContext());
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                builder.message("Будет создан новый монитор с текущими настройками поиска. " +
                        "Мониторы помогают сохранять настройки поиска и отслеживать поступление новых объявлений по этим настройкам.")
                        .title("Создать новый монитор?")
                        .positiveAction("Создать")
                        .negativeAction("Нет");
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getActivity().getSupportFragmentManager(), null);
            }
        });
        //TextView textView = (TextView) view;
        //textView.setText("Fragment #" + mPage);
        return view;
    }


}
