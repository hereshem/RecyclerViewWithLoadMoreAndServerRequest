package com.hereshem.lib.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.hereshem.lib.server.ServerRequest;


/**
 * Created by hereshem on 2/22/18.
 */

public class MyRecyclerView extends RecyclerView {
    boolean dataLoaded = true;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }
    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void loadComplete(){
        dataLoaded = true;
        getAdapter().notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(final OnLoadMoreListener lis){
        if(getAdapter() instanceof RecyclerViewAdapter){
            ((RecyclerViewAdapter)getAdapter()).enableLoadMore();
        }
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleThreshold = 5; // number of remaining items to be displayed
                int lastVisibleItem, totalItemCount;
                totalItemCount = recyclerView.getLayoutManager().getItemCount();
                lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (ServerRequest.isNetworkConnected(getContext()) && dataLoaded && getAdapter().getItemCount()>0 && (totalItemCount <= (lastVisibleItem + visibleThreshold))) {
                    dataLoaded = false;
                    new android.os.Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            lis.onLoadMore();
                        }
                    });
                }
            }
        });
    }

    public void setOnItemClickListener(final OnItemClickListener lis){
        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        this.addOnItemTouchListener(new OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && lis != null && gestureDetector.onTouchEvent(e)){
                    lis.onItemClick(rv.getChildLayoutPosition(childView));
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    public void hideLoadMore(){
        if(getAdapter() instanceof RecyclerViewAdapter){
            ((RecyclerViewAdapter)getAdapter()).hideLoadMore();
        }
    }

}
