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

    public static final String PREF_AUTHENTICATION_TOKEN = "authentication_token";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_FIRST_NAME = "user_first_name";
    public static final String PREF_USER_MIDDLE_NAME = "user_middle_name";
    public static final String PREF_USER_LAST_NAME = "user_last_name";
    public static final String PREF_USER_NAME = "user_name";
    public static final String PREF_USER_EMAIL = "user_email";
    public static final String PREF_USER_MNO = "user_mno";
    public static final String PREF_USER_DEPARTMENT = "user_department";
    public static final String PREF_USER_PROFILE = "user_profile";
    public static final String PREF_LOCATION_ID = "add_location_id";


    // Constructor
    @SuppressLint("CommitPrefEdits")
    SunTecPreferenceManager(Context context) {
        // Context
        // Shared pref mode
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void putStringValue(String keyName, String value) {
        editor.putString(keyName, value);
        editor.commit();
    }

    public String getStringValue(String keyName, String defaultValue) {
        return pref.getString(keyName, defaultValue);
    }

    public void putBooleanValue(String keyName, boolean value) {
        editor.putBoolean(keyName, value);
        editor.commit();
    }

    public boolean getBooleanValue(String keyName, boolean defaultValue) {
        return pref.getBoolean(keyName, defaultValue);
    }

    public void putIntValue(String keyName, int value) {
        editor.putInt(keyName, value);
        editor.commit();
    }

    public int getIntValue(String keyName, int defaultValue) {
        return pref.getInt(keyName, defaultValue);
    }

    public void putLongValue(String keyName, long value) {
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
