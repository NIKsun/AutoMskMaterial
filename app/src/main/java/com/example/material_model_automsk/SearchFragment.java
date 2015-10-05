package com.example.material_model_automsk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.Button;

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
    }

    @Override
    public void onResume(){
        super.onPause();
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

            //getContext().getResources().getDisplayMetrics().densityDpi = 2;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(15, 15, 15, 15);
            ll.setLayoutParams(lp);
            ll.setUseCompatPadding(true);
            ll.setCardElevation(4);
        }
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        //TextView textView = (TextView) view;
        //textView.setText("Fragment #" + mPage);
        return view;
    }


}
