package com.example.material_model_automsk;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Никита on 24.09.2015.
 */

class CarCard
{
    String href;
    String img;
    String msg;
    String date;
    CarCard(String href, String img, String msg, String date)
    {
        this.date = date;
        this.href = href;
        this.img = img;
        this.msg = msg;
    }
}

public class FavoritesFragment extends Fragment {

    Boolean imageLoaderMayRunning = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View savedView = inflater.inflate(R.layout.fragment_favorites, container, false);
        RecyclerView rv = (RecyclerView)savedView.findViewById(R.id.rv_favorites);
        LinearLayoutManager llm = new LinearLayoutManager(savedView.getContext());
        rv.setLayoutManager(llm);
        registerForContextMenu(rv);

        SQLiteDatabase db = new DbHelper(getActivity()).getWritableDatabase();
        Cursor cursor = db.query("favorites", null, null, null, null, null, null);
        int indexHref = cursor.getColumnIndex("href");
        int indexImage = cursor.getColumnIndex("image");
        int indexMessage = cursor.getColumnIndex("message");
        int indexDate = cursor.getColumnIndex("dateTime");
        final List<CarCard> favorites = new ArrayList<CarCard>();
        if (cursor.moveToFirst()) {
            do {
                favorites.add(new CarCard(
                        cursor.getString(indexHref),
                        cursor.getString(indexImage),
                        cursor.getString(indexMessage),
                        cursor.getString(indexDate)));
            } while (cursor.moveToNext());
        }
        db.close();
        final Bitmap images[] = new Bitmap[favorites.size()];
        final Bitmap loadingImage = BitmapFactory.decodeResource(getResources(), R.drawable.car_loading_pic);
        for (int i = 0; i < favorites.size(); i++)
            images[i] = loadingImage;

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                for (int i = 0; i < favorites.size(); i++) {
                    try {
                        if (imageLoaderMayRunning) {
                            images[i] = LOCfragment.getRoundedCornerBitmap(BitmapFactory.decodeStream((InputStream) new URL(favorites.get(i).img).getContent()),LOCfragment.RND_PXLS);
                            if (images[i] == null)
                                images[i] = loadingImage;
                        } else
                            return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final int finalI = i;
                    handler.post(new Runnable() {
                        public void run() {
                            RecyclerView rv = (RecyclerView) savedView.findViewById(R.id.rv_favorites);
                            rv.getAdapter().notifyItemChanged(finalI);
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();

        LOCcardAdapter adapter = new LOCcardAdapter(favorites,images);
        rv.setAdapter(adapter);
        return savedView;
    }

    @Override
    public void onDestroy() {
        imageLoaderMayRunning = false;
        super.onDestroy();
    }
}
