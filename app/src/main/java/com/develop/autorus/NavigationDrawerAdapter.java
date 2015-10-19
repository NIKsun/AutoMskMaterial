package com.develop.autorus;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

    private List<NavigationItem> mData;
    private NavigationDrawerCallbacks mNavigationDrawerCallbacks;
    private View mSelectedView;
    private int mSelectedPosition;
    Context mContext;

    public NavigationDrawerAdapter(List<NavigationItem> data, Context context) {
        mData = data;
        mContext = context;
    }

    public NavigationDrawerCallbacks getNavigationDrawerCallbacks() {
        return mNavigationDrawerCallbacks;
    }

    public void setNavigationDrawerCallbacks(NavigationDrawerCallbacks navigationDrawerCallbacks) {
        mNavigationDrawerCallbacks = navigationDrawerCallbacks;
    }

    @Override
    public NavigationDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.drawer_row, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.itemView.setClickable(true);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       if (mSelectedView != null) {
                                                           mSelectedView.setSelected(false);
                                                       }
                                                       mSelectedPosition = viewHolder.getAdapterPosition();
                                                       v.setSelected(true);
                                                       mSelectedView = v;
                                                       if (mNavigationDrawerCallbacks != null)
                                                           mNavigationDrawerCallbacks.onNavigationDrawerItemSelected(viewHolder.getAdapterPosition());
                                                   }
                                               }
        );
        viewHolder.itemView.setBackgroundResource(R.drawable.row_selector);
        return viewHolder;
    }

    private Boolean isItemBackgroundTransparent = false;
    public void setItemBackgroundTransparent()
    {
        isItemBackgroundTransparent = true;
    }

    @Override
    public void onBindViewHolder(NavigationDrawerAdapter.ViewHolder viewHolder, int i) {
        if(isItemBackgroundTransparent)
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.myTransparent));
        viewHolder.textView.setText(mData.get(i).getText());
        viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(mData.get(i).getDrawable(), null, null, null);
        if (mSelectedPosition == i) {
            if (mSelectedView != null) {
                mSelectedView.setSelected(false);
            }
            mSelectedPosition = i;
            mSelectedView = viewHolder.itemView;
            mSelectedView.setSelected(true);
        }
    }


    public void selectPosition(int position) {
        mSelectedPosition = position;
        notifyItemChanged(position);
    }


    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_name);
        }
    }
}