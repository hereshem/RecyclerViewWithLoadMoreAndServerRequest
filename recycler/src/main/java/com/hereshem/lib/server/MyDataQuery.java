package com.hereshem.lib.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hereshem on 9/2/15.
 */
public abstract class MyDataQuery {
    Context context;
    HashMap<String, String> params;
    String method = "POST";
    String url = "https://hereshem.github.io";
    String table = "tables";

    int code = 0;

    public abstract void onSuccess(String table, String result);
    public void onError(String table, int code, String message){}
    public String onDbQuery(String table, HashMap<String, String> params){return "[]";}
    public void onDbSave(String table, String response){}


    public MyDataQuery (Context context){
        this.context = context;
    }

    public MyDataQuery (Context context, HashMap<String, String> params){
        this.context = context;
        this.params = params;
    }

    public MyDataQuery setMethod(String method){
        this.method = method;
        return this;
    }

    public MyDataQuery setTable(String table){
        this.table = table;
        return this;
    }

    public MyDataQuery setUrl(String url){
        this.url = url;
        return this;
    }

    public static MyDataQuery getInstance(Context context, HashMap<String, String> params){
        return new MyDataQuery(context, params){
            @Override
            public void onSuccess(String table, String result) {

            }
        };
    }

    public void execute(){
        String res = onDbQuery(table, params);
        if(res != null && res.length() > 2)
            onSuccess(table, res);
        if(!isNetworkConnected(context)){
            onError(table, 450, "No connection");
            return;
        }
        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... voids) {
                StringBuffer response = new StringBuffer();
                String message = "";
                try {
                    URL u = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection)u.openConnection();
                    if(method.equalsIgnoreCase("post")) {
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    }
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(getUrlEncodeData(params));
                    writer.flush();
                    writer.close();
                    InputStream inputStream;
                    code = conn.getResponseCode();
                    if(code == HttpURLConnection.HTTP_OK){
                        inputStream = conn.getInputStream();
                    }else{
                        inputStream = conn.getErrorStream();
                    }
                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(inputStream));
                    message = conn.getResponseMessage();
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    reader.close();
                }
                catch(Exception e){e.printStackTrace();return message;}
                return response.toString();

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.d("MyDataQuery", "Url = " + url + " Params = " + getUrlEncodeData(params) + " Response = " + result);
                if(code == 200){
                    onDbSave(table, result);
                }
                sendResponse(table, result);
            }
        }.execute();
    }

    private void sendResponse(String table, String result) {
            if(code == 200) {
                onSuccess(table, result);
            }
            else{
                onError(table, code, result);
            }

    }

    private String getUrlEncodeData(HashMap<String, String> params) {
        if(params == null){
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            try {
                if (first)
                    first = false;
                else
                    result.append("&");
                result.append(entry.getKey());
                result.append("=");
                result.append(entry.getValue());
            }
            catch(Exception e){e.printStackTrace();}
        }
        return result.toString();
    }

    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni!=null;
    }

    public static HashMap<String, String> getRequestParameters(String action, int skip) {
        HashMap<String, String> params = new HashMap();
        params.put("action", action);
        params.put("start", skip + "");
        params.put("limit", "20");
        return params;
    }
}
