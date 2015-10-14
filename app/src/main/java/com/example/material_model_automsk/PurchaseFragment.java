package com.example.material_model_automsk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
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

import java.util.ArrayList;


/**
 * Created by Alex on 12.10.2015.
 */
public class PurchaseFragment extends android.support.v4.app.Fragment {
    View savedView;
    FloatingActionButton fab;

    private static final String TAG =
            "Pasha i Nikita";//
    public static IabHelper mHelper;

    static final String ITEM_SKU = "android.test.purchased";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.init(getActivity(), 2, 0, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1")) {
            getActivity().setTheme(R.style.AppTheme);
        }
        else if (themeName.equals("2")) {
            getActivity().setTheme(R.style.AppTheme2);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedView = inflater.inflate(R.layout.fragment_purchase, container, false);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean bool = settings.getBoolean("TAG_DISABLED_ADS", false);

        if (bool) {
            Toast.makeText(getActivity(), bool.toString() + "   111111" + " \n", Toast.LENGTH_LONG);
            Log.d("In App Purchase", "1111111111111");
        } else {
            Toast.makeText(getActivity(), bool.toString() + "   222222" + " \n", Toast.LENGTH_LONG);
            Log.d("In App Purchase", "2222222222222");
        }

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





        final FloatingActionButton fab = (FloatingActionButton) savedView.findViewById(R.id.purchase_something);
        final Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_simple_grow);
        fab.startAnimation(anim);
        fab.setVisibility(View.VISIBLE);
        fab.setIcon(getResources().getDrawable(R.drawable.ic_mode_edit_white_24dp), false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1")) {
            fab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else
            fab.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle ownedItems;
                try {
                    ownedItems = MainActivity.mService.getPurchases(3, getActivity().getPackageName(), "inapp", null);

                    Toast.makeText(getContext(), "getPurchases() - success return Bundle", Toast.LENGTH_SHORT).show();
                    Log.i("222", "getPurchases() - success return Bundle");
                } catch (RemoteException e) {
                    e.printStackTrace();

                    Toast.makeText(getContext(), "getPurchases - fail!", Toast.LENGTH_SHORT).show();
                    Log.w("222", "getPurchases() - fail!");
                    return;
                }

                int response = ownedItems.getInt("RESPONSE_CODE");
                Toast.makeText(getContext(), "getPurchases() - \"RESPONSE_CODE\" return " + String.valueOf(response), Toast.LENGTH_SHORT).show();
                Log.i("222", "getPurchases() - \"RESPONSE_CODE\" return " + String.valueOf(response));

                if (response != 0) return;

                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
                String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");
                if(purchaseDataList.size() > 0)
                {
                    //Item(s) owned

                    for(int i=0; i<purchaseDataList.size(); ++i)
                    {

                        String purchaseData = purchaseDataList.get(i);
                        //String signature = signatureList.get(i); //Note signatures do not appear to work with android.test.purchased (silly google)
                        String sku = ownedSkus.get(i);

                        Log.i("222", purchaseData+"\n 1111     "+"\n 1111     "+sku);
                    }
                }

                Log.i("222", "getPurchases() - \"INAPP_PURCHASE_ITEM_LIST\" return " + ownedSkus.toString());
                Log.i("222", "getPurchases() - \"INAPP_PURCHASE_DATA_LIST\" return " + purchaseDataList.toString());
                Log.i("222", "getPurchases() - \"INAPP_DATA_SIGNATURE\" return " + (signatureList != null ? signatureList.toString() : "null"));
                Log.i("222", "getPurchases() - \"INAPP_CONTINUATION_TOKEN\" return " + (continuationToken != null ? continuationToken : "null"));

                mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU, 10001,
                        mPurchaseFinishedListener, "mypurchasetoken2");
            }
        });

        return savedView;
    }


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


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
                return;
            }
            else {
                if (purchase.getSku().equals(ITEM_SKU)) {// где ITEM_SKU = на отключение рекламы.
                    consumeItem();
                    Log.d( "3333333333", "8888888888888888888888888888");


                    // Если наш ITEM_SKU совпадает с соответсвующем для рекламы ITEM_SKU
                    // В sharedPreference сохраняем, что реклама отключена. Нужно, чтобы при обновления приложения покупка оставалась.
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("TAG_DISABLED_ADS", true);
                    editor.commit();

                }
                /*  Изменить и сделать нужное количество.
                if(purchase.getSku().equals(ITEM_SKU2))
                {
                    consumeItem();
                    // Ставим нужное количество мониторов.
                    //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    //SharedPreferences.Editor editor = settings.edit();
                    //editor.putBoolean("TAG_DISABLED_ADS", true);
                    //editor.commit();
                }
               */
            }

        }
    };

}