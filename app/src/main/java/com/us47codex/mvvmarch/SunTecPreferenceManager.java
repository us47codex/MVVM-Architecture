package com.us47codex.mvvmarch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SunTecPreferenceManager {

    // Shared Preferences
    private final SharedPreferences pref;

    // Editor for Shared preferences
    private final SharedPreferences.Editor editor;

    // SharedPref file name
    private static final String PREF_NAME = "Sun_Tec_Pref";

    public static final String AUTHENTICATION_TOKEN = "authentication_token";


    // Constructor
    @SuppressLint("CommitPrefEdits")
    SunTecPreferenceManager(Context context) {
        // Context
        // Shared pref mode
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void addStringValue(String keyName, String value) {
        editor.putString(keyName, value);
        editor.commit();
    }

    public String getStringValue(String keyName, String defaultValue) {
        return pref.getString(keyName, defaultValue);
    }

    public void addBooleanValue(String keyName, boolean value) {
        editor.putBoolean(keyName, value);
        editor.commit();
    }

    public boolean getBooleanValue(String keyName, boolean defaultValue) {
        return pref.getBoolean(keyName, defaultValue);
    }

    public void addIntegerValue(String keyName, int value) {
        editor.putInt(keyName, value);
        editor.commit();
    }

    public int getIntegerValue(String keyName, int defaultValue) {
        return pref.getInt(keyName, defaultValue);
    }

    public void addLongValue(String keyName, long value) {
        editor.putLong(keyName, value);
        editor.commit();
    }

    public long getLongValue(String keyName, long defaultValue) {
        return pref.getLong(keyName, defaultValue);
    }

    /*public void addFloatValue(String keyName, float value) {
        editor.putFloat(keyName, value);
        editor.commit();
    }

    public float getFloatValue(String keyName, float defaultValue) {
        return pref.getFloat(keyName, defaultValue);
    }*/

    public void clear() {
        editor.clear();
        editor.commit();
    }

}
