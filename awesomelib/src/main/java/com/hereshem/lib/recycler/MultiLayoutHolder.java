package com.hereshem.lib.recycler;

public class MultiLayoutHolder {
    private Class viewHolder, dataType;
    private int layout;
    public MultiLayoutHolder(Class dataType, Class viewHolder, int layout) {
        this.viewHolder = viewHolder;
        this.dataType = dataType;
        this.layout = layout;
    }

    public Class getDataType() {
        return dataType;
    }

    public Class getViewHolder() {
        return viewHolder;
    }

    public int getLayout() {
        return layout;
    }
}
