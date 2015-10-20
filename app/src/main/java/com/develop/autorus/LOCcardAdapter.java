package com.develop.autorus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.InterstitialAd;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Никита on 27.09.2015.
 */
public class LOCcardAdapter extends RecyclerView.Adapter<LOCcardAdapter.LOCviewHolder>{

    public static class LOCviewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView textViewMessage;
        TextView textViewIsNew;
        TextView textViewDateTime;
        ImageView  iv;

        LOCviewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_car);
            textViewMessage = (TextView)itemView.findViewById(R.id.text);
            textViewIsNew = (TextView)itemView.findViewById(R.id.isNew);
            textViewDateTime = (TextView)itemView.findViewById(R.id.dateTime);
            iv = (ImageView)itemView.findViewById(R.id.imageViewCarPhoto);
        }
    }

    Cars cars;
    Bitmap images[];
    Boolean isFromFavorites;
    List<CarCard> favorites;
    String dateOrID;
    Integer numberOfSite, counterIdDrom;
    InterstitialAd mInterstitialAd;

    LOCcardAdapter(Cars cars,Bitmap[] images, String dateOrID, Integer numberOfSite, InterstitialAd mAd){
        this.dateOrID = dateOrID;
        this.numberOfSite = numberOfSite;
        this.cars = cars;
        this.images = images;
        isFromFavorites = false;
        mInterstitialAd = mAd;

        if(numberOfSite == 2) {
            counterIdDrom = 0;
            while (counterIdDrom != cars.getLength() && !dateOrID.equals(cars.cars[counterIdDrom].id))
                counterIdDrom++;
        }
    }

    LOCcardAdapter(List<CarCard> cc, Bitmap[] images){
        this.favorites = cc;
        this.images = images;
        isFromFavorites = true;
    }

    private int pos;
    public int getPosition() {
        return pos;
    }
    public void setPosition(int position) {
        this.pos = position;
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

    public void insert(int position, CarCard car, Bitmap img) {
        favorites.add(position,car);
        Bitmap temp = images[position];
        images[position] = img;
        notifyItemInserted(position);
        for (int i = favorites.size()-1;i>position+1;i--) {
            images[i] = images[i-1];
            notifyItemChanged(i);
        }
        if(position+1 != images.length) {
            images[position + 1] = temp;
            notifyItemChanged(position + 1);
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

            if(!dateOrID.equals("ItIsJUST_search")) {
                if (numberOfSite != 2) {
                    if (dateOrID.equals("###"))
                        monitorViewHolder.textViewIsNew.setVisibility(View.VISIBLE);
                    else {
                        if (Long.parseLong(dateOrID) / 1000 < cars.getCarDateLong(i) / 1000) {//New cars
                            monitorViewHolder.textViewIsNew.setVisibility(View.VISIBLE);
                        } else
                            monitorViewHolder.textViewIsNew.setVisibility(View.GONE);
                    }
                } else if (dateOrID.equals("###") || i < counterIdDrom)
                    monitorViewHolder.textViewIsNew.setVisibility(View.VISIBLE);
                else
                    monitorViewHolder.textViewIsNew.setVisibility(View.GONE);
            }
            else
                monitorViewHolder.textViewIsNew.setVisibility(View.GONE);
        }

        final String finalHref = href;
        monitorViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CarWebPage.class);
                intent.putExtra("url", finalHref);
                if (!isFromFavorites) {
                    intent.putExtra("message", cars.getMessage(i));
                    intent.putExtra("image", cars.getImg(i));
                    SimpleDateFormat format;
                    if (cars.getCarDate(i).getHours() == 0 && cars.getCarDate(i).getMinutes() == 0 && cars.getCarDate(i).getSeconds() == 0)
                        format = new SimpleDateFormat("dd.MM.yyyy");
                    else
                        format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    intent.putExtra("dateTime", format.format(cars.getCarDate(i)));
                }
                intent.putExtra("isFromFavorites", isFromFavorites);

                v.getContext().startActivity(intent);
                if (mInterstitialAd != null && mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
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

        if(!isFromFavorites) {
            SimpleDateFormat format;
            if (cars.getCarDate(i).getHours() == 0 && cars.getCarDate(i).getMinutes() == 0 && cars.getCarDate(i).getSeconds() == 0)
                format = new SimpleDateFormat("dd.MM.yyyy");
            else
                format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            monitorViewHolder.textViewDateTime.setText(format.format(cars.getCarDate(i)));
        }
        else
            monitorViewHolder.textViewDateTime.setText(favorites.get(i).date);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
