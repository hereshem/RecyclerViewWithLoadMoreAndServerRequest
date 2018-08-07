package com.hereshem.lib.recycler;

import java.util.LinkedHashMap;

public class MultiLayoutHolder {
    private class Holder {
        public Class viewHolder, dataType;
        public int layout, index;
        public Holder(Class dataType, Class viewHolder, int layout, int index) {
            this.viewHolder = viewHolder;
            this.dataType = dataType;
            this.layout = layout;
            this.index = index;
        }
    }
    private LinkedHashMap<Class, Holder> maps;

    public MultiLayoutHolder(){
        maps = new LinkedHashMap<>();
    }
    public static MultiLayoutHolder init(){
        return new MultiLayoutHolder();
    }

    public MultiLayoutHolder add(Class dataType, Class viewHolder, int layout) {
        try {
            viewHolder.getMethod("bindView", dataType);
            if(MyViewHolder.class.isAssignableFrom(viewHolder))
                maps.put(dataType, new Holder(dataType, viewHolder, layout, maps.size()));
            else
                throw new RuntimeException(
                        viewHolder.getSimpleName() + " must inherit MyViewHolder<"+dataType.getSimpleName()+"> class." +
                                "\n public static class " + viewHolder.getSimpleName() + " extends MyViewHolder<"+dataType.getSimpleName()+">{...}"
                );
        }catch (Exception e){
            throw new RuntimeException(
                    viewHolder.getSimpleName() + " must inherit MyViewHolder<"+dataType.getSimpleName()+"> class." +
                            "\n public static class " + viewHolder.getSimpleName() + " extends MyViewHolder<"+dataType.getSimpleName()+">{...}"
            );
        }
        return this;
    }

    public Class getViewHolderFromIndex(int index) {
        return ((Holder)maps.values().toArray()[index]).viewHolder;
    }

    public int getLayoutFromIndex(int index) {
        return ((Holder)maps.values().toArray()[index]).layout;
    }

    public int getIndexFromType(Class dataType) {
        return maps.get(dataType).index;
    }

}
