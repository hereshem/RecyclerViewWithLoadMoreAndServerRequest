package com.hereshem.lib.recycler;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hereshem.lib.R;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Created by hereshem on 2/18/18.
 */

public class MultiLayoutAdapter extends RecyclerView.Adapter<MyViewHolder> {
    Activity activity;
    List<Object> items;
    boolean loadMore = false;
    MultiLayoutHolder holder;
    MyRecyclerView.OnItemClickListener lis;


    public MultiLayoutAdapter(Activity activity, List<Object> items, MultiLayoutHolder holder){
        this.activity = activity;
        this.items = items;
        this.holder = holder;
    }
    public static MultiLayoutAdapter init(Activity activity, List<Object> items, MultiLayoutHolder holder){
        return new MultiLayoutAdapter(activity, items, holder);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType != -1) {
            try {
                Constructor constructor = holder.getViewHolderFromIndex(viewType).getConstructor(View.class);
                return (MyViewHolder) constructor.newInstance(LayoutInflater.from(activity)
                        .inflate(holder.getLayoutFromIndex(viewType), parent, false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new LoadMoreViewHolder(LayoutInflater.from(activity).inflate(R.layout.more_loading, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if(!(holder instanceof LoadMoreViewHolder)){
            holder.bindView(items.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lis != null)
                        lis.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size() == 0 ? items.size() : (loadMore?items.size()+1:items.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (loadMore && items.size() == position && position!=0)
            return -1;
        else {
            return holder.getIndexFromType(items.get(position).getClass());
        }
    }

    private static class LoadMoreViewHolder extends MyViewHolder{
        private LoadMoreViewHolder(View itemView) {
            super(itemView);
        }
        @Override
        public void bindView(Object item) {}
    }

    public void enableLoadMore(){
        loadMore = true;
    }

    public void hideLoadMore(){
        loadMore = false;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(MyRecyclerView.OnItemClickListener lis){
        this.lis = lis;
    }
}
