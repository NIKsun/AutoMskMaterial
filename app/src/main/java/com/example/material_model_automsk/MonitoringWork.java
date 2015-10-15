package com.example.material_model_automsk;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Никита on 03.09.2015.
 */
public class MonitoringWork extends Service {
    NotificationManager nm;
    private static final int NOTIFY_ID = 530;

    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Thread thread = new Thread(new Runnable() {
            public void run() {
                final SQLiteDatabase db = new DbHelper(getBaseContext()).getWritableDatabase();
                Cursor cursorMonitors = db.query("monitors", null, null, null, null, null, null);
                int iMonitorID = cursorMonitors.getColumnIndex("id");
                int iHrefAuto = cursorMonitors.getColumnIndex("href_auto");
                int iHrefAvito = cursorMonitors.getColumnIndex("href_avito");
                int iHrefDrom = cursorMonitors.getColumnIndex("href_drom");
                int iLastCarDateAuto = cursorMonitors.getColumnIndex("date_auto");
                int iLastCarDateAvito = cursorMonitors.getColumnIndex("date_avito");
                int iLastCarIdDrom = cursorMonitors.getColumnIndex("id_drom");
                int iIsActive = cursorMonitors.getColumnIndex("is_active");
                int iCONC = cursorMonitors.getColumnIndex("count_of_new_cars");
                final int[][] counter = {{0}};
                Integer CONC_counter = 0;

                if (cursorMonitors.moveToFirst()) {
                    do {
                        Log.d("notification", String.valueOf(cursorMonitors.getInt(iIsActive)));
                        Log.d("notification", String.valueOf(cursorMonitors.getInt(iCONC)));
                        if(cursorMonitors.getInt(iIsActive) == 1){
                            if(cursorMonitors.getInt(iCONC) >= 100){
                                CONC_counter++;
                                continue;
                            }
                            final int[] monitorCounter = {0};
                            final String requestAuto = cursorMonitors.getString(iHrefAuto);
                            final String requestAvito = cursorMonitors.getString(iHrefAvito);
                            final String requestDrom = cursorMonitors.getString(iHrefDrom);

                            final String lastCarDateAuto = cursorMonitors.getString(iLastCarDateAuto) != null ? cursorMonitors.getString(iLastCarDateAuto) : "###";
                            final String lastCarDateAvito = cursorMonitors.getString(iLastCarDateAvito) != null ? cursorMonitors.getString(iLastCarDateAvito) : "###";
                            final String lastCarIdDrom = cursorMonitors.getString(iLastCarIdDrom) != null ? cursorMonitors.getString(iLastCarIdDrom) : "###";


                            Thread t = new Thread(new Runnable() {
                                public void run() {
                                    int tryCounter = 0;
                                    boolean isSuccess = false, isConnectedAuto = true, isConnectedAvito = true, isConnectedDrom = true;
                                    Document doc = null;
                                    Elements mainElems;
                                    while (!isSuccess && tryCounter < 3) {
                                        if (!requestAuto.equals("###") && (!isConnectedAuto || tryCounter == 0)) {
                                            isConnectedAuto = true;
                                            try {
                                                doc = Jsoup.connect(requestAuto).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; ru-RU; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").timeout(12000).get();
                                            } catch (IOException e) {
                                                isConnectedAuto = false;
                                            }
                                            if (isConnectedAuto) {
                                                mainElems = doc.select("body > div.branding_fix > div.content.content_style > article > div.clearfix > div.b-page-wrapper > div.b-page-content").first().children();

                                                Elements listOfCars = null;
                                                for (int i = 0; i < mainElems.size(); i++) {
                                                    String className = mainElems.get(i).className();
                                                    if ((className.indexOf("widget widget_theme_white sales-list") == 0) && (className.length() == 36)) {
                                                        listOfCars = mainElems.get(i).select("div.sales-list-item");
                                                        break;
                                                    }
                                                }
                                                if (listOfCars != null) {
                                                    Date buf;
                                                    if (lastCarDateAuto.equals("###")) {
                                                        for (int i = 0; i < listOfCars.size(); i++) {
                                                            buf = Cars.getDateAuto(listOfCars.get(i).select("table > tbody > tr").first());
                                                            if (buf != null) {
                                                                counter[0][0]++;
                                                                monitorCounter[0]++;
                                                            }
                                                        }
                                                    } else {
                                                        for (int i = 0; i < listOfCars.size(); i++) {
                                                            buf = Cars.getDateAuto(listOfCars.get(i).select("table > tbody > tr").first());
                                                            if (buf != null && Long.parseLong(lastCarDateAuto) / 1000 < buf.getTime() / 1000) {
                                                                counter[0][0]++;
                                                                monitorCounter[0]++;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (!requestAvito.equals("###") && (!isConnectedAvito || tryCounter == 0)) {
                                            isConnectedAvito = true;
                                            try {
                                                doc = Jsoup.connect(requestAvito).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; ru-RU; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").timeout(12000).get();
                                            } catch (Exception e) {
                                                isConnectedAvito = false;
                                            }
                                            if (isConnectedAvito) {
                                                mainElems = doc.select("#catalog > div.layout-internal.col-12.js-autosuggest__search-list-container > div.l-content.clearfix > div.clearfix > div.catalog.catalog_table > div.catalog-list.clearfix").first().children();

                                                if (lastCarDateAvito.equals("###")) {
                                                    for (int i = 0; i < mainElems.size(); i++)
                                                        for (int j = 0; j < mainElems.get(i).children().size(); j++) {
                                                            counter[0][0]++;
                                                            monitorCounter[0]++;
                                                        }
                                                } else {
                                                    for (int i = 0; i < mainElems.size(); i++)
                                                        for (int j = 0; j < mainElems.get(i).children().size(); j++) {
                                                            if (Long.parseLong(lastCarDateAvito) / 1000 < Cars.getDateAvito(mainElems.get(i).children().get(j)).getTime() / 1000) {
                                                                counter[0][0]++;
                                                                monitorCounter[0]++;
                                                            }
                                                        }
                                                }
                                            }
                                        }
                                        if (!requestDrom.equals("###") && (!isConnectedDrom || tryCounter == 0)) {
                                            Integer counterDromCars = 0, pageCounter = 1;
                                            isConnectedDrom = true;
                                            mainLoop:
                                            while (counterDromCars < 20) {
                                                try {
                                                    doc = Jsoup.connect(requestDrom.replace("page@@@page", "page" + pageCounter)).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; ru-RU; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").timeout(12000).get();
                                                } catch (Exception e) {
                                                    isConnectedDrom = false;
                                                    break;
                                                }
                                                if (isConnectedDrom) {
                                                    mainElems = doc.select("body > div.main0 > div.main1 > div.main2 > table:nth-child(2) > tbody > tr > td:nth-child(1) > div.content > div:nth-child(2)");
                                                    if (!mainElems.isEmpty())
                                                        mainElems = mainElems.select("table.newCatList.visitedT");
                                                    else
                                                        break;

                                                    if (!mainElems.isEmpty()) {
                                                        mainElems = mainElems.select("tbody").first().children();
                                                        for (int i = 0; i < mainElems.size(); i++)
                                                            if (mainElems.get(i).className().equals("row")) {
                                                                String id = Cars.getCarIdDrom(mainElems.get(i));
                                                                if (!id.equals("pinned")) {
                                                                    counterDromCars++;
                                                                    if (!id.equals(lastCarIdDrom)) {
                                                                        counter[0][0]++;
                                                                        monitorCounter[0]++;
                                                                    } else
                                                                        break mainLoop;
                                                                }
                                                            }
                                                    } else
                                                        break;
                                                }
                                                pageCounter++;
                                                if (pageCounter > 8)
                                                    break;
                                            }
                                        }
                                        if (isConnectedAuto && isConnectedAvito && isConnectedDrom)
                                            break;
                                        tryCounter++;
                                    }
                                }
                            });
                            t.start();
                            while (t.isAlive()) ;

                            ContentValues container = new ContentValues();
                            container.put("count_of_new_cars", monitorCounter[0]);
                            db.update("monitors", container, "id = ?", new String[]{String.valueOf(cursorMonitors.getInt(iMonitorID))});
                        }
                    } while (cursorMonitors.moveToNext());
                }
                db.close();
                if (counter[0][0]+100*CONC_counter != 0) {
                    sendNotification(counter[0][0]+100*CONC_counter, CONC_counter);
                }
                stopSelf();

            }
        });
        thread.start();

        return START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
    }
    public IBinder onBind(Intent intent) {
        return null;
    }


    void sendNotification(int countOfNewCars, Integer CONC_counter) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isFromNotification",true);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this);
        Notification notification = builder.setContentIntent(pIntent)
                .setSmallIcon(R.drawable.status_bar).setTicker("Свежие авто!")
                .setAutoCancel(true).setContentTitle("Авто Москва")
                .setContentText((CONC_counter > 0 ? "Более " : "") +countOfNewCars +" cвежих авто! Кликай скорей!").build();


        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
        notification.defaults = 0;
        if(sPref.getBoolean("soundIsActive",true))
            notification.defaults |= Notification.DEFAULT_SOUND;
        if(sPref.getBoolean("vibrationIsActive",true))
            notification.defaults |= Notification.DEFAULT_VIBRATE;

        nm.notify(NOTIFY_ID, notification);
    }

}
