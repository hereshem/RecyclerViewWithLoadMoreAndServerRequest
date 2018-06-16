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
    List<MultiLayoutHolder> holders;
    MyRecyclerView.OnItemClickListener lis;


    public MultiLayoutAdapter(Activity activity, List<Object> items, List<MultiLayoutHolder> holders){
        this.activity = activity;
        this.items = items;
        this.holders = holders;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType != -1) {
            try {
                Constructor constructor = holders.get(viewType).getViewHolder().getConstructor(View.class);
                return (MyViewHolder) constructor.newInstance(LayoutInflater.from(activity)
                        .inflate(holders.get(viewType).getLayout(), parent, false));
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
            for (int i = 0; i < holders.size(); i++) {
                try {
                    if (holders.get(i).getDataType() == items.get(position).getClass())
                        return i;
                }catch (Exception e){e.printStackTrace();}
            }
        }
        return -1;
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
