package com.develop.autorus;

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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.SnackBar;

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

    CarCard tempCar;
    Bitmap tempImage;
    int tempPosition;

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
                if(tempCar != null)
                {
                    deleteItemFromDB(tempCar.href);
                }
                tempPosition = viewHolder.getPosition();
                tempCar = favorites.get(tempPosition);
                tempImage = images[tempPosition];

                adapter.remove(tempPosition);
                SnackBar sb = ((MainActivity)getActivity()).getSnackBar();
                sb.applyStyle(R.style.SnackBarSingleLine);
                sb.actionTextColor(getContext().getResources().getColor(R.color.myPrimaryColor));

                sb.text("Удален из избранного")
                        .actionText("Восстановить")
                        .duration(2500)
                        .actionClickListener(new SnackBar.OnActionClickListener() {
                            @Override
                            public void onActionClick(SnackBar snackBar, int i) {
                                if(tempCar != null) {
                                    adapter.insert(tempPosition, tempCar, tempImage);
                                    tempCar = null;
                                    RecyclerView rv = (RecyclerView) savedView.findViewById(R.id.rv_favorites);
                                    rv.scrollToPosition(tempPosition);
                                }
                            }
                        });
                sb.show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);

        registerForContextMenu(rv);
        LoadListView llv = new LoadListView();
        llv.execute();
        return savedView;
    }

    private void deleteItemFromDB(String href)
    {
        SQLiteDatabase db = new DbHelper(getActivity()).getWritableDatabase();
        db.delete("favorites", "href = ?", new String[]{href});
        db.close();
    }

    @Override
    public void onDestroy() {
        imageLoaderMayRunning = false;
        if(tempCar != null)
        {
            deleteItemFromDB(tempCar.href);
        }
        ((MainActivity)getActivity()).getSnackBar().dismiss();
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
            SQLiteDatabase db;
            try {
                db = new DbHelper(getActivity()).getWritableDatabase();
            }
            catch (Exception ex)
            {
                return null;
            }
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
            if(favorites == null)
                return;
            pvCircular.stop();
            final Handler handler = new Handler();
            ((MainActivity)getActivity()).getTracker().send(new HitBuilders.EventBuilder().
                    setCategory("Favorites count").setAction(String.valueOf(favorites.size())).setValue(1).build());

            if(favorites.size() != 0) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        for (int i = 0; i < favorites.size(); i++) {
                            try {
                                if (imageLoaderMayRunning) {
                                    images[i] = LOCfragment.getRoundedCornerBitmap(BitmapFactory.decodeStream((InputStream) new URL(favorites.get(i).img).getContent()), LOCfragment.RND_PXLS);
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
                TextView message =(TextView) savedView.findViewById(R.id.message_about_empty);
                message.setVisibility(View.GONE);
                new Thread(runnable).start();
                RecyclerView rv = (RecyclerView) savedView.findViewById(R.id.rv_favorites);
                adapter = new LOCcardAdapter(favorites, images);
                rv.setAdapter(adapter);
            }
            else
            {
                TextView message =(TextView) savedView.findViewById(R.id.message_about_empty);
                message.setVisibility(View.VISIBLE);
            }
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

                if(tempCar != null)
                {
                    deleteItemFromDB(tempCar.href);
                }
                tempPosition = adapter.getPosition();
                tempCar = favorites.get(tempPosition);
                tempImage = images[tempPosition];
                adapter.remove(adapter.getPosition());
                SnackBar sb = ((MainActivity)getActivity()).getSnackBar();
                sb.applyStyle(R.style.SnackBarSingleLine);
                sb.actionTextColor(getContext().getResources().getColor(R.color.myPrimaryColor));

                sb.text("Удален из избранного")
                        .actionText("Восстановить")
                        .duration(2500)
                        .actionClickListener(new SnackBar.OnActionClickListener() {
                            @Override
                            public void onActionClick(SnackBar snackBar, int i) {
                                if(tempCar != null) {
                                    adapter.insert(tempPosition, tempCar, tempImage);
                                    RecyclerView rv = (RecyclerView) savedView.findViewById(R.id.rv_favorites);
                                    rv.scrollToPosition(tempPosition);
                                    tempCar = null;
                                }
                            }
                        });
                sb.show();
                break;
        }
        return super.onContextItemSelected(item);
    }
}
