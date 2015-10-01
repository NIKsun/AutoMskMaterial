package com.example.material_model_automsk;

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
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Никита on 27.09.2015.
 */
public class LOCcardAdapter extends RecyclerView.Adapter<LOCcardAdapter.LOCviewHolder>{

    public static class LOCviewHolder extends RecyclerView.ViewHolder {
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

    LOCcardAdapter(Cars cars,Bitmap[] images){
        this.cars = cars;
        this.images = images;
    }

    @Override
    public int getItemCount() {
        return cars.getLength();
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
        monitorViewHolder.textViewMessage.setText(Html.fromHtml(cars.getMessage(i)));
        monitorViewHolder.textViewIsNew.setHeight(0);
        monitorViewHolder.textViewIsNew.setVisibility(View.VISIBLE);
        monitorViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CarWebPage.class);
                intent.putExtra("url", cars.getHref(i));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
