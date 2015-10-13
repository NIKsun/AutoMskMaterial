package com.example.material_model_automsk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.material_model_automsk.inappbilling.util.IabHelper;
import com.example.material_model_automsk.inappbilling.util.IabResult;
import com.example.material_model_automsk.inappbilling.util.Inventory;
import com.example.material_model_automsk.inappbilling.util.Purchase;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.RelativeLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


/**
 * Created by Alex on 12.10.2015.
 */
public class PurchaseFragment extends android.support.v4.app.Fragment {
    View savedView;
    FloatingActionButton fab;

    private static final String TAG =
            "Pasha i Nikita";
    IabHelper mHelper;
    static final String ITEM_SKU = "android.test.purchased";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ThemeManager.init(getActivity(), 2, 0, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1")) {
            getActivity().setTheme(R.style.AppTheme);
        } else if (themeName.equals("2")) {
            getActivity().setTheme(R.style.AppTheme2);
        }

        //Чем отличаются две функции? Потом уберу этот комментарий.

        //Danger! Auchtung! Никита, Паша!!!
        String base64EncodedPublicKey =
                "<your license key here>";//Здесь реальный наш ключ. Изменить!!! Не уверен, что нужно заливать на
                                          // github с реальным ключом. Иначе он будет в открытом виде в инете висеть!!!

        mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " +
                            result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });
        //Возможно, этот код нужно в нижнюю функцию поместить.
    }
    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        //clickButton.setEnabled(true);
                    } else {
                        // handle error
                    }
                }
            };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
                final FloatingActionButton fab = (FloatingActionButton) savedView.findViewById(R.id.purchase_something);
                fab.setBackgroundColor(getResources().getColor(R.color.colorControlNormal));
                //buyButton.setEnabled(false);
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedView = inflater.inflate(R.layout.fragment_purchase, container, false);

        final FloatingActionButton fab = (FloatingActionButton) savedView.findViewById(R.id.purchase_something);
        final Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_simple_grow);
        fab.startAnimation(anim);
        fab.setVisibility(View.VISIBLE);
        fab.setIcon(getResources().getDrawable(R.drawable.ic_mode_edit_white_24dp), false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1")) {
            fab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else
            fab.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU, 10001,
                        mPurchaseFinishedListener, "mypurchasetoken2");
            }
        });

        return savedView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}