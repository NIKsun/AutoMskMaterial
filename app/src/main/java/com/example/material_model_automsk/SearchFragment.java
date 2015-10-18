package com.example.material_model_automsk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.Switch;

/**
 * Created by Никита on 24.09.2015.
 */
public class SearchFragment extends Fragment {

    View view;
    Boolean butoonBlock = true;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        fragment.butoonBlock = false;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("lis","createFR");
        super.onCreate(savedInstanceState);
        SharedPreferences sPref = getActivity().getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        Integer isEditFilter = sPref.getInt("isEditFilter",0);
        Log.d("lis", isEditFilter.toString());
        if(isEditFilter!=1) {
            ed.putString("SelectedMark", "Любая").commit();
            ed.putString("SelectedModel", "Любая").commit();
        }
    }

    public void onDestroy(){
        super.onDestroy();


    }

    @Override
    public void onResume(){
        Log.d("lis", "resumeFR");
        super.onResume();
        SharedPreferences sPref = getContext().getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
        //Integer isEditFilter = sPref.getInt("isEditFilter",0);
            String mark = sPref.getString("SelectedMark", "Любая");
            String model = sPref.getString("SelectedModel", "Любая");

            ImageView iv;

            TextView t = (TextView) getActivity().findViewById(R.id.search_ll_mark_text);
            t.setText(mark);

            TextView t2 = (TextView) getActivity().findViewById(R.id.search_ll_model_text);
            t2.setText(model);

            CardView ll = (CardView) getActivity().findViewById(R.id.search_ll_model_cardview);
            if (!mark.equals("Любая")) {
                Button b = (Button) getActivity().findViewById(R.id.search_ll_mark_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) getActivity().findViewById(R.id.arrow_mark);
                iv.setVisibility(View.INVISIBLE);
                ll.setVisibility(View.VISIBLE);
            } else
                ll.setVisibility(View.GONE);
            if (!model.equals("Любая")) {
                Button b2 = (Button) getActivity().findViewById(R.id.search_ll_model_clear);
                b2.setVisibility(View.VISIBLE);
                iv = (ImageView) getActivity().findViewById(R.id.arrow_model);
                iv.setVisibility(View.INVISIBLE);
            } else {
                Button b3 = (Button) getActivity().findViewById(R.id.search_ll_model_clear);
                b3.setVisibility(View.INVISIBLE);
                iv = (ImageView) getActivity().findViewById(R.id.arrow_model);
                iv.setVisibility(View.VISIBLE);
            }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        if(!butoonBlock) {
            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_search);
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_simple_grow);
            fab.startAnimation(anim);
            fab.setVisibility(View.VISIBLE);
            fab.setIcon(getResources().getDrawable(R.drawable.ic_search_white_48dp), false);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectivityManager cm =
                            (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    if (netInfo != null && netInfo.isConnectedOrConnecting()) {

                        Filter filter = new Filter();
                        filter.fillFilter(view);
                        filter.getHref(getContext());
                        filter.insertToDb(getContext());
                        SharedPreferences sPref = getActivity().getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("hrefAutoRu", filter.hrefAuto);
                        ed.putString("hrefAvitoRu", filter.hrefAvito);
                        ed.putString("hrefDromRu", filter.hrefDrom);
                        ed.commit();
                        Intent intent = new Intent(v.getContext(), ListOfCarsActivity.class);
                        intent.putExtra("monitorID", -1);
                        intent.putExtra("filterID", filter.id);
                        v.getContext().startActivity(intent);
                    } else {
                        SnackBar mSnackBar = ((MainActivity)getActivity()).getSnackBar();
                        if (v.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            mSnackBar.applyStyle(R.style.SnackBarSingleLine);
                            mSnackBar.show();
                        } else {
                            mSnackBar.applyStyle(R.style.Material_Widget_SnackBar_Tablet_MultiLine);
                            mSnackBar.text("Нет удалось подключиться к серверу. Проверьте соеденение с интернетом.")
                                    .actionText("Ок")
                                    .duration(4000)
                                    .show();
                        }
                    }
                }
            });

            android.widget.Button addMonitorButton = ((MainActivity) getActivity()).getAddMonitorButton();
            addMonitorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            super.onPositiveActionClicked(fragment);
                            Filter filter = new Filter();
                            filter.fillFilter(view);

                            filter.getHref(getContext());

                            filter.insertToDb(getContext());

                            Monitor monitor = new Monitor(filter, getContext());
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
        }
        return view;
    }

    public void showFAB(){
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_search);
        if(fab != null) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_simple_grow);
            fab.startAnimation(animation);
            fab.setVisibility(View.VISIBLE);
        }
    }
    public void hideFAB(){
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_search);
        if(fab != null)
            fab.setVisibility(View.INVISIBLE);
    }


    public void onClickHandlerHidden(View v){
        LinearLayout ll;
        Button b;
        ImageView iv;

        final Spinner sp1;
        final Spinner sp2;

        final String[] data;
        final String[] data2;



        switch (v.getId()){
            case R.id.search_ll_engine_type:
                v.setClickable(false);

                ll = (LinearLayout)view.findViewById(R.id.search_ll_engine_type_hidden);
                b = (Button) view.findViewById(R.id.search_ll_engine_type_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_engine_type);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                MainActivity.expand(ll);
                break;
            case R.id.search_ll_price:
/*
                v.setClickable(false);

                data = new String[176];

                for(int i=0; i<data.length; ++i){
                    if(i<51){
                        data[i]= String.valueOf(i*10000);
                    }
                    else
                    if(i<76){
                        data[i]= String.valueOf(Integer.parseInt(data[i-1].replace(" ",""))+20000);
                    }
                    else
                    if(i<96) {
                        data[i] = String.valueOf(Integer.parseInt(data[i - 1].replace(" ","")) + 50000);
                    }
                    else
                        data[i] = String.valueOf(Integer.parseInt(data[i - 1].replace(" ","")) + 100000);

                    int len = data[i].length(), counter;
                    String result = "";
                    if(len%3!=0)
                        result = data[i].substring(0,len%3)+" ";
                    counter = len%3;
                    while (counter < len)
                    {
                        result += data[i].substring(counter,counter+3)+" ";
                        counter+=3;
                    }
                    data[i] = result.substring(0,result.length()-1);
                }

                sp1 = (Spinner) view.findViewById(R.id.spinner_label_price_from);
                sp2 = (Spinner) view.findViewById(R.id.spinner_label_price_to);

                data[0]="От";
                fillSpinner(sp1, data, 0);
                data2 = (String[])data.clone();
                data2[0]="До";
                fillSpinner(sp2, data2, 0);
*/
                initSpinners("Price", v);
                sp1 = (Spinner) view.findViewById(R.id.spinner_label_price_from);
                sp2 = (Spinner) view.findViewById(R.id.spinner_label_price_to);

                sp1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(Spinner spinner, View view, int i, long l) {
                        Integer item = spinner.getSelectedItemPosition();
                        Integer item2 = sp2.getSelectedItemPosition();
                        if (item > item2 && item2 != 0) {
                            spinner.setSelection(sp2.getSelectedItemPosition());
                            Toast.makeText(getContext(), R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                sp2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(Spinner spinner, View view, int i, long l) {
                        Integer item = spinner.getSelectedItemPosition();
                        Integer item2 = sp1.getSelectedItemPosition();
                        if (item < item2 && item != 0) {
                            spinner.setSelection(sp1.getSelectedItemPosition());
                            Toast.makeText(getContext(), R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
/*
                ll = (LinearLayout)view.findViewById(R.id.search_ll_price_hidden);
                b = (Button) view.findViewById(R.id.search_ll_price_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_price);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                MainActivity.expand(ll);
*/
                break;
            case R.id.search_ll_year:
/*
                data = new String[36];
                for(int i = 0; i < data.length; i++)
                    data[i] = "" + String.valueOf(i + 1980);

                sp1 = (Spinner) view.findViewById(R.id.spinner_label_year_from);
                sp2 = (Spinner) view.findViewById(R.id.spinner_label_year_to);

                data[0]="От";
                fillSpinner(sp1, data, 0);
                data2 = (String[])data.clone();
                data2[0]="До";
                fillSpinner(sp2, data2, 0);

                //CardView cv = (CardView) view.findViewById(R.id.search_ll_year_cv);
                v.setClickable(false);
*/
                sp1 = (Spinner) view.findViewById(R.id.spinner_label_year_from);
                sp2 = (Spinner) view.findViewById(R.id.spinner_label_year_to);

                initSpinners("Year",v);
                sp1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(Spinner spinner, View view, int i, long l) {
                        Integer item = spinner.getSelectedItemPosition();
                        Integer item2 = sp2.getSelectedItemPosition();
                        if (item > item2 && item2 != 0) {
                            spinner.setSelection(sp2.getSelectedItemPosition());
                            Toast.makeText(getContext(), R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                sp2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(Spinner spinner, View view, int i, long l) {
                        Integer item = spinner.getSelectedItemPosition();
                        Integer item2 = sp1.getSelectedItemPosition();
                        if (item < item2 && item != 0) {
                            spinner.setSelection(sp1.getSelectedItemPosition());
                            Toast.makeText(getContext(), R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

/*
                ll = (LinearLayout)view.findViewById(R.id.search_ll_year_hidden);
                b = (Button) view.findViewById(R.id.search_ll_year_clear);
                //b.setVisibility(View.INVISIBLE);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_year);
                iv.setVisibility(View.INVISIBLE);

                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                MainActivity.expand(ll);
*/
                break;
            case R.id.search_ll_mileage:
/*
                v.setClickable(false);


                data = new String[]{"0","5","10","15","20","25","30","35","40","45","50","55", "60", "65", "70","75","80","85","90","95","100","110","120","130","140","150","160","170","180","190","200","210","220","230","240","250","260","270","280","290","300","310","320","330","340","350","360","370","380","390","400","410","420","430","440","450","460","470","480","490","500","600"};
                String[] data_view = new String[data.length];
                for(int n = 1; n < data.length-1 ; ++n ){
                    data_view[n]=data[n]+" 000";
                }
                data_view[0]="0";
                data_view[data.length-1]="500 000+";

                sp1 = (Spinner) view.findViewById(R.id.spinner_label_mileage_from);
                sp2 = (Spinner) view.findViewById(R.id.spinner_label_mileage_to);

                data_view[0]="От";
                fillSpinner(sp1, data_view, 0);
                data2 = (String[])data_view.clone();
                data2[0]="До";
                fillSpinner(sp2, data2, 0);
*/
                sp1 = (Spinner) view.findViewById(R.id.spinner_label_mileage_from);
                sp2 = (Spinner) view.findViewById(R.id.spinner_label_mileage_to);

                initSpinners("Milleage",v);
                sp1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(Spinner spinner, View view, int i, long l) {
                        Integer item = spinner.getSelectedItemPosition();
                        Integer item2 = sp2.getSelectedItemPosition();
                        if (item > item2  && item2 != 0) {
                            spinner.setSelection(sp2.getSelectedItemPosition());
                            Toast.makeText(getContext(), R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                sp2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(Spinner spinner, View view, int i, long l) {
                        Integer item = spinner.getSelectedItemPosition();
                        Integer item2 = sp1.getSelectedItemPosition();
                        if (item < item2 && item != 0) {
                            spinner.setSelection(sp1.getSelectedItemPosition());
                            Toast.makeText(getContext(), R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
/*
                ll = (LinearLayout)view.findViewById(R.id.search_ll_mileage_hidden);
                b = (Button) view.findViewById(R.id.search_ll_mileage_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_mileage);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                MainActivity.expand(ll);
*/
                break;
            case R.id.search_ll_engine_volume:
/*
                v.setClickable(false);
                data = new String[]{"0.0","0.6","0.7","0.8","0.9","1.0","1.1","1.2","1.3","1.4","1.5","1.6","1.7","1.8","1.9","2.0","2.1","2.2","2.3","2.4","2.5","2.6","2.7","2.8","2.9","3.0","3.1","3.2","3.3","3.4","3.5","4.0","4.5","5.0","5.5","6.0","6.0+"};

                sp1 = (Spinner) view.findViewById(R.id.spinner_label_engine_volume_from);
                sp2 = (Spinner) view.findViewById(R.id.spinner_label_engine_volume_to);

                data[0]="От";
                fillSpinner(sp1, data, 0);
                data2 = (String[])data.clone();
                data2[0]="До";
                fillSpinner(sp2, data2, 0);
*/
                sp1 = (Spinner) view.findViewById(R.id.spinner_label_engine_volume_from);
                sp2 = (Spinner) view.findViewById(R.id.spinner_label_engine_volume_to);
                initSpinners("Volume",v);

                sp1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(Spinner spinner, View view, int i, long l) {
                        Integer item = spinner.getSelectedItemPosition();
                        Integer item2 = sp2.getSelectedItemPosition();
                        if (item > item2  && item2 != 0) {
                            spinner.setSelection(sp2.getSelectedItemPosition());
                            Toast.makeText(getContext(), R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                sp2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(Spinner spinner, View view, int i, long l) {
                        Integer item = spinner.getSelectedItemPosition();
                        Integer item2 = sp1.getSelectedItemPosition();
                        if (item < item2 && item != 0) {
                            spinner.setSelection(sp1.getSelectedItemPosition());
                            Toast.makeText(getContext(), R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
/*
                ll = (LinearLayout)view.findViewById(R.id.search_ll_engine_volume_hidden);
                b = (Button) view.findViewById(R.id.search_ll_engine_volume_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_volume);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                MainActivity.expand(ll);
*/                break;
            case R.id.search_ll_trans:
                v.setClickable(false);

                ll = (LinearLayout)view.findViewById(R.id.search_ll_trans_hidden);
                b = (Button) view.findViewById(R.id.search_ll_trans_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_trans);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                MainActivity.expand(ll);
                break;
            case R.id.search_ll_body_type:
                v.setClickable(false);
                ll = (LinearLayout)view.findViewById(R.id.search_ll_body_type_hidden);
                b = (Button) view.findViewById(R.id.search_ll_body_type_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_body_type);
                iv.setVisibility(View.INVISIBLE);
                ll.setVisibility(View.VISIBLE);
                MainActivity.expand(ll);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            case R.id.search_ll_drive:
                v.setClickable(false);
                ll = (LinearLayout)view.findViewById(R.id.search_ll_drive_hidden);
                b = (Button) view.findViewById(R.id.search_ll_drive_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_drive);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                MainActivity.expand(ll);
                break;

        }
        return;
    }
    public void onClickClearSelection(View v){
        LinearLayout ll;
        Button b;
        com.rey.material.widget.CheckBox ch;
        TextView t;
        SharedPreferences sPref;
        SharedPreferences.Editor ed;
        Spinner sp;
        ImageView iv;
        switch (v.getId()){
            case R.id.search_ll_year_clear:

                ll = (LinearLayout)view.findViewById(R.id.search_ll_year);
                ll.setClickable(true);

                ll = (LinearLayout)view.findViewById(R.id.search_ll_year_hidden);
                b = (Button) view.findViewById(R.id.search_ll_year_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_year);
                iv.setVisibility(View.VISIBLE);



                sp = (Spinner) view.findViewById(R.id.spinner_label_year_from);
                sp.setSelection(0);
                sp = (Spinner) view.findViewById(R.id.spinner_label_year_to);
                sp.setSelection(0);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                MainActivity.collapse(ll);
                //ll.setVisibility(View.GONE);
                break;
            case R.id.search_ll_price_clear:

                ll = (LinearLayout)view.findViewById(R.id.search_ll_price);
                ll.setClickable(true);

                ll = (LinearLayout)view.findViewById(R.id.search_ll_price_hidden);
                b = (Button) view.findViewById(R.id.search_ll_price_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_price);
                iv.setVisibility(View.VISIBLE);

                sp = (Spinner) view.findViewById(R.id.spinner_label_price_from);
                sp.setSelection(0);
                sp = (Spinner) view.findViewById(R.id.spinner_label_price_to);
                sp.setSelection(0);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                MainActivity.collapse(ll);
                break;
            case R.id.search_ll_mileage_clear:

                ll = (LinearLayout)view.findViewById(R.id.search_ll_mileage);
                ll.setClickable(true);

                ll = (LinearLayout)view.findViewById(R.id.search_ll_mileage_hidden);
                b = (Button) view.findViewById(R.id.search_ll_mileage_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_mileage);
                iv.setVisibility(View.VISIBLE);

                sp = (Spinner) view.findViewById(R.id.spinner_label_mileage_from);
                sp.setSelection(0);
                sp = (Spinner) view.findViewById(R.id.spinner_label_mileage_to);
                sp.setSelection(0);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                MainActivity.collapse(ll);
                break;
            case R.id.search_ll_engine_volume_clear:
                ll = (LinearLayout)view.findViewById(R.id.search_ll_engine_volume);
                ll.setClickable(true);

                ll = (LinearLayout)view.findViewById(R.id.search_ll_engine_volume_hidden);
                b = (Button) view.findViewById(R.id.search_ll_engine_volume_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_volume);
                iv.setVisibility(View.VISIBLE);

                sp = (Spinner) view.findViewById(R.id.spinner_label_engine_volume_from);
                sp.setSelection(0);
                sp = (Spinner) view.findViewById(R.id.spinner_label_engine_volume_to);
                sp.setSelection(0);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                MainActivity.collapse(ll);
                break;
            case R.id.search_ll_engine_type_clear:
                ll = (LinearLayout)view.findViewById(R.id.search_ll_engine_type);
                ll.setClickable(true);

                ll = (LinearLayout)view.findViewById(R.id.search_ll_engine_type_hidden);
                b = (Button) view.findViewById(R.id.search_ll_engine_type_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_engine_type);
                iv.setVisibility(View.VISIBLE);

                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_engine_type_diesel);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_engine_type_electro);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_engine_type_gas);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_engine_type_gasoline);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_engine_type_hybrid);
                ch.setChecked(false);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                MainActivity.collapse(ll);
                break;
            case R.id.search_ll_trans_clear:
                ll = (LinearLayout)view.findViewById(R.id.search_ll_trans);
                ll.setClickable(true);

                ll = (LinearLayout)view.findViewById(R.id.search_ll_trans_hidden);
                b = (Button) view.findViewById(R.id.search_ll_trans_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_trans);
                iv.setVisibility(View.VISIBLE);

                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_trans_auto);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_trans_man);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_trans_robot);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_trans_var);
                ch.setChecked(false);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                MainActivity.collapse(ll);
                break;
            case R.id.search_ll_body_type_clear:
                ll = (LinearLayout)view.findViewById(R.id.search_ll_body_type);
                ll.setClickable(true);

                ll = (LinearLayout)view.findViewById(R.id.search_ll_body_type_hidden);
                b = (Button) view.findViewById(R.id.search_ll_body_type_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_body_type);
                iv.setVisibility(View.VISIBLE);

                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_cabrio);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_coupe);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_hatch);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_limus);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_minivan);
                ch.setChecked(false);


                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_offroad);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_picap);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_sed);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_univ);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_body_van);
                ch.setChecked(false);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                MainActivity.collapse(ll);
                break;
            case R.id.search_ll_drive_clear:
                ll = (LinearLayout)view.findViewById(R.id.search_ll_drive);
                ll.setClickable(true);

                ll = (LinearLayout)view.findViewById(R.id.search_ll_drive_hidden);
                b = (Button) view.findViewById(R.id.search_ll_drive_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_drive);
                iv.setVisibility(View.VISIBLE);

                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_drive_backward);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_drive_forward);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) view.findViewById(R.id.switches_cb_drive_full);
                ch.setChecked(false);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                MainActivity.collapse(ll);
                break;
            case R.id.search_ll_mark_clear:
                b = (Button) view.findViewById(R.id.search_ll_mark_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_mark);
                iv.setVisibility(View.VISIBLE);


                t = (TextView) view.findViewById(R.id.search_ll_mark_text);
                t.setText("Любая");

                sPref = getActivity().getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
                ed = sPref.edit();
                ed.putString("SelectedMark","Любая").commit();
                ed.putString("SelectedModel","Любая").commit();

                CardView cv = (CardView) view.findViewById(R.id.search_ll_model_cardview);
                cv.setVisibility(View.GONE);
                break;
            case R.id.search_ll_model_clear:
                b = (Button) view.findViewById(R.id.search_ll_model_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_model);
                iv.setVisibility(View.VISIBLE);

                t = (TextView)view.findViewById(R.id.search_ll_model_text);
                t.setText("Любая");

                sPref = getActivity().getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
                ed = sPref.edit();
                ed.putString("SelectedModel","Любая").commit();
                break;
        }
        return;
    }
    public void onClickMarkorModel(View v){
        Intent intent;
        final DbHelper dbHelper = new DbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (v.getId()){
            case R.id.search_ll_mark_cardview :
                //Button b = (Button) view.findViewById(R.id.search_ll_mark_clear);
                //b.setVisibility(View.VISIBLE); //в новое активити перенести это

                Cursor cursor = db.query("marksTable", null, null, null, null, null, null);
                String strToParse = "Любая@@@";

                if (cursor.moveToFirst()) {
                    int MarkColIndex = cursor.getColumnIndex("markauser");
                    do {

                        strToParse += cursor.getString(MarkColIndex) + "@@@";
                    } while (cursor.moveToNext());
                }
                String[] marks_arr = strToParse.split("@@@");
                intent = new Intent(getActivity(), MarkFilter.class);
                intent.putExtra("Marks",marks_arr);
                startActivity(intent);
                break;
            case R.id.search_ll_model_cardview :
                //Button b = (Button) view.view.findViewById(R.id.search_ll_mark_clear);
                //b.setVisibility(View.VISIBLE); //в новое активити перенести это
                SharedPreferences sPref2 = getActivity().getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
//тут
                String pos = sPref2.getString("SelectedMark", "Любая");
                if(pos.equals("Любая")){
                    Toast t = Toast.makeText(getActivity().getApplicationContext(),"Для начала выберите марку",Toast.LENGTH_SHORT);
                    t.show();
                    break;
                }

                Cursor cursor3 = db.query("marksTable", null, "markauser=?", new String[]{pos}, null, null, null);
                cursor3.moveToFirst();
                Integer markId = cursor3.getColumnIndex("id");
                String markIdValue = cursor3.getString(markId);
                Cursor cursor2 = db.query("modelsTable", null, "marka_id=?", new String[]{markIdValue}, null, null, null);
                String strToParse2 = "Любая@@@";

                if (cursor2.moveToFirst()) {
                    int ModelColIndex = cursor2.getColumnIndex("modeluser");
                    do {

                        strToParse2 += cursor2.getString(ModelColIndex) + "@@@";
                    } while (cursor2.moveToNext());
                }
                String[] models_arr = strToParse2.split("@@@");
                intent = new Intent(getActivity(), MarkFilter.class);
                intent.putExtra("Models",models_arr);
                startActivity(intent);
                break;
        }
        db.close();
    }

    public void fillSpinner(Spinner sp,String[] data, int pos){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.row_spn, data);
        adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
        sp.setAdapter(adapter);
        sp.setSelection(pos);
    }

    public void initSpinners(String name, View v){
        LinearLayout ll;
        Button b;
        ImageView iv;

        final Spinner sp1;
        final Spinner sp2;

        final String[] data;
        final String[] data2;
        switch (name){
            case "Year" :
                data = new String[36];
                for(int i = 0; i < data.length; i++)
                    data[i] = "" + String.valueOf(i + 1980);

                sp1 = (Spinner) view.findViewById(R.id.spinner_label_year_from);
                sp2 = (Spinner) view.findViewById(R.id.spinner_label_year_to);

                data[0]="От";
                fillSpinner(sp1, data, 0);
                data2 = (String[])data.clone();
                data2[0]="До";
                fillSpinner(sp2, data2, 0);

                //CardView cv = (CardView) view.findViewById(R.id.search_ll_year_cv);
                v.setClickable(false);

                /////////

                ll = (LinearLayout)view.findViewById(R.id.search_ll_year_hidden);
                b = (Button) view.findViewById(R.id.search_ll_year_clear);
                //b.setVisibility(View.INVISIBLE);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_year);
                iv.setVisibility(View.INVISIBLE);

                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                MainActivity.expand(ll);
                break;
            case "Price":

                v.setClickable(false);

                data = new String[176];

                for(int i=0; i<data.length; ++i){
                    if(i<51){
                        data[i]= String.valueOf(i*10000);
                    }
                    else
                    if(i<76){
                        data[i]= String.valueOf(Integer.parseInt(data[i-1].replace(" ",""))+20000);
                    }
                    else
                    if(i<96) {
                        data[i] = String.valueOf(Integer.parseInt(data[i - 1].replace(" ","")) + 50000);
                    }
                    else
                        data[i] = String.valueOf(Integer.parseInt(data[i - 1].replace(" ","")) + 100000);

                    int len = data[i].length(), counter;
                    String result = "";
                    if(len%3!=0)
                        result = data[i].substring(0,len%3)+" ";
                    counter = len%3;
                    while (counter < len)
                    {
                        result += data[i].substring(counter,counter+3)+" ";
                        counter+=3;
                    }
                    data[i] = result.substring(0,result.length()-1);
                }

                sp1 = (Spinner) view.findViewById(R.id.spinner_label_price_from);
                sp2 = (Spinner) view.findViewById(R.id.spinner_label_price_to);

                data[0]="От";
                fillSpinner(sp1, data, 0);
                data2 = (String[])data.clone();
                data2[0]="До";
                fillSpinner(sp2, data2, 0);
                /////////////


                ll = (LinearLayout)view.findViewById(R.id.search_ll_price_hidden);
                b = (Button) view.findViewById(R.id.search_ll_price_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_price);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                MainActivity.expand(ll);
                break;
            case "Milleage" :
                v.setClickable(false);

                data = new String[]{"0","5","10","15","20","25","30","35","40","45","50","55", "60", "65", "70","75","80","85","90","95","100","110","120","130","140","150","160","170","180","190","200","210","220","230","240","250","260","270","280","290","300","310","320","330","340","350","360","370","380","390","400","410","420","430","440","450","460","470","480","490","500","600"};
                String[] data_view = new String[data.length];
                for(int n = 1; n < data.length-1 ; ++n ){
                    data_view[n]=data[n]+" 000";
                }
                data_view[0]="0";
                data_view[data.length-1]="500 000+";

                sp1 = (Spinner) view.findViewById(R.id.spinner_label_mileage_from);
                sp2 = (Spinner) view.findViewById(R.id.spinner_label_mileage_to);

                data_view[0]="От";
                fillSpinner(sp1, data_view, 0);
                data2 = (String[])data_view.clone();
                data2[0]="До";
                fillSpinner(sp2, data2, 0);

                ////////

                ll = (LinearLayout)view.findViewById(R.id.search_ll_mileage_hidden);
                b = (Button) view.findViewById(R.id.search_ll_mileage_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_mileage);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                MainActivity.expand(ll);
                break;
            case "Volume" :
                v.setClickable(false);
                data = new String[]{"0.0","0.6","0.7","0.8","0.9","1.0","1.1","1.2","1.3","1.4","1.5","1.6","1.7","1.8","1.9","2.0","2.1","2.2","2.3","2.4","2.5","2.6","2.7","2.8","2.9","3.0","3.1","3.2","3.3","3.4","3.5","4.0","4.5","5.0","5.5","6.0","6.0+"};

                sp1 = (Spinner) view.findViewById(R.id.spinner_label_engine_volume_from);
                sp2 = (Spinner) view.findViewById(R.id.spinner_label_engine_volume_to);

                data[0]="От";
                fillSpinner(sp1, data, 0);
                data2 = (String[])data.clone();
                data2[0]="До";
                fillSpinner(sp2, data2, 0);

                ll = (LinearLayout)view.findViewById(R.id.search_ll_engine_volume_hidden);
                b = (Button) view.findViewById(R.id.search_ll_engine_volume_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) view.findViewById(R.id.arrow_volume);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                MainActivity.expand(ll);

                break;
        }
    }
}
