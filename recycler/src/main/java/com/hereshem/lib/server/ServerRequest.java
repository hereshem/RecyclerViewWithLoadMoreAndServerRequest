package com.hereshem.lib.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hereshem.lib.BuildConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class ServerRequest {

    Context context;
    public ServerRequest(Context context){
        this.context = context;
    }

	public static boolean isNetworkConnected(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni!=null;
	}

    private void log(String str){
        if(BuildConfig.DEBUG)
            Log.i(this.getClass().getName(),str);
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
                log("server, " + entry.getKey() + " = " + entry.getValue());
            }
            catch(Exception e){e.printStackTrace();}
        }
        return result.toString();
    }

    public String httpGetData(String url){
        return httpGetData(url, null);
    }
    public String httpGetData(String url, HashMap<String, String> params){
        log("ServerRequest - GET :: url = " + url + "?" + getUrlEncodeData(params));
        StringBuffer response = new StringBuffer();
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
//            conn.setRequestProperty("X-APP-ID", Constants.APP_ID);
//            conn.setRequestProperty("X-AGENT-TOKEN", Constants.ANDROID_KEY);
//            conn.setRequestProperty("X-DEVICE-ID", getDeviceId(context));
//            conn.setRequestProperty("X-APP-VERSION", getVersionName(context));
//            if(context != null) {
//                conn.setRequestProperty("X-ACCESS-TOKEN", new Preferences(context).getPreferences("u_token"));
//            }
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(getUrlEncodeData(getSha1Hex(params)));
            writer.flush();
            writer.close();

            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
        }
        catch(Exception e){e.printStackTrace();}
        log("ServerRequest - GET :: url : " + url + getUrlEncodeData(params) + " Response : " + response.toString());
        return response.toString();
    }

    public String httpPostData(String url, HashMap<String, String> params){
        log("ServerRequest - POST :: url = " + url + "?"+ getUrlEncodeData(params));
        StringBuffer response = new StringBuffer();
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)u.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(getUrlEncodeData(getSha1Hex(params)));
            writer.flush();
            writer.close();

            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
        }
        catch(Exception e){e.printStackTrace();}
        log("ServerRequest - POST :: url : " + url + " Params : " + getUrlEncodeData(params) + " Response = " + response.toString());
        return response.toString();
    }

    public HashMap<String, String> getSha1Hex(HashMap<String, String> params){
        //params.put("signature", getSha1Hex(getUrlEncodeData(params)));
//        log(Constants.APP_ID + "_" + Constants.ANDROID_KEY + params.get("action"));
//        params.put("signature", getSha1Hex(Constants.APP_ID + "_" + Constants.ANDROID_KEY + "_" + params.get("action")));
        return params;
    }

    private String getSha1Hex(String string){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(string.getBytes());
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes){
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            log("SHA-1 :: params = " + string + " signature = " + buffer.toString());
            return buffer.toString();
        }
        catch (Exception ignored){
            ignored.printStackTrace();
        }
        return "";
    }
}
