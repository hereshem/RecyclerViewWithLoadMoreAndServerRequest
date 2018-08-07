package com.hereshem.lib.server;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MapPair {
    private LinkedHashMap<String, String> pair;
    public MapPair(){
        pair = new LinkedHashMap<>();
    }
    public MapPair add(String key, String value){
        pair.put(key, value);
        return this;
    }

    public LinkedHashMap<String, String> getMap() {
        return pair;
    }

    public boolean containsKey(String key){
        return pair.containsKey(key);
    }

    public String get(String key){
        return pair.get(key);
    }
}
