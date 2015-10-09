package com.example.material_model_automsk;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

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


    public class ServiceThread implements Runnable {
        public int serviceId;
        public String requestAvito, requestAuto, requestDrom;
        public ServiceThread(int Id, String requestAvito, String requestAuto, String requestDrom) {
            this.serviceId=Id;
            this.requestAuto = requestAuto;
            this.requestAvito = requestAvito;
            this.requestDrom = requestDrom;
        }
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public void run() {
            try {
                ServiceProcess(serviceId, requestAvito, requestAuto, requestDrom);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        SharedPreferences sPref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
        String[] status = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE).getString("SearchMyCarService_status", "false;false;false").split(";");
        int number = intent.getIntExtra("SearchMyCarService_serviceID",0);
        if(number == 0)
            return START_NOT_STICKY;
        if(status[number-1].equals("true")) {
            String requestAuto = sPref.getString("SearchMyCarServiceRequestAuto" + number, "###");
            String requestAvito = sPref.getString("SearchMyCarServiceRequestAvito" + number, "###");
            String requestDrom = sPref.getString("SearchMyCarServiceRequestDrom" + number, "###");
            Runnable st = new ServiceThread(number, requestAvito, requestAuto, requestDrom);
            new Thread(st).start();
        }
        return START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
    }
    public IBinder onBind(Intent intent) {
        return null;
    }




    void ServiceProcess(final int serviceID, final String requestAvito, final String requestAuto, final String requestDrom) throws InterruptedException {
        SharedPreferences sPref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
        final String lastCarDateAuto = sPref.getString("SearchMyCarService_LastCarDateAuto" + serviceID, "###");
        final String lastCarDateAvito = sPref.getString("SearchMyCarService_LastCarDateAvito" + serviceID, "###");
        final String lastCarIdDrom  = sPref.getString("SearchMyCarService_LastCarIdDrom" + serviceID, "###");
        final int[][] counter = {{0}};

        Thread t = new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void run() {
                int tryCounter = 0;
                boolean isSuccess = false, isConnectedAuto = true, isConnectedAvito = true, isConnectedDrom = true;
                Document doc = null;
                Elements mainElems;
                while(!isSuccess && tryCounter < 3) {
                    if(!requestAuto.equals("###") && (!isConnectedAuto || tryCounter == 0)) {
                        isConnectedAuto = true;
                        try {
                            doc = Jsoup.connect(requestAuto).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; ru-RU; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").timeout(12000).get();
                        } catch (IOException e) {
                            isConnectedAuto = false;
                        }
                        if(isConnectedAuto) {
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
                                        if (buf != null)
                                            counter[0][0]++;
                                    }
                                } else {
                                    for (int i = 0; i < listOfCars.size(); i++) {
                                        buf = Cars.getDateAuto(listOfCars.get(i).select("table > tbody > tr").first());
                                        if (buf != null && Long.parseLong(lastCarDateAuto)/1000 < buf.getTime()/1000)
                                            counter[0][0]++;
                                    }
                                }
                            }
                        }
                    }
                    if(!requestAvito.equals("###") && (!isConnectedAvito || tryCounter == 0)) {
                        isConnectedAvito = true;
                        try {
                            doc = Jsoup.connect(requestAvito).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; ru-RU; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").timeout(12000).get();
                        } catch (Exception e) {
                            isConnectedAvito = false;
                        }
                        if(isConnectedAvito) {
                            mainElems = doc.select("#catalog > div.layout-internal.col-12.js-autosuggest__search-list-container > div.l-content.clearfix > div.clearfix > div.catalog.catalog_table > div.catalog-list.clearfix").first().children();

                            if (lastCarDateAvito.equals("###")) {
                                for (int i = 0; i < mainElems.size(); i++)
                                    for (int j = 0; j < mainElems.get(i).children().size(); j++)
                                        counter[0][0]++;
                            } else {
                                for (int i = 0; i < mainElems.size(); i++)
                                    for (int j = 0; j < mainElems.get(i).children().size(); j++) {
                                        if (Long.parseLong(lastCarDateAvito) / 1000 < Cars.getDateAvito(mainElems.get(i).children().get(j)).getTime() / 1000)
                                            counter[0][0]++;
                                    }
                            }
                        }
                    }
                    if(!requestDrom.equals("###")  && (!isConnectedDrom || tryCounter == 0))
                    {
                        Integer counterDromCars = 0, pageCounter = 1;
                        isConnectedDrom = true;
                        mainLoop:while(counterDromCars < 20) {
                            try {
                                doc = Jsoup.connect(requestDrom.replace("page@@@page", "page"+pageCounter)).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; ru-RU; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").timeout(12000).get();
                            } catch (Exception e) {
                                isConnectedDrom = false;
                                break;
                            }
                            if(isConnectedDrom)
                            {
                                mainElems = doc.select("body > div.main0 > div.main1 > div.main2 > table:nth-child(2) > tbody > tr > td:nth-child(1) > div.content > div:nth-child(2)");
                                if(!mainElems.isEmpty())
                                    mainElems = mainElems.select("table.newCatList.visitedT");
                                else
                                    break;

                                if (!mainElems.isEmpty())
                                {
                                    mainElems = mainElems.select("tbody").first().children();
                                    for (int i = 0; i < mainElems.size(); i++)
                                        if (mainElems.get(i).className().equals("row"))
                                        {
                                            String id = Cars.getCarIdDrom(mainElems.get(i));
                                            if(!id.equals("pinned"))
                                            {
                                                counterDromCars++;
                                                if(!id.equals(lastCarIdDrom))
                                                    counter[0][0]++;
                                                else
                                                    break mainLoop;
                                            }
                                        }
                                }
                                else
                                    break;
                            }
                            pageCounter++;
                        }
                    }
                    if(isConnectedAuto && isConnectedAvito && isConnectedDrom)
                        break;
                    tryCounter++;
                }
            }
        });
        t.start();
        while (t.isAlive());
        if(counter[0][0] != 0) {
            sendNotification(counter[0][0], serviceID);
        }
        stopSelf();
    }

    void sendNotification(int countOfNewCars, int serviceID) {


        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("NotificationMessage", serviceID);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, serviceID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this);
        Notification notification = builder.setContentIntent(pIntent)
                .setSmallIcon(R.drawable.status_bar).setTicker("Свежие авто!").setWhen(System.currentTimeMillis())
                .setAutoCancel(true).setContentTitle("Авто Москва")
                .setContentText(countOfNewCars+" cвежих авто! Кликай скорей!").build();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);

        /*
        Notification notif = new Notification(R.drawable.status_bar, "Новое авто!",
                System.currentTimeMillis());

        String shrtMessage = "";
        SharedPreferences sPref = getSharedPreferences("SearchMyCarPreferences", Context.MODE_PRIVATE);
        if(!sPref.getString("SearchMyCarService_shortMessage"+serviceID,"###").equals("###"))
            shrtMessage = sPref.getString("SearchMyCarService_shortMessage"+serviceID,"###");
        else
            shrtMessage = "авто";

        Uri ringURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notif.number = countOfNewCars;
        notif.sound = ringURI;
        notif.flags |= Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_SOUND | Notification.FLAG_ONLY_ALERT_ONCE;
        nm.notify(serviceID, notif);
        */
    }

}
