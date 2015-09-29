package com.example.material_model_automsk;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.rey.material.widget.ProgressView;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Никита on 24.09.2015.
 */
public class LOCfragment extends Fragment {

    final private static int RND_PXLS = 10;
    private int numberOfSite;
    private int filterID;
    ProgressView pvCircular;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Cars cars = null;
    View savedView = null;
    LoadListView loader;
    Boolean imageLoaderMayRunning = true;

    public static LOCfragment newInstance(int page, int filterID) {
        LOCfragment fragment = new LOCfragment();
        fragment.numberOfSite = page;
        fragment.filterID = filterID;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new LoadListView();
    }
    @Override
    public void onDestroy() {
        loader.cancel(true);
        imageLoaderMayRunning = false;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(cars == null) {
            if(loader.getStatus()== AsyncTask.Status.RUNNING || loader.getStatus()== AsyncTask.Status.FINISHED)
                return savedView;
            else{
                savedView = inflater.inflate(R.layout.fragment_list_of_cars, container, false);
                mSwipeRefreshLayout = (SwipeRefreshLayout)savedView.findViewById(R.id.swipe_refresh_layout);

                mSwipeRefreshLayout.setColorSchemeResources(R.color.orange,R.color.green,R.color.blue);

                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh(){
                        loader.cancel(true);
                        imageLoaderMayRunning = false;
                        loader = new LoadListView();
                        loader.execute();
                    }
                });
                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                pvCircular = (ProgressView)savedView.findViewById(R.id.progress_circular);
                loader.execute();
            }
        }
        return savedView;
    }

    class LoadListView extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!mSwipeRefreshLayout.isRefreshing())
                pvCircular.start();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            //РАБОТА С БД, ВЫЗОВ onProgressUpdate

            Document doc = null;
            Elements mainElems;
            Boolean isNotFound = false, isNotConnected = false;
            Cars carsBuf = null;
            switch (numberOfSite){
                case 0:
                    try {
                        doc = Jsoup.connect("http://auto.ru/cars/chevrolet/lanos/group-sedan/all/").userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; ru-RU; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").timeout(12000).get();
                    }
                    catch (HttpStatusException e)
                    {
                        isNotFound = true;
                        break;
                    }
                    catch (IOException e) {
                        isNotConnected = true;
                        break;
                    }
                    mainElems = doc.select("body > div.branding_fix > div.content.content_style > article > div.clearfix > div.b-page-wrapper > div.b-page-content");

                    if(mainElems.isEmpty())
                        isNotFound = true;
                    else
                        mainElems = mainElems.first().children();

                    if(!isNotFound) {
                        Elements listOfCars = null;
                        for (int i = 0; i < mainElems.size(); i++) {
                            String className = mainElems.get(i).className();
                            if ((className.indexOf("widget widget_theme_white sales-list") == 0) && (className.length() == 36)) {
                                listOfCars = mainElems.get(i).select("div.sales-list-item");
                                break;
                            }
                        }
                        if (listOfCars == null) {
                            isNotFound = true;
                        } else {
                            carsBuf = new Cars(listOfCars.size());
                            for (int i = 0; i < listOfCars.size(); i++)
                                carsBuf.addFromAutoRu(listOfCars.get(i).select("table > tbody > tr").first());
                        }

                    }
                    break;
                case 1:
                    try {
                        doc = Jsoup.connect("https://www.avito.ru/moskva/avtomobili/chevrolet/lanos").userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; ru-RU; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").timeout(12000).get();
                    }
                    catch (HttpStatusException e)
                    {
                        isNotFound = true;
                        break;
                    }
                    catch (IOException e) {
                        isNotConnected = true;
                        break;
                    }
                    mainElems = doc.select("#catalog > div.layout-internal.col-12.js-autosuggest__search-list-container > div.l-content.clearfix > div.clearfix > div.catalog.catalog_table > div.catalog-list.clearfix");
                    if(mainElems != null)
                        mainElems = mainElems.first().children();
                    else {
                        isNotFound = true;
                        break;
                    }

                    int length = 0;
                    for (int i = 0; i < mainElems.size(); i++)
                        length += mainElems.get(i).children().size();

                    carsBuf = new Cars(length);
                    for (int i = 0; i < mainElems.size(); i++)
                        for (int j = 0; j < mainElems.get(i).children().size(); j++) {
                            carsBuf.addFromAvito(mainElems.get(i).children().get(j));
                        }
                    carsBuf.sortByDateAvito();
                    break;
                case 2:
                    int counter = 0;
                    int pageCounter = 1;
                    carsBuf = new Cars(50);
                    while(counter < 20) {
                        try {
                            doc = Jsoup.connect("http://auto.drom.ru/chevrolet/lanos/page@@@page/?go_search=2".replace("page@@@page", "page"+pageCounter)).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; ru-RU; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").timeout(12000).get();
                        } catch (HttpStatusException e) {
                            isNotFound = true;
                            break;
                        } catch (IOException e) {
                            isNotConnected = true;
                            break;
                        }

                        mainElems = doc.select("body > div.main0 > div.main1 > div.main2 > table:nth-child(2) > tbody > tr > td:nth-child(1) > div.content > div:nth-child(2)");
                        if(!mainElems.isEmpty())
                            mainElems = mainElems.select("table.newCatList.visitedT");
                        else {
                            if (counter == 0) {
                                isNotFound = true;
                                break;
                            } else
                                break;
                        }

                        if (!mainElems.isEmpty()) {
                            mainElems = mainElems.select("tbody").first().children();
                            for (int i = 0; i < mainElems.size(); i++) {
                                if (mainElems.get(i).className().equals("row"))
                                    if (carsBuf.appendFromDromRu(mainElems.get(i)))
                                        counter++;
                            }
                        } else {
                            if(counter == 0) {
                                isNotFound = true;
                                break;
                            }
                            else
                                break;
                        }
                        pageCounter++;
                        if (pageCounter > 5)
                        {
                            isNotFound = true;
                            break;
                        }
                    }
                    break;
            }
            if(!isNotConnected && !isNotFound)
                //Нужно не перезаписывать, а добовлять!
                cars = carsBuf;
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
            mSwipeRefreshLayout.setRefreshing(false);
            pvCircular.stop();
            imageLoaderMayRunning = true;

            if(cars != null) {
                final Bitmap images[] = new Bitmap[cars.getLength()];
                Bitmap loadingImage = BitmapFactory.decodeResource(getResources(), R.drawable.car_loading_pic);
                for(int i=0;i<cars.getLength();i++)
                    images[i] = loadingImage;

                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    public void run() {
                        for (int i = 0; i < cars.getLength(); i++) {
                            try {
                                if(imageLoaderMayRunning)
                                    images[i] = getRoundedCornerBitmap(BitmapFactory.decodeStream((InputStream) new URL(cars.getImg(i)).getContent()), RND_PXLS);
                                else
                                    return;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            final int finalI = i;
                            handler.post(new Runnable() {
                                public void run() {
                                    RecyclerView rv = (RecyclerView) savedView.findViewById(R.id.rv_cars);
                                    rv.getAdapter().notifyItemChanged(finalI);
                                }
                            });
                        }
                    }
                };
                new Thread(runnable).start();
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                RecyclerView rv = (RecyclerView) savedView.findViewById(R.id.rv_cars);
                LinearLayoutManager llm = new LinearLayoutManager(savedView.getContext());
                rv.setLayoutManager(llm);
                LOCcardAdapter adapter = new LOCcardAdapter(cars, images);
                rv.setAdapter(adapter);
            }
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
