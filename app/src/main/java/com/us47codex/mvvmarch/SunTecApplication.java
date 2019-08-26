package com.us47codex.mvvmarch;

import android.app.Application;

import com.us47codex.mvvmarch.helper.AppLog;
import com.us47codex.mvvmarch.roomDatabase.SunTecDatabase;

import java.util.HashMap;

import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_AUTHENTICATION_TOKEN;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_ID;

public class SunTecApplication extends Application {
    private static final String TAG = SunTecApplication.class.getSimpleName();

    private static SunTecApplication mInstance;
    private SunTecPreferenceManager pref;
    private SunTecDatabase sunTecDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized SunTecApplication getInstance(){
        return mInstance;
    }

    public SunTecPreferenceManager getPreferenceManager() {
        if (pref == null) {
            pref = new SunTecPreferenceManager(this);
        }
        return pref;
    }

    public SunTecDatabase getDatabase() {
        if (sunTecDatabase == null) {
            sunTecDatabase = SunTecDatabase.getInstance(this);
        }
        return sunTecDatabase;
    }
}
