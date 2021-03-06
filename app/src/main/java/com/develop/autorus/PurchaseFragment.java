package com.develop.autorus;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.develop.autorus.inappbilling.util.IabHelper;
import com.develop.autorus.inappbilling.util.IabResult;
import com.develop.autorus.inappbilling.util.Inventory;
import com.develop.autorus.inappbilling.util.Purchase;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;

import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by Alex on 12.10.2015.
 */
public class PurchaseFragment extends android.support.v4.app.Fragment {
    private static final String TAG =
            "Pasha i Nikita";//
    public static IabHelper mHelper;

    // Изменить на реальные.
    static final String ITEM_SKU1 = "delete_adds";//"android.test.purchased";
    static final String ITEM_SKU2 = "no_limit_favorites";//"android.test.purchased";
    static final String ITEM_SKU3 = "not_limit_monitors";//"android.test.purchased";
    static final String ITEM_SKU4 = "god_mode";//"android.test.purchased";


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
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View savedView = inflater.inflate(R.layout.fragment_purchase, container, false);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean bool = settings.getBoolean("TAG_DISABLED_ADS", false);

        if (bool) {
            //Toast.makeText(getActivity(), bool.toString() + "   111111" + " \n", Toast.LENGTH_LONG);
            Log.d("In App Purchase", "True. No reklama, we buy this option");
        } else {
            //Toast.makeText(getActivity(), bool.toString() + "   222222" + " \n", Toast.LENGTH_LONG);
            Log.d("In App Purchase", "False. With reklama, we don't buy this option");
        }

        //Danger! Auchtung! Никита, Паша!!!
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAskc/nL1SvaOSFbHVSRQrekyfW0Qqnk1I0ld+elQyTczDMbSO57DBynG7tYcQeKFN2/oQC+rt1LvIeHrEVELl3cnTrfUQLHqaqX73C8ZuI2ygcJ/joLHtW4qjHvOOfMuDOYNwmH6APcr11cRPfuMwZPOSrpKjq193F4xpCmhsGQA7ZoAXlJNeotnglDa2uzvAOMv+6Lry/8jZRxCvmJ7crH4cGP3FqSfirq/Qy61DkhEoWy4DiUIlDRxom6IqF9cKvDn4EeVJBQAAbWCflYlfKv1sIlmr+kfLyb17Adbm6+8FFS6t0Ko9MhDo0B1kvBy19FNOtFlLPp5lLmHx0Vs5YwIDAQAB";//Здесь реальный наш ключ. Изменить!!! Не уверен, что нужно заливать на
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



