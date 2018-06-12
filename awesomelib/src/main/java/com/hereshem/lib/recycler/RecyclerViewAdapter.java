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

public abstract class RecyclerViewAdapter<LI, VH> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    List<LI> items;
    int layout;
    boolean loadMore = false;
    Class<VH> holderClass;
    MyRecyclerView.OnItemClickListener lis;

    public RecyclerViewAdapter(Activity activity, List<LI> items, Class<VH> holderClass, int layout){
        this.activity = activity;
        this.items = items;
        this.layout = layout;
        this.holderClass = holderClass;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1) {
            try {
                Constructor constructor = holderClass.getConstructor(View.class);
                return (RecyclerView.ViewHolder) constructor.newInstance(LayoutInflater.from(activity).inflate(layout, parent, false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new LoadMoreViewHolder(LayoutInflater.from(activity).inflate(R.layout.more_loading, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(!(holder instanceof LoadMoreViewHolder)){
            onBinded((VH)holder, position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lis != null)
                        lis.onItemClick(position);
                }
            });
        }
    }

    public abstract void onBinded(VH holder, int position);

    @Override
    public int getItemCount() {
        return items.size() == 0 ? items.size() : (loadMore?items.size()+1:items.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (loadMore && items.size() == position && position!=0)
            return 0;
        else return 1;
    }

    private static class LoadMoreViewHolder extends RecyclerView.ViewHolder{
        private LoadMoreViewHolder(View itemView) {
            super(itemView);
        }
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
