package com.example.material_model_automsk;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.material_model_automsk.inappbilling.util.IabHelper;
import com.example.material_model_automsk.inappbilling.util.IabResult;
import com.example.material_model_automsk.inappbilling.util.Inventory;
import com.example.material_model_automsk.inappbilling.util.Purchase;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.Button;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.Spinner;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

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
    static android.app.Dialog dialogPicker ;
    private AlarmManager am;


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

        Intent checkIntent = new Intent(getApplicationContext(), MonitoringWork.class);
        Boolean alrarmIsActive = false;
        if (PendingIntent.getService(getApplicationContext(), 0, checkIntent, PendingIntent.FLAG_NO_CREATE) != null)
            alrarmIsActive = true;
        am = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent serviceIntent = new Intent(getApplicationContext(), MonitoringWork.class);
        PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, 0);
        am.cancel(pIntent);
        //am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5000, 240000, pIntent);

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

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeName = pref.getString("theme", "1");
        View decorView = getWindow().getDecorView();


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
            mainFragment = SearchAndMonitorsFragment.newInstance(1);
            fTrans.add(R.id.container, mainFragment, "MAIN").commit();
        }
        backToast = Toast.makeText(this,"Нажмите еще раз для выхода",Toast.LENGTH_SHORT);
        setContentView(R.layout.main_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        mSnackBar = (SnackBar)findViewById(R.id.main_sn);

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
                    doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.develop.searchmycarandroid").userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; ru-RU; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").timeout(12000).get();

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
                    //bulAvito[0] =false;
                    return;
                }
                catch (IOException e)
                {
                    //connectionAvitoSuccess[0] = false;
                    return;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });


        SharedPreferences sPrefVersion;
        sPrefVersion = getPreferences(MODE_PRIVATE);
        Boolean isNewVersion;
        isNewVersion = sPrefVersion.getBoolean(SAVED_TEXT_WITH_VERSION, true);
        threadAvito.start();
        boolean remind=true;
        if (!isNewVersion)
        {

            Log.d("aaffa", "isNewVersion= "+isNewVersion);
            Log.d("aaffa", "не новая версия!!! Так записано. Возможно, поток еще не отработал");
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
                    Log.d("aaffa", "записанная в shared запись версии НЕ совпадает с действительной=" + isNewVersion12);

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
                {
                    remind = false;
                    Log.d("aaffa", "записанная в shared запись версии  совпадает с действительной");
                }


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
            Log.d("aaffa", "dontRemind= "+dontRemind.toString());
            Log.d("aaffa", "remind= "+remind);

            if ((!dontRemind) && (!remind)) {

                AlertDialog.Builder ad;
                ad = new AlertDialog.Builder(this);
                ad.setTitle("Обновление");
                ad.setMessage("Вы хотите обновить приложение?");
                ad.setPositiveButton("Обновить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.develop.searchmycarandroid"));
                        startActivity(intent);
                    }
                });
                ad.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                    }
                });
                ad.setNeutralButton("Не напоминать", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        SharedPreferences sPrefRemind;
                        sPrefRemind = getPreferences(MODE_PRIVATE);
                        sPrefRemind.edit().putBoolean(DO_NOT_REMIND, true).commit();
                    }
                });
                ad.setCancelable(true);
                ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                    }
                });

                ad.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int numberOfCallingFragment = pref.getInt("NumberOfCallingFragment", -1);
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
                mToolbar.setTitle("Авто Москва");
                mainFragment.setPage(0);
                if(secondFragment != null) {
                    fTrans.remove(secondFragment);
                    fTrans.show(mainFragment);
                }
                break;
            case 1:
                mToolbar.setTitle("Авто Москва");
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

                ll = (LinearLayout)findViewById(R.id.search_ll_engine_type_hidden);
                b = (Button) findViewById(R.id.search_ll_engine_type_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_engine_type);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                expand(ll);
                break;
            case R.id.search_ll_price:

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

                sp1 = (Spinner) findViewById(R.id.spinner_label_price_from);
                sp2 = (Spinner) findViewById(R.id.spinner_label_price_to);

                data[0]="От";
                fillSpinner(sp1, data, 0);
                data2 = (String[])data.clone();
                data2[0]="До";
                fillSpinner(sp2, data2, 0);

                sp1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(Spinner spinner, View view, int i, long l) {
                        Integer item = spinner.getSelectedItemPosition();
                        Integer item2 = sp2.getSelectedItemPosition();
                        if (item > item2 && item2 != 0) {
                            spinner.setSelection(sp2.getSelectedItemPosition());
                            Toast.makeText(MainActivity.this, R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this, R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                ll = (LinearLayout)findViewById(R.id.search_ll_price_hidden);
                b = (Button) findViewById(R.id.search_ll_price_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_price);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                expand(ll);

                break;
            case R.id.search_ll_year:

                data = new String[36];
                for(int i = 0; i < data.length; i++)
                    data[i] = "" + String.valueOf(i + 1980);

                sp1 = (Spinner) findViewById(R.id.spinner_label_year_from);
                sp2 = (Spinner) findViewById(R.id.spinner_label_year_to);

                data[0]="От";
                fillSpinner(sp1, data, 0);
                data2 = (String[])data.clone();
                data2[0]="До";
                fillSpinner(sp2, data2, 0);

                //CardView cv = (CardView) findViewById(R.id.search_ll_year_cv);
                v.setClickable(false);


                sp1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(Spinner spinner, View view, int i, long l) {
                        Integer item = spinner.getSelectedItemPosition();
                        Integer item2 = sp2.getSelectedItemPosition();
                        if (item > item2 && item2 != 0) {
                            spinner.setSelection(sp2.getSelectedItemPosition());
                            Toast.makeText(MainActivity.this, R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this, R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                ll = (LinearLayout)findViewById(R.id.search_ll_year_hidden);
                b = (Button) findViewById(R.id.search_ll_year_clear);
                //b.setVisibility(View.INVISIBLE);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_year);
                iv.setVisibility(View.INVISIBLE);

                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                expand(ll);
                break;
            case R.id.search_ll_mileage:

                v.setClickable(false);


                data = new String[]{"0","5","10","15","20","25","30","35","40","45","50","55", "60", "65", "70","75","80","85","90","95","100","110","120","130","140","150","160","170","180","190","200","210","220","230","240","250","260","270","280","290","300","310","320","330","340","350","360","370","380","390","400","410","420","430","440","450","460","470","480","490","500","600"};
                String[] data_view = new String[data.length];
                for(int n = 1; n < data.length-1 ; ++n ){
                    data_view[n]=data[n]+" 000";
                }
                data_view[0]="0";
                data_view[data.length-1]="500 000+";

                sp1 = (Spinner) findViewById(R.id.spinner_label_mileage_from);
                sp2 = (Spinner) findViewById(R.id.spinner_label_mileage_to);

                data_view[0]="От";
                fillSpinner(sp1, data_view, 0);
                data2 = (String[])data_view.clone();
                data2[0]="До";
                fillSpinner(sp2, data2, 0);

                sp1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(Spinner spinner, View view, int i, long l) {
                        Integer item = spinner.getSelectedItemPosition();
                        Integer item2 = sp2.getSelectedItemPosition();
                        if (item > item2  && item2 != 0) {
                            spinner.setSelection(sp2.getSelectedItemPosition());
                            Toast.makeText(MainActivity.this, R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this, R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                ll = (LinearLayout)findViewById(R.id.search_ll_mileage_hidden);
                b = (Button) findViewById(R.id.search_ll_mileage_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_mileage);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                expand(ll);
                break;
            case R.id.search_ll_engine_volume:

                v.setClickable(false);
                data = new String[]{"0.0","0.6","0.7","0.8","0.9","1.0","1.1","1.2","1.3","1.4","1.5","1.6","1.7","1.8","1.9","2.0","2.1","2.2","2.3","2.4","2.5","2.6","2.7","2.8","2.9","3.0","3.1","3.2","3.3","3.4","3.5","4.0","4.5","5.0","5.5","6.0","6.0+"};

                sp1 = (Spinner) findViewById(R.id.spinner_label_engine_volume_from);
                sp2 = (Spinner) findViewById(R.id.spinner_label_engine_volume_to);

                data[0]="От";
                fillSpinner(sp1, data, 0);
                data2 = (String[])data.clone();
                data2[0]="До";
                fillSpinner(sp2, data2, 0);

                sp1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(Spinner spinner, View view, int i, long l) {
                        Integer item = spinner.getSelectedItemPosition();
                        Integer item2 = sp2.getSelectedItemPosition();
                        if (item > item2  && item2 != 0) {
                            spinner.setSelection(sp2.getSelectedItemPosition());
                            Toast.makeText(MainActivity.this, R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this, R.string.incorrect_param_search, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                ll = (LinearLayout)findViewById(R.id.search_ll_engine_volume_hidden);
                b = (Button) findViewById(R.id.search_ll_engine_volume_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_volume);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                expand(ll);
                break;
            case R.id.search_ll_trans:
                v.setClickable(false);

                ll = (LinearLayout)findViewById(R.id.search_ll_trans_hidden);
                b = (Button) findViewById(R.id.search_ll_trans_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_trans);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                expand(ll);
                break;
            case R.id.search_ll_body_type:
                v.setClickable(false);
                ll = (LinearLayout)findViewById(R.id.search_ll_body_type_hidden);
                b = (Button) findViewById(R.id.search_ll_body_type_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_body_type);
                iv.setVisibility(View.INVISIBLE);
                ll.setVisibility(View.VISIBLE);
                expand(ll);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            case R.id.search_ll_drive:
                v.setClickable(false);
                ll = (LinearLayout)findViewById(R.id.search_ll_drive_hidden);
                b = (Button) findViewById(R.id.search_ll_drive_clear);
                b.setVisibility(View.VISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_drive);
                iv.setVisibility(View.INVISIBLE);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setVisibility(View.VISIBLE);
                expand(ll);
                break;

        }
        return;
    }

    public void onClickClearSelection(View v){
        LinearLayout ll;
        Button b;
        com.rey.material.widget.CheckBox ch;
        android.support.v7.widget.AppCompatTextView t;
        SharedPreferences sPref;
        SharedPreferences.Editor ed;
        Spinner sp;
        ImageView iv;
        switch (v.getId()){
            case R.id.search_ll_year_clear:

                ll = (LinearLayout)findViewById(R.id.search_ll_year);
                ll.setClickable(true);

                ll = (LinearLayout)findViewById(R.id.search_ll_year_hidden);
                b = (Button) findViewById(R.id.search_ll_year_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_year);
                iv.setVisibility(View.VISIBLE);



                sp = (Spinner) findViewById(R.id.spinner_label_year_from);
                sp.setSelection(0);
                sp = (Spinner) findViewById(R.id.spinner_label_year_to);
                sp.setSelection(0);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                collapse(ll);
                //ll.setVisibility(View.GONE);
                break;
            case R.id.search_ll_price_clear:

                ll = (LinearLayout)findViewById(R.id.search_ll_price);
                ll.setClickable(true);

                ll = (LinearLayout)findViewById(R.id.search_ll_price_hidden);
                b = (Button) findViewById(R.id.search_ll_price_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_price);
                iv.setVisibility(View.VISIBLE);

                sp = (Spinner) findViewById(R.id.spinner_label_price_from);
                sp.setSelection(0);
                sp = (Spinner) findViewById(R.id.spinner_label_price_to);
                sp.setSelection(0);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                collapse(ll);
                break;
            case R.id.search_ll_mileage_clear:

                ll = (LinearLayout)findViewById(R.id.search_ll_mileage);
                ll.setClickable(true);

                ll = (LinearLayout)findViewById(R.id.search_ll_mileage_hidden);
                b = (Button) findViewById(R.id.search_ll_mileage_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_mileage);
                iv.setVisibility(View.VISIBLE);

                sp = (Spinner) findViewById(R.id.spinner_label_mileage_from);
                sp.setSelection(0);
                sp = (Spinner) findViewById(R.id.spinner_label_mileage_to);
                sp.setSelection(0);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                collapse(ll);
                break;
            case R.id.search_ll_engine_volume_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_engine_volume);
                ll.setClickable(true);

                ll = (LinearLayout)findViewById(R.id.search_ll_engine_volume_hidden);
                b = (Button) findViewById(R.id.search_ll_engine_volume_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_volume);
                iv.setVisibility(View.VISIBLE);

                sp = (Spinner) findViewById(R.id.spinner_label_engine_volume_from);
                sp.setSelection(0);
                sp = (Spinner) findViewById(R.id.spinner_label_engine_volume_to);
                sp.setSelection(0);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                collapse(ll);
                break;
            case R.id.search_ll_engine_type_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_engine_type);
                ll.setClickable(true);

                ll = (LinearLayout)findViewById(R.id.search_ll_engine_type_hidden);
                b = (Button) findViewById(R.id.search_ll_engine_type_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_engine_type);
                iv.setVisibility(View.VISIBLE);

                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_diesel);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_electro);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_gas);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_gasoline);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_engine_type_hybrid);
                ch.setChecked(false);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                collapse(ll);
                break;
            case R.id.search_ll_trans_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_trans);
                ll.setClickable(true);

                ll = (LinearLayout)findViewById(R.id.search_ll_trans_hidden);
                b = (Button) findViewById(R.id.search_ll_trans_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_trans);
                iv.setVisibility(View.VISIBLE);

                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_trans_auto);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_trans_man);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_trans_robot);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_trans_var);
                ch.setChecked(false);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                collapse(ll);
                break;
            case R.id.search_ll_body_type_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_body_type);
                ll.setClickable(true);

                ll = (LinearLayout)findViewById(R.id.search_ll_body_type_hidden);
                b = (Button) findViewById(R.id.search_ll_body_type_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_body_type);
                iv.setVisibility(View.VISIBLE);

                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_cabrio);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_coupe);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_hatch);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_limus);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_minivan);
                ch.setChecked(false);


                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_offroad);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_picap);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_sed);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_univ);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_body_van);
                ch.setChecked(false);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                collapse(ll);
                break;
            case R.id.search_ll_drive_clear:
                ll = (LinearLayout)findViewById(R.id.search_ll_drive);
                ll.setClickable(true);

                ll = (LinearLayout)findViewById(R.id.search_ll_drive_hidden);
                b = (Button) findViewById(R.id.search_ll_drive_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_drive);
                iv.setVisibility(View.VISIBLE);

                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_drive_backward);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_drive_forward);
                ch.setChecked(false);
                ch = (com.rey.material.widget.CheckBox) findViewById(R.id.switches_cb_drive_full);
                ch.setChecked(false);
                //ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                collapse(ll);
                break;
            case R.id.search_ll_mark_clear:
                b = (Button) findViewById(R.id.search_ll_mark_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_mark);
                iv.setVisibility(View.VISIBLE);


                t = (android.support.v7.widget.AppCompatTextView) findViewById(R.id.search_ll_mark_text);
                t.setText("Любая");

                sPref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
                ed = sPref.edit();
                ed.putString("SelectedMark","Любая").commit();
                ed.putString("SelectedModel","Любая").commit();

                CardView cv = (CardView) findViewById(R.id.search_ll_model_cardview);
                cv.setVisibility(View.GONE);
                break;
            case R.id.search_ll_model_clear:
                b = (Button) findViewById(R.id.search_ll_model_clear);
                b.setVisibility(View.INVISIBLE);
                iv = (ImageView) findViewById(R.id.arrow_model);
                iv.setVisibility(View.VISIBLE);

                t = (android.support.v7.widget.AppCompatTextView) findViewById(R.id.search_ll_model_text);
                t.setText("Любая");

                sPref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
                ed = sPref.edit();
                ed.putString("SelectedModel","Любая").commit();
                break;
        }
        return;
    }
    public void onClickMarkorModel(View v){
        Intent intent;
        final DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (v.getId()){
            case R.id.search_ll_mark_cardview :
                //Button b = (Button) findViewById(R.id.search_ll_mark_clear);
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
                intent = new Intent(this, MarkFilter.class);
                intent.putExtra("Marks",marks_arr);
                startActivity(intent);
                break;
            case R.id.search_ll_model_cardview :
                //Button b = (Button) findViewById(R.id.search_ll_mark_clear);
                //b.setVisibility(View.VISIBLE); //в новое активити перенести это
                SharedPreferences sPref2 = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
//тут
                String pos = sPref2.getString("SelectedMark", "Любая");
                if(pos.equals("Любая")){
                    Toast t = Toast.makeText(getApplicationContext(),"Для начала выберите марку",Toast.LENGTH_SHORT);
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
                intent = new Intent(this, MarkFilter.class);
                intent.putExtra("Models",models_arr);
                startActivity(intent);
                break;
        }
        db.close();
    }


    public void fillSpinner(Spinner sp,String[] data, int pos){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_spn, data);
        adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
        sp.setAdapter(adapter);
        sp.setSelection(pos);

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

        // 1dp/ms
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
        super.onDestroy();
        if (PurchaseFragment.mHelper != null) PurchaseFragment.mHelper.dispose();
        PurchaseFragment.mHelper = null;
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



}
