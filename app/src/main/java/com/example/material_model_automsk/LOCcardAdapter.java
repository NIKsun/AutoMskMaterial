package com.example.material_model_automsk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import android.os.Vibrator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by Никита on 27.09.2015.
 */
public class LOCcardAdapter extends RecyclerView.Adapter<LOCcardAdapter.LOCviewHolder>{

    public static class LOCviewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView textViewMessage;
        TextView textViewIsNew;
        ImageView  iv;

        LOCviewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_car);
            textViewMessage = (TextView)itemView.findViewById(R.id.text);
            textViewIsNew = (TextView)itemView.findViewById(R.id.isNew);
            iv = (ImageView)itemView.findViewById(R.id.imageViewCarPhoto);
        }
    }

    Cars cars;
    Bitmap images[];
    Boolean isFromFavorites;
    List<CarCard> favorites;

    LOCcardAdapter(Cars cars,Bitmap[] images){
        this.cars = cars;
        this.images = images;
        isFromFavorites = false;
    }

    LOCcardAdapter(List<CarCard> cc, Bitmap[] images){
        this.favorites = cc;
        this.images = images;
        isFromFavorites = true;
    }

    private int position;
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getItemCount() {
        if(isFromFavorites)
            return favorites.size();
        else
            return cars.getLength();
    }


    public void remove(int position) {
        favorites.remove(position);
        notifyItemRemoved(position);
        for (int i = position;i<favorites.size();i++) {
            if(i+1<images.length)
                images[i] = images[i+1];
            notifyItemChanged(i);
        }
    }

    @Override
    public LOCviewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_car, viewGroup, false);
        LOCviewHolder pvh = new LOCviewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final LOCviewHolder monitorViewHolder, final int i) {

        monitorViewHolder.iv.setImageBitmap(images[i]);
        String href = null;
        if (isFromFavorites)
        {
            href = favorites.get(i).href;
            monitorViewHolder.textViewMessage.setText(Html.fromHtml(favorites.get(i).msg));
        }
        else {
            href = cars.getHref(i);
            monitorViewHolder.textViewMessage.setText(Html.fromHtml(cars.getMessage(i)));
            //monitorViewHolder.textViewIsNew.setHeight(0);
            monitorViewHolder.textViewIsNew.setVisibility(View.VISIBLE);
        }
        final String finalHref = href;
        monitorViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CarWebPage.class);
                intent.putExtra("url", finalHref);
                if(!isFromFavorites) {
                    intent.putExtra("message", cars.getMessage(i));
                    intent.putExtra("image", cars.getImg(i));
                    intent.putExtra("dateTime", "12.21.42");
                }
                intent.putExtra("isFromFavorites", isFromFavorites);
                v.getContext().startActivity(intent);
            }
        });
        monitorViewHolder.cv.setLongClickable(true);
        monitorViewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(i);
                return false;
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
