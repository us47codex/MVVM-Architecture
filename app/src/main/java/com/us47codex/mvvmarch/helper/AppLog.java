package com.us47codex.mvvmarch.helper;

import android.util.Log;

import com.us47codex.mvvmarch.BuildConfig;


public class AppLog {

    public static void logw(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message );
        }
    }

    public static void logd(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void loge(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void logi(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void logv(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message);
        }
    }
}