        android.support.v7.widget.CardView cardView1 = (android.support.v7.widget.CardView) savedView.findViewById(R.id.buyFirstItem);
        cardView1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
                            @Override
                            public void onPositiveActionClicked(DialogFragment fragment) {
                                super.onPositiveActionClicked(fragment);
                                if (!MainActivity.blnBind) return;

                                if (MainActivity.mService == null) return;

                                try {
                                    mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU1, 10001,
                                            mPurchaseFinishedListener, "mypurchasetoken");
                                } catch (Exception ex) {
                                    mHelper.flagEndAsync();
                                    mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU1, 10001,
                                            mPurchaseFinishedListener, "mypurchasetoken");
                                }
                            }

                            @Override
                            public void onNegativeActionClicked(DialogFragment fragment) {
                                super.onNegativeActionClicked(fragment);
                            }

                        };
                        builder.message("После совершения данной покупки вся реклама в приложении будет навсегда отключена для Вашего аккаунта Google.").title("Отключение рекламы").positiveAction("Купить").negativeAction("Отмена");
                        DialogFragment fragment = DialogFragment.newInstance(builder);
                        fragment.show(getActivity().getSupportFragmentManager(), null);
                    }
                }
        );

        android.support.v7.widget.CardView cardView2 = (android.support.v7.widget.CardView) savedView.findViewById(R.id.buySecondItem);
        cardView2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
                            @Override
                            public void onPositiveActionClicked(DialogFragment fragment) {
                                super.onPositiveActionClicked(fragment);
                                try {
                                    if (!MainActivity.blnBind) return;

                                    if (MainActivity.mService == null) return;

                                    mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU2, 10001,
                                            mPurchaseFinishedListener, "mypurchasetoken");
                                } catch (Exception ex) {
                                    mHelper.flagEndAsync();
                                    mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU2, 10001,
                                            mPurchaseFinishedListener, "mypurchasetoken");
                                }
                            }

                            @Override
                            public void onNegativeActionClicked(DialogFragment fragment) {
                                super.onNegativeActionClicked(fragment);
                            }

                        };
                        builder.message("Покупка данной опции позволит Вам добовлять неограниченное количество авто в избранное.").title("Опция 'Мое избранное'").positiveAction("Купить").negativeAction("Отмена");
                        DialogFragment fragment = DialogFragment.newInstance(builder);
                        fragment.show(getActivity().getSupportFragmentManager(), null);
                    }
                }
        );

        android.support.v7.widget.CardView cardView3 = (android.support.v7.widget.CardView) savedView.findViewById(R.id.buyThirdItem);
        cardView3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
                            @Override
                            public void onPositiveActionClicked(DialogFragment fragment) {
                                super.onPositiveActionClicked(fragment);
                                try {
                                    if (!MainActivity.blnBind) return;
                                    Log.d("checkPurchaseTest2", "3 2222222");
                                    if (MainActivity.mService == null) return;
                                    Log.d("checkPurchaseTest2", "3 33333333333");
                                    mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU3, 10001,
                                            mPurchaseFinishedListener, "mypurchasetoken");
                                } catch (Exception ex) {
                                    mHelper.flagEndAsync();
                                    mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU3, 10001,
                                            mPurchaseFinishedListener, "mypurchasetoken");
                                }
                            }

                            @Override
                            public void onNegativeActionClicked(DialogFragment fragment) {
                                super.onNegativeActionClicked(fragment);
                            }

                        };
                        builder.message("Покупка данной опции позволит Вам создавать неограниченное количество мониторов.").title("Опция 'Мои мониторы'").positiveAction("Купить").negativeAction("Отмена");
                        DialogFragment fragment = DialogFragment.newInstance(builder);
                        fragment.show(getActivity().getSupportFragmentManager(), null);
                    }
                }
        );

        android.support.v7.widget.CardView cardView4 = (android.support.v7.widget.CardView) savedView.findViewById(R.id.buyFourthItem);
        cardView4.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
                            @Override
                            public void onPositiveActionClicked(DialogFragment fragment) {
                                super.onPositiveActionClicked(fragment);
                                Log.d("checkPurchaseTest2", "4 111111");
                                if (!MainActivity.blnBind) return;
                                Log.d("checkPurchaseTest2", "4 222222222");
                                if (MainActivity.mService == null) return;
                                Log.d("checkPurchaseTest2", "4 333333333");
                                try {
                                    mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU4, 10001,
                                            mPurchaseFinishedListener, "mypurchasetoken");
                                } catch (Exception ex) {
                                    mHelper.flagEndAsync();
                                    mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU4, 10001,
                                            mPurchaseFinishedListener, "mypurchasetoken");
                                }
                            }

                            @Override
                            public void onNegativeActionClicked(DialogFragment fragment) {
                                super.onNegativeActionClicked(fragment);
                            }

                        };
                        builder.message("При покупке данного пакета Вы получаете возможность неограниченного добавления авто в избранное и возможность создания неограниченного числа мониторов. " +
                                "Также навсегда будет отключена реклама.").title("Пакет 'Все включено'").positiveAction("Купить").negativeAction("Отмена");
                        DialogFragment fragment = DialogFragment.newInstance(builder);
                        fragment.show(getActivity().getSupportFragmentManager(), null);
                    }
                }
        );


        /*com.rey.material.widget.Button button = (com.rey.material.widget.Button) savedView.findViewById(R.id.deletePurchase);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!MainActivity.blnBind) return;

                        if (MainActivity.mService == null) return;

                        consumeItem();
                    }
                }
        );
*/

        //fab.setIcon(getResources().getDrawable(R.drawable.ic_mode_edit_white_24dp), false);

        updateView(savedView, settings);

        Thread checkPurchase = new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void run()
            {
                /*try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                String tag = "checkPurchaseTest";
                Log.i(tag, "111111111111");
                if (!MainActivity.blnBind) return;
                Log.i(tag, "2222222222");
                if (MainActivity.mService == null) return;
                Log.i(tag, "3333333333333");

                Bundle ownedItems;
                try {
                    ownedItems = MainActivity.mService.getPurchases(3, savedView.getContext().getPackageName(), "inapp", null);

                    //Toast.makeText(getApplicationContext(), "getPurchases() - success return Bundle", Toast.LENGTH_SHORT).show();

                } catch (RemoteException e) {
                    e.printStackTrace();

                    //Toast.makeText(getApplicationContext(), "getPurchases - fail!", Toast.LENGTH_SHORT).show();

                    return;
                }

                int response = ownedItems.getInt("RESPONSE_CODE");
                //Toast.makeText(getApplicationContext(), "getPurchases() - \"RESPONSE_CODE\" return " + String.valueOf(response), Toast.LENGTH_SHORT).show();
                Log.i(tag, "getPurchases() - \"RESPONSE_CODE\" return " + String.valueOf(response));

                if (response != 0) return;

                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
                String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");

                Log.i(tag, "getPurchases() - \"INAPP_PURCHASE_ITEM_LIST\" return " + ownedSkus.toString());
                Log.i(tag, "getPurchases() - \"INAPP_PURCHASE_DATA_LIST\" return " + purchaseDataList.toString());
                Log.i(tag, "getPurchases() - \"INAPP_DATA_SIGNATURE\" return " + (signatureList != null ? signatureList.toString() : "null"));
                Log.i(tag, "getPurchases() - \"INAPP_CONTINUATION_TOKEN\" return " + (continuationToken != null ? continuationToken : "null"));


                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    //String purchaseData = purchaseDataList.get(i);
                    //String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);
                    Log.d("checkPurchaseTest", i + "  " + sku.toString());
                    if(sku.equals(PurchaseFragment.ITEM_SKU1))
                    {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("TAG_DISABLED_ADS", true);
                        editor.commit();
                        Log.d("checkPurchaseTest", i + "  " + sku.toString() + "   fuck the poli");

                    }
                    if(sku.equals(PurchaseFragment.ITEM_SKU2))
                    {
                        // Ставим нужное количество мониторов.
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("TAG_FAVORITES", true);
                        editor.commit();

                        Log.d("checkPurchaseTest", i + "  " + sku.toString() + "   fuck the poli");
                    }

                    if(sku.equals(PurchaseFragment.ITEM_SKU3))
                    {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("TAG_MONITOR", true);
                        editor.commit();

                        Log.d("checkPurchaseTest", i + "  " + sku.toString() + "   fuck the poli");
                    }

                    if(sku.equals(PurchaseFragment.ITEM_SKU4))
                    {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("TAG_BUY_ALL", true);
                        editor.commit();

                        Log.d("checkPurchaseTest", i + "  " + sku.toString() + "   fuck the poli");
                    }
                }
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                Boolean bool = settings.getBoolean("TAG_DISABLED_ADS", false);
                android.support.v7.widget.CardView cardView1 = (android.support.v7.widget.CardView) savedView.findViewById(R.id.buyFirstItem);
                android.support.v7.widget.CardView cardView2 = (android.support.v7.widget.CardView) savedView.findViewById(R.id.buySecondItem);
                android.support.v7.widget.CardView cardView3 = (android.support.v7.widget.CardView) savedView.findViewById(R.id.buyThirdItem);
                android.support.v7.widget.CardView cardView4 = (android.support.v7.widget.CardView) savedView.findViewById(R.id.buyFourthItem);


            }

        });

        checkPurchase.run();

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

        ArrayList<String> additionalSkuList = new ArrayList<String>();
        additionalSkuList.add(ITEM_SKU1);
        additionalSkuList.add(ITEM_SKU2);
        additionalSkuList.add(ITEM_SKU3);
        additionalSkuList.add(ITEM_SKU4);
        mHelper.queryInventoryAsync(true, additionalSkuList, mReceivedInventoryListener);


        //mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    // Как выбрать именно этот вариант.
    //Это точно неправильно inventory.hasPurchase, скорее всего отвечает не за текущее состояние. А вообще за покупку.
    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
            } else {
                if (inventory.hasPurchase(ITEM_SKU1)) {
                    mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU1),
                            mConsumeFinishedListener);
                    return;
                }
                if (inventory.hasPurchase(ITEM_SKU2)){
                    mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU2),
                            mConsumeFinishedListener);
                    return;
                }
                if (inventory.hasPurchase(ITEM_SKU3)) {
                    mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU3),
                            mConsumeFinishedListener);
                    return;
                }
                if (inventory.hasPurchase(ITEM_SKU4)) {
                    mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU4),
                            mConsumeFinishedListener);
                    return;
                }

            }
        }


    };



        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
                = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result,
                                              Purchase purchase) {
                if (result.isFailure()) {
                    // Handle error
                    return;
                } else {
                    if (purchase.getSku().equals(ITEM_SKU1)) {// где ITEM_SKU = на отключение рекламы.
                        Log.d("3333333333", "11111111111111111111w");

                        // Если наш ITEM_SKU совпадает с соответсвующем для рекламы ITEM_SKU
                        // В sharedPreference сохраняем, что реклама отключена. Нужно, чтобы при обновления приложения покупка оставалась.
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("TAG_DISABLED_ADS", true);
                        editor.commit();

                    }
                /*  Изменить и сделать нужное количество. */
                    else if (purchase.getSku().equals(ITEM_SKU2)) {
                        Log.d("3333333333", "222222222222222222222w");

                        // Ставим нужное количество мониторов.
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("TAG_FAVORITES", true);
                        editor.commit();
                    }
                /*  Изменить и сделать нужное количество. */
                    else if (purchase.getSku().equals(ITEM_SKU3)) {
                        Log.d("3333333333", "33333333333333333w");

                        // Ставим нужное количество мониторов.
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("TAG_MONITOR", true);
                        editor.commit();
                    }
                /*  Изменить и сделать нужное количество. */
                    else if (purchase.getSku().equals(ITEM_SKU4)) {
                        Log.d("3333333333", "444444444444444444w");
                        // Ставим нужное количество мониторов.
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("TAG_BUY_ALL", true);
                        editor.commit();
                    }
                    Toast.makeText(getContext(),"Поздравляем с покупкой!",Toast.LENGTH_LONG).show();
                    updateView(getView(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
                }
            }
        };

    private void updateView(View savedView, SharedPreferences settings)
    {
        if(settings.getBoolean("TAG_DISABLED_ADS", false))
        {
            savedView.findViewById(R.id.purchasedAdds).setVisibility(View.VISIBLE);
            savedView.findViewById(R.id.buyFirstItem).setClickable(false);
        }
        if(settings.getBoolean("TAG_FAVORITES", false))
        {
            savedView.findViewById(R.id.purchasedFavorites).setVisibility(View.VISIBLE);
            savedView.findViewById(R.id.buySecondItem).setClickable(false);
        }
        if(settings.getBoolean("TAG_MONITOR", false))
        {
            savedView.findViewById(R.id.purchasedMonitors).setVisibility(View.VISIBLE);
            savedView.findViewById(R.id.buyThirdItem).setClickable(false);
        }
        if(settings.getBoolean("TAG_BUY_ALL", false))
        {
            savedView.findViewById(R.id.purchasedAdds).setVisibility(View.VISIBLE);
            savedView.findViewById(R.id.buyFirstItem).setClickable(false);

            savedView.findViewById(R.id.purchasedFavorites).setVisibility(View.VISIBLE);
            savedView.findViewById(R.id.buySecondItem).setClickable(false);

            savedView.findViewById(R.id.purchasedMonitors).setVisibility(View.VISIBLE);
            savedView.findViewById(R.id.buyThirdItem).setClickable(false);

            savedView.findViewById(R.id.purchasedAll).setVisibility(View.VISIBLE);
            savedView.findViewById(R.id.buyFourthItem).setClickable(false);
        }
    }

}

//  Код для проверки, что уже куплено.
/*

        Bundle ownedItems;
                if (!MainActivity.blnBind) return;

                if (MainActivity.mService == null) return;

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
                        if(sku.equals("android.test.purchased"))
                            Log.d("InAPP123", "899999999999999999999");

                    }
                }

                Log.i("222", "getPurchases() - \"INAPP_PURCHASE_ITEM_LIST\" return " + ownedSkus.toString());
                Log.i("222", "getPurchases() - \"INAPP_PURCHASE_DATA_LIST\" return " + purchaseDataList.toString());
                Log.i("222", "getPurchases() - \"INAPP_DATA_SIGNATURE\" return " + (signatureList != null ? signatureList.toString() : "null"));
                Log.i("222", "getPurchases() - \"INAPP_CONTINUATION_TOKEN\" return " + (continuationToken != null ? continuationToken : "null"));
try {
        mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU1, 10001,
        mPurchaseFinishedListener, "mypurchasetoken");
        } catch (Exception ex) {
        mHelper.flagEndAsync();
        mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU1, 10001,
        mPurchaseFinishedListener, "mypurchasetoken");
        }

        */
