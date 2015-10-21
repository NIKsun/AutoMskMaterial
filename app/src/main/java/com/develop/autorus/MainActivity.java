package com.develop.autorus;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.Button;
import com.rey.material.widget.SnackBar;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SnackBar mSnackBar;
    private Button addMonitorButton;
    private Boolean isFirstLaunch = true, itemSelectFromTabLayout = false;
    private Toolbar mToolbar;
    private Fragment secondFragment;
    private SearchAndMonitorsFragment mainFragment;
    private Toast backToast = null;
    final String SAVED_TEXT_WITH_VERSION = "checkVersion";
    final String DO_NOT_REMIND = "DontRemind";
    private AlarmManager am;
    Tracker mTracker;

    public static boolean blnBind;

    /*ForEasyDelete
    private static final String TAG =
            "Pasha i Nikita";//
    IabHelper mHelper;

    static final String ITEM_SKU = "android.test.purchased";
*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        super.onCreate(savedInstanceState);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Main activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if(pref.getBoolean("notificationIsActive",true)) {
            Intent checkIntent = new Intent(getApplicationContext(), MonitoringWork.class);
            Boolean alrarmIsActive = false;
            if (PendingIntent.getService(getApplicationContext(), 0, checkIntent, PendingIntent.FLAG_NO_CREATE) != null)
                alrarmIsActive = true;
            am = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (!alrarmIsActive) {
                Intent serviceIntent = new Intent(getApplicationContext(), MonitoringWork.class);
                PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, 0);

                int period = pref.getInt("numberOfActiveMonitors", 0) * 180000;

                if(period != 0)
                    am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + period, period, pIntent);
            }
        }

            /*ForEasyDelete

        //Danger! Auchtung! Никита, Паша!!!
        String base64EncodedPublicKey =
                "<your license key here>";//Здесь реальный наш ключ. Изменить!!! Не уверен, что нужно заливать на
        // github с реальным ключом. Иначе он будет в открытом виде в инете висеть!!!

        mHelper = new IabHelper(this, base64EncodedPublicKey);

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
*/

        //Service inapp
        Intent intent = new Intent(
                "com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        blnBind = bindService(intent,
                mServiceConn, Context.BIND_AUTO_CREATE);



        String themeName = pref.getString("theme", "1");

        if (themeName.equals("1")) {
            setTheme(R.style.AppTheme);
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window statusBar = getWindow();
                statusBar.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                statusBar.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                statusBar.setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
            }
        }
        else if (themeName.equals("2")) {
            setTheme(R.style.AppTheme2);
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window statusBar = getWindow();
                statusBar.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                statusBar.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                statusBar.setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor2));
            }
        }
        ThemeManager.init(this, 2, 0, null);


        if(isFirstLaunch) {
            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            SQLiteDatabase db = new DbHelper(this).getWritableDatabase();
            Cursor cursorMonitors = db.query("monitors", null, null, null, null, null, null);
            if(cursorMonitors != null && cursorMonitors.getCount()>0)
                mainFragment = SearchAndMonitorsFragment.newInstance(0);
            else
                mainFragment = SearchAndMonitorsFragment.newInstance(1);
            db.close();
            fTrans.add(R.id.container, mainFragment, "MAIN").commit();
        }
        backToast = Toast.makeText(this,"Нажмите еще раз для выхода",Toast.LENGTH_SHORT);
        setContentView(R.layout.main_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        mSnackBar = (SnackBar)findViewById(R.id.main_sn);



        //TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        //tv.setTextColor(Color.WHITE);

        addMonitorButton = (Button)findViewById(R.id.toolbar_add_monitor_button);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        Thread threadAvito = new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void run() {
                Document doc;
                SharedPreferences sPref;
                try {
                    String packageName =getApplicationContext().getPackageName();
                    doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName).
                            userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; ru-RU; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").
                            timeout(12000).get();
                            //"")

                    PackageManager packageManager;
                    PackageInfo packageInfo;
                    packageManager=getPackageManager();

                    packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
                    Element mainElems = doc.select("#body-content > div > div > div.main-content > div.details-wrapper.apps-secondary-color > div > div.details-section-contents > div:nth-child(4) > div.content").first();

                    if (!packageInfo.versionName.equals(mainElems.text()))
                    {
                        sPref = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putBoolean(SAVED_TEXT_WITH_VERSION, false);
                        ed.commit();
                    }
                    else
                    {
                        sPref = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putBoolean(SAVED_TEXT_WITH_VERSION, true);
                        ed.commit();

                    }
                    //SharedPreferences sPrefRemind;
                    //sPrefRemind = getPreferences(MODE_PRIVATE);
                    //sPrefRemind.edit().putBoolean(DO_NOT_REMIND, false).commit();
                }
                catch (HttpStatusException e)
                {
                    return;
                }
                catch (IOException e)
                {
                    return;
                } catch (PackageManager.NameNotFoundException e) {

                    e.printStackTrace();
                }


            }
        });


        Thread checkPurchase = new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void run()
            {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String tag = "checkPurchaseTest";
                Log.i(tag, "111111111111");
                if (!blnBind) return;
                Log.i(tag, "2222222222");
                if (mService == null) return;
                Log.i(tag, "3333333333333");

                Bundle ownedItems;
                try {
                    ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);

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
                    String purchaseData = purchaseDataList.get(i);
                    String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);

                    if(sku == PurchaseFragment.ITEM_SKU1)
                    {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("TAG_DISABLED_ADS", true);
                        editor.commit();
                    }
                    if(sku == PurchaseFragment.ITEM_SKU2)
                    {
                        // Ставим нужное количество мониторов.
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("TAG_MONITOR", true);
                        editor.commit();
                    }

                    if(sku == PurchaseFragment.ITEM_SKU3)
                    {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("TAG_FAVORITES", true);
                        editor.commit();
                    }

                    if(sku == PurchaseFragment.ITEM_SKU4)
                    {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("TAG_BUY_ALL", true);
                        editor.commit();
                    }
                }
            }
        });

        checkPurchase.run();

        SharedPreferences sPrefVersion;
        sPrefVersion = getPreferences(MODE_PRIVATE);
        Boolean isNewVersion;
        isNewVersion = sPrefVersion.getBoolean(SAVED_TEXT_WITH_VERSION, true);


        threadAvito.start();
        boolean remind=true;
        if (!isNewVersion)
        {
            Log.d("affa", "isNewVersion= "+isNewVersion);
            SharedPreferences sPref12;
            sPref12 = getPreferences(MODE_PRIVATE);
            String isNewVersion12;

            PackageManager packageManager;
            PackageInfo packageInfo;
            packageManager=getPackageManager();

            try {
                packageInfo=packageManager.getPackageInfo(getPackageName(), 0);
                isNewVersion12 = sPref12.getString("OldVersionName", packageInfo.versionName);

                if (!isNewVersion12.equals(packageInfo.versionName))
                {
                    SharedPreferences sPref;
                    sPref = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean(SAVED_TEXT_WITH_VERSION, false);
                    ed.commit();

                    SharedPreferences sPrefRemind;
                    sPrefRemind = getPreferences(MODE_PRIVATE);
                    sPrefRemind.edit().putBoolean(DO_NOT_REMIND, false).commit();
                }
                else
                    remind = false;


                SharedPreferences sPrefRemind;
                sPrefRemind = getPreferences(MODE_PRIVATE);
                sPrefRemind.edit().putString("OldVersionName", packageInfo.versionName).commit();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            SharedPreferences sPrefRemind;
            sPrefRemind = getPreferences(MODE_PRIVATE);
            Boolean dontRemind;
            dontRemind = sPrefRemind.getBoolean(DO_NOT_REMIND, false);
            Log.d("affa", "dontRemind= "+dontRemind.toString());
            Log.d("affa", "remind= "+remind);
            Log.d("affa", "44444444444444444444444= ");
            if ((!dontRemind) && (!remind)) {
                Log.d("affa", "5555555555555555555555555= ");
                SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        super.onPositiveActionClicked(fragment);
                        String packageName =getApplicationContext().getPackageName();
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                        startActivity(intent);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }

                    @Override
                    public void onNeutralActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                        SharedPreferences sPrefRemind;
                        sPrefRemind = getPreferences(MODE_PRIVATE);
                        sPrefRemind.edit().putBoolean(DO_NOT_REMIND, true).commit();
                    }
                };

                builder.message("Вы хотите обновиться до актуальной версии приложения приложение?")
                        .title("Вышло обновление!")
                        .positiveAction("Обновить")
                        .negativeAction("Отмена")
                        .neutralAction("Не напоминать");
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), null);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mNavigationDrawerFragment.getCurrentItemSelected()==0)
            mainFragment.updateMonitorsFragment();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int numberOfCallingFragment = pref.getInt("NumberOfCallingFragment", -1);
        if(getIntent().hasExtra("isFromNotification") && getIntent().getBooleanExtra("isFromNotification",false)) {
            Log.d("monitor","isFromNotification");
            numberOfCallingFragment = 0;
        }
        Log.d("monitorNOCF", String.valueOf(numberOfCallingFragment));

        if(numberOfCallingFragment != -1) {
            if((mNavigationDrawerFragment.getCurrentItemSelected() == 0 && numberOfCallingFragment == 1) ||
                    (mNavigationDrawerFragment.getCurrentItemSelected() == 1 && numberOfCallingFragment == 0))
            {
                Waiter waiter = new Waiter();
                if (Build.VERSION.SDK_INT>=11)
                        waiter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, numberOfCallingFragment, 400);
                else
                {
                    onNavigationDrawerItemSelected(numberOfCallingFragment);
                    setNavigationDrawerItem(numberOfCallingFragment);
                }
            }
            else {
                onNavigationDrawerItemSelected(numberOfCallingFragment);
                setNavigationDrawerItem(numberOfCallingFragment);
            }
            pref.edit().remove("NumberOfCallingFragment").commit();
        }

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d("monitor","position "+position);

        if(itemSelectFromTabLayout)
        {
            itemSelectFromTabLayout = false;
            return;
        }
        if(isFirstLaunch)
        {
            isFirstLaunch = false;
            return;
        }

        if(mSnackBar!=null)
            mSnackBar.dismiss();


        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        switch (position){
            case 0:
                mToolbar.setTitle("Авто Русь");
                mainFragment.setPage(0);
                if(secondFragment != null) {
                    fTrans.remove(secondFragment);
                    fTrans.show(mainFragment);
                }
                mainFragment.updateMonitorsFragment();
                break;
            case 1:
                mToolbar.setTitle("Авто Русь");
                mainFragment.setPage(1);
                if(secondFragment != null) {
                    fTrans.remove(secondFragment);
                    fTrans.show(mainFragment);
                }
                break;
            case 2:
                mToolbar.setTitle("Избранное");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new FavoritesFragment();
                fTrans.add(R.id.container, secondFragment);
                break;
            case 3:
                mToolbar.setTitle("Настройки");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new SettingsFragment();
                fTrans.add(R.id.container, secondFragment);
                break;
            case 4:
                mToolbar.setTitle("Обратная связь");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new FeedbackFragment();
                fTrans.add(R.id.container, secondFragment);
                break;
            case 5:
                mToolbar.setTitle("Покупки");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new PurchaseFragment();
                fTrans.add(R.id.container, secondFragment);
                break;

            /*ForEasyDelete
              mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                        mPurchaseFinishedListener, "mypurchasetoken2");
                break;
*/
            case 6:
                mToolbar.setTitle("Справка");
                fTrans.hide(mainFragment);
                if(secondFragment != null)
                    fTrans.remove(secondFragment);
                secondFragment = new ReferenceFragment();
                fTrans.add(R.id.container, secondFragment);
                break;

        }
        fTrans.commit();
    }

    public SnackBar getSnackBar(){
            return mSnackBar;
    }
    public Button getAddMonitorButton(){
        return addMonitorButton;
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else{
            if(backToast.getView().getWindowVisibility() == View.VISIBLE) {
                backToast.getView().setVisibility(View.INVISIBLE);
                super.onBackPressed();
            }
            else
                backToast.show();
        }
    }

    public void onClickHandlerHidden(View v){
        mainFragment.getSearchFragment().onClickHandlerHidden(v);
    }

    public void onClickClearSelection(View v){
        mainFragment.getSearchFragment().onClickClearSelection(v);
    }
    public void onClickMarkorModel(View v){
        mainFragment.getSearchFragment().onClickMarkorModelorRegion(v);
    }


    public void setNavigationDrawerItem(int itemNumber) {
        itemSelectFromTabLayout = true;
        mNavigationDrawerFragment.selectItem(itemNumber);
    }


    class Waiter extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(final Integer... params) {
            try {
                Thread.sleep(params[1]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params[0];
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(Integer values) {
            super.onPostExecute(values);
            onNavigationDrawerItemSelected(values);
            setNavigationDrawerItem(values);
        }
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

 /*ForEasyDelete
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
            else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
                Log.d( "3333333333", "8888888888888888888888888888");


                // Если наш ITEM_SKU совпадает с соответсвующем для рекламы ITEM_SKU
                // В sharedPreference сохраняем, что реклама отключена. Нужно, чтобы при обновления приложения покупка оставалась.
                //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                //SharedPreferences.Editor editor = settings.edit();
                //editor.putBoolean("TAG_DISABLED_ADS", true);
                //editor.commit();

            }

        }
    };
*/

    @Override
    public void onDestroy() {
        if (PurchaseFragment.mHelper != null) PurchaseFragment.mHelper.dispose();
        if (mService != null)
            unbindService(mServiceConn);
        PurchaseFragment.mHelper = null;
        super.onDestroy();
    }


    //Создать новое activity, иначе не работает
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!PurchaseFragment.mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
// Добавлено для сервиса.

    public static IInAppBillingService mService;
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    public Tracker getTracker(){return mTracker;}


}
