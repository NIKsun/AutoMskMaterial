package com.example.material_model_automsk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.Switch;

/**
 * Created by Никита on 24.09.2015.
 */
public class SearchFragment extends Fragment {

    View view;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        ImageView iv;

        TextView t = (TextView) getActivity().findViewById(R.id.search_ll_mark_text);
        t.setText(mark);

        TextView t2 = (TextView) getActivity().findViewById(R.id.search_ll_model_text);
        t2.setText(model);

        CardView ll = (CardView) getActivity().findViewById(R.id.search_ll_model_cardview);
        if(!mark.equals("Любая"))
        {
            Button b = (Button) getActivity().findViewById(R.id.search_ll_mark_clear);
            b.setVisibility(View.VISIBLE);
            iv = (ImageView) getActivity().findViewById(R.id.arrow_mark);
            iv.setVisibility(View.INVISIBLE);
            ll.setVisibility(View.VISIBLE);
        }
        else
            ll.setVisibility(View.GONE);
        if(!model.equals("Любая"))
        {
            Button b2 = (Button) getActivity().findViewById(R.id.search_ll_model_clear);
            b2.setVisibility(View.VISIBLE);
            iv = (ImageView) getActivity().findViewById(R.id.arrow_model);
            iv.setVisibility(View.INVISIBLE);
        }
        else
        {
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

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_search);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_simple_grow);
        fab.startAnimation(anim);
        fab.setVisibility(View.VISIBLE);
        fab.setIcon(getResources().getDrawable(R.drawable.ic_search_white_48dp), false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filter filter = new Filter();
                filter.fillFilter(view);
                filter.getHref(getContext());
                SharedPreferences sPref = getActivity().getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("hrefAutoRu", filter.hrefAuto);
                ed.putString("hrefAvitoRu", filter.hrefAvito);
                ed.putString("hrefDromRu", filter.hrefDrom);
                ed.commit();
                Intent intent = new Intent(v.getContext(), ListOfCarsActivity.class);
                intent.putExtra("monitorID", -1);
                v.getContext().startActivity(intent);
            }
        });

        android.widget.Button addMonitorButton = ((MainActivity)getActivity()).getAddMonitorButton();
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
}
