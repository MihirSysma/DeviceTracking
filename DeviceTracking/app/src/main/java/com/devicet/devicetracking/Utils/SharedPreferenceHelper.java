package com.devicet.devicetracking.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceHelper {

    public final static String PREF_LOGIN_KEY = "LOGIN";
    public final static String KEY = "KEY";
    public final static String UID = "UID";
    public final static String STATUS = "STATUS";
    public final static String USERNAME = "USERNAME";


    public static void setPrefTokenKey(Context context, String key, String value){
        SharedPreferences settings = context.getSharedPreferences(PREF_LOGIN_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }
  public static String getPrefTokenKey(Context context, String key, String defValue){
        SharedPreferences settings = context.getSharedPreferences(PREF_LOGIN_KEY, MODE_PRIVATE);
        return settings.getString(key, defValue);
    }
  public static void clearPrefToken(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREF_LOGIN_KEY, MODE_PRIVATE);
        settings.edit().clear().apply();

    }


    public static void setStatus(Context context, String key, String value){
        SharedPreferences settings = context.getSharedPreferences(STATUS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getStatus(Context context, String key, String defValue){
        SharedPreferences settings = context.getSharedPreferences(STATUS, MODE_PRIVATE);
        return settings.getString(key, defValue);
    }
    public static void clearStatus(Context context){
        SharedPreferences settings = context.getSharedPreferences(STATUS, MODE_PRIVATE);
        settings.edit().clear().apply();

    }



    public static void setUsername(Context context, String key, String value){
        SharedPreferences settings = context.getSharedPreferences(USERNAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getUsername(Context context, String key, String defValue){
        SharedPreferences settings = context.getSharedPreferences(USERNAME, MODE_PRIVATE);
        return settings.getString(key, defValue);
    }
    public static void clearUsername(Context context){
        SharedPreferences settings = context.getSharedPreferences(USERNAME, MODE_PRIVATE);
        settings.edit().clear().apply();

    }


    public static void setKey(Context context, String key, String value){
        SharedPreferences settings = context.getSharedPreferences(KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getKey(Context context, String key, String defValue){
        SharedPreferences settings = context.getSharedPreferences(KEY, MODE_PRIVATE);
        return settings.getString(key, defValue);
    }
    public static void clearKey(Context context){
        SharedPreferences settings = context.getSharedPreferences(KEY, MODE_PRIVATE);
        settings.edit().clear().apply();

    }

    public static void setUid(Context context, String key, String value){
        SharedPreferences settings = context.getSharedPreferences(UID, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getUid(Context context, String key, String defValue){
        SharedPreferences settings = context.getSharedPreferences(UID, MODE_PRIVATE);
        return settings.getString(key, defValue);
    }
    public static void clearUid(Context context){
        SharedPreferences settings = context.getSharedPreferences(UID, MODE_PRIVATE);
        settings.edit().clear().apply();

    }


}
