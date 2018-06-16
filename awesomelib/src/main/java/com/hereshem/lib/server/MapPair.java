package com.hereshem.lib.server;

import java.util.HashMap;

public class MapPair {
    private HashMap<String, String> pair;
    public MapPair(){
        pair = new HashMap<>();
    }
    public MapPair add(String key, String value){
        pair.put(key, value);
        return this;
    }

    public HashMap<String, String> getMap() {
        return pair;
    }

    public boolean containsKey(String key){
        return pair.containsKey(key);
    }

    public String get(String key){
        return pair.get(key);
    }
}
