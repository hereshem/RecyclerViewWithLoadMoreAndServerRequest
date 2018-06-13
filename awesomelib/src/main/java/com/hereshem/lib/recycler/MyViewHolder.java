package com.hereshem.lib.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class MyViewHolder<T> extends RecyclerView.ViewHolder {
    public MyViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void bindView(T item);
}
