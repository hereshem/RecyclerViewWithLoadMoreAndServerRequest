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
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hereshem on 9/2/15.
 */
public abstract class MyDataQuery {

    public enum Method {GET, POST, PUT, DELETE}

    Context context;
    Method method = Method.POST;
    HashMap<String, String> params, headers;
    String url = "https://hereshem.github.io";
    String identifier = "tables";
    boolean debug = true;
    int code = 0;

    public abstract void onSuccess(String identifier, String result);
    public void onError(String identifier, int code, String message){}
    public void onSoftError(String identifier, String message){}
    public String onDbQuery(String identifier, HashMap<String, String> params){return "[]";}
    public void onDbSave(String identifier, String response){}


    public MyDataQuery (Context context){
        this.context = context;
    }

    public MyDataQuery (Context context, HashMap<String, String> params){
        this.context = context;
        this.params = params;
    }

    public MyDataQuery setParameters(HashMap<String, String> params){
        this.params = params;
        return this;
    }

    public MyDataQuery setHeaders(HashMap<String, String> headers){
        this.headers = headers;
        return this;
    }

    public MyDataQuery setMethod(Method method){
        this.method = method;
        return this;
    }

    public MyDataQuery setIdentifier(String identifier){
        this.identifier = identifier;
        return this;
    }

    public MyDataQuery setUrl(String url){
        this.url = url;
        return this;
    }

    public MyDataQuery setDebug(boolean debug){
        this.debug = debug;
        return this;
    }

    public MyDataQuery execute(){
        String res = onDbQuery(identifier, params);
        if(res != null && res.length() > 2) {
            log("From query :: " + res);
            onSuccess(identifier, res);
        }
        if(!isNetworkConnected(context)){
            if(res != null && res.length() > 2) {
                log("Soft error :: no connection");
                onSoftError(identifier, "No internet connection");
            }else {
                log("Error :: no connection");
                onError(identifier, 450, "No internet connection");
            }
            return this;
        }
        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... voids) {
                StringBuffer response = new StringBuffer();
                String message = "";
                try {
                    URL u = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection)u.openConnection();
                    conn.setRequestMethod(method.name());
                    if(method.equals(Method.POST)) {
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    }
                    log("Debug :: Method = " + method.name() +" Url = " + url);
                    addHeaders(conn);
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
                if(code == 200){
                    onDbSave(identifier, result);
                }
                sendResponse(identifier, result);
            }
        }.execute();
        return this;
    }

    private void log(String text){
        if(debug)
            Log.d("MyDataQuery", text);
    }

    private void addHeaders(HttpURLConnection conn) {
        if(headers != null && !headers.isEmpty()){
            for (String key : headers.keySet()) {
                conn.setRequestProperty(key, headers.get(key));
            }
            log("Headers = " + headers.toString());
        }
    }

    private void sendResponse(String identifier, String result) {
        if(code == 200) {
            log("Success :: " + result);
            onSuccess(identifier, result);
        }
        else{
            log("Error :: Code = " + code + " Result = " + result);
            onError(identifier, code, result);
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
        log("Params :: "+ result.toString());
        return result.toString();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni!=null;
    }

    public static HashMap<String, String> getRequestParameters(String action) {
        HashMap<String, String> params = new HashMap();
        params.put("action", action);
        return params;
    }

    public static HashMap<String, String> getRequestParameters(String action, int skip) {
        HashMap<String, String> params = new HashMap();
        params.put("action", action);
        params.put("start", skip + "");
        params.put("limit", "20");
        return params;
    }

    public static String getSha1Hex(String string){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(string.getBytes());
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes){
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        }
        catch (Exception ignored){
            ignored.printStackTrace();
        }
        return string;
    }
}
