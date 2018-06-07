package com.hereshem.lib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    Context ctx;
	SharedPreferences preferences;

	public Preferences(Context ctx){
		this.ctx = ctx;
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	
	public boolean containsKey(String key){
		return preferences.contains(key);
	}
	
	public String getPreferences(String key){
		return preferences.getString(key, "");
	}
	
	public boolean setPreferences(String key, String value){
		return preferences.edit().putString(key, value).commit();
	}
	public int getIntPreferences(String key){
		return preferences.getInt(key, 0);
	}
	
	public boolean setIntPreferences(String key, int value){
		return preferences.edit().putInt(key, value).commit();
	}
	
	public long getLongPreferences(String key){
		return preferences.getLong(key, 0l);
	}
	
	public boolean setLongPreferences(String key, long value){
		return preferences.edit().putLong(key, value).commit();
	}
	
	public boolean getBoolPreferences(String key){
		return preferences.getBoolean(key, false);
	}

	public boolean getBoolPreferences(String key, boolean fallback){
		return preferences.getBoolean(key, fallback);
	}
	
	public boolean setBoolPreferences(String key, boolean value){
		return preferences.edit().putBoolean(key, value).commit();
	}
	
	public boolean clearPreferences(){
		return preferences.edit().clear().commit();
	}

}
