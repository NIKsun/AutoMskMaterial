package com.example.material_model_automsk;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.widget.ProgressView;

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
    ProgressView pvCircular;
    List<CarCard> favorites;
    Bitmap images[];
    View savedView;
    LOCcardAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedView = inflater.inflate(R.layout.fragment_favorites, container, false);
        RecyclerView rv = (RecyclerView)savedView.findViewById(R.id.rv_favorites);
        pvCircular = (ProgressView)savedView.findViewById(R.id.progress_circular_favorites);
        LinearLayoutManager llm = new LinearLayoutManager(savedView.getContext());
        rv.setLayoutManager(llm);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                SQLiteDatabase db = new DbHelper(getActivity()).getWritableDatabase();
                db.delete("favorites","href = ?", new String[]{favorites.get(viewHolder.getPosition()).href});
                adapter.remove(viewHolder.getPosition());
                db.close();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);

        registerForContextMenu(rv);
        LoadListView llv = new LoadListView();
        llv.execute();
        return savedView;
    }

    @Override
    public void onDestroy() {
        imageLoaderMayRunning = false;
        super.onDestroy();
    }

    class LoadListView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pvCircular.start();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            SQLiteDatabase db = new DbHelper(getActivity()).getWritableDatabase();
            Cursor cursor = db.query("favorites", null, null, null, null, null, null);
            int indexHref = cursor.getColumnIndex("href");
            int indexImage = cursor.getColumnIndex("image");
            int indexMessage = cursor.getColumnIndex("message");
            int indexDate = cursor.getColumnIndex("dateTime");
            favorites = new ArrayList<>();
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
            images = new Bitmap[favorites.size()];
            Bitmap loadingImage = BitmapFactory.decodeResource(getResources(), R.drawable.car_loading_pic);
            for (int i = 0; i < favorites.size(); i++)
                images[i] = loadingImage;
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(Void isNotFound) {
            super.onPostExecute(isNotFound);
            pvCircular.stop();
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                public void run() {
                    for (int i = 0; i < favorites.size(); i++) {
                        try {
                            if (imageLoaderMayRunning) {
                                images[i] = LOCfragment.getRoundedCornerBitmap(BitmapFactory.decodeStream((InputStream) new URL(favorites.get(i).img).getContent()),LOCfragment.RND_PXLS);
                                if (images[i] == null)
                                    images[i] = BitmapFactory.decodeResource(getResources(), R.drawable.car_loading_pic);
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
            RecyclerView rv = (RecyclerView)savedView.findViewById(R.id.rv_favorites);
            adapter = new LOCcardAdapter(favorites,images);
            rv.setAdapter(adapter);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        Context context = getContext();
        Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
        getActivity().getMenuInflater().inflate(R.menu.fav_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(!this.isMenuVisible())
            return super.onContextItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.item_web:
                Intent intent = new Intent(getContext(), CarWebPage.class);
                intent.putExtra("url", favorites.get(adapter.getPosition()).href);
                intent.putExtra("isFromFavorites", true);
                getContext().startActivity(intent);
                break;
            case R.id.item_in_favorites:
                SQLiteDatabase db = new DbHelper(getActivity()).getWritableDatabase();
                db.delete("favorites","href = ?", new String[]{favorites.get(adapter.getPosition()).href});
                adapter.remove(adapter.getPosition());
                db.close();
                break;
        }
        return super.onContextItemSelected(item);
    }
}
