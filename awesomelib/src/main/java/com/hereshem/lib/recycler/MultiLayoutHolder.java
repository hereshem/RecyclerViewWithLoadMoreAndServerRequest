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
                if(maps.containsKey(dataType))
                    throw new RuntimeException("Duplicate dataType \"" + dataType.getSimpleName() + "\",\nThe dataType must be unique in order to show the correct layout");
                else
                    maps.put(dataType, new Holder(dataType, viewHolder, layout, maps.size()));
            else
                throw new NoSuchMethodException();
        }catch (NoSuchMethodException e){
            throw new RuntimeException(
                    viewHolder.getSimpleName() + " must inherit MyViewHolder<"+dataType.getSimpleName()+"> class." +
                            "\nexample - public static class " + viewHolder.getSimpleName() + " extends MyViewHolder<"+dataType.getSimpleName()+">{\n...\n}"
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
        if(maps.containsKey(dataType))
            return maps.get(dataType).index;
        else
            return -2;
    }

}
