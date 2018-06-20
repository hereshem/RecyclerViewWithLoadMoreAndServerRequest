package com.hereshem.lib.server;

import android.content.Context;

public class Config {
    Context context;
    String url = "http://www.hemshrestha.com.np";
    Method method = Method.POST;
    MapPair headers;
    boolean debug = true;

    public Config(Context context){
        this.context = context;
    }

    public Config setHeaders(MapPair headers){
        this.headers = headers;
        return this;
    }
    public Config setMethod(Method method){
        this.method = method;
        return this;
    }
    public Config setUrl(String url){
        this.url = url;
        return this;
    }
    public Config setDebug(boolean debug){
        this.debug = debug;
        return this;
    }
}
