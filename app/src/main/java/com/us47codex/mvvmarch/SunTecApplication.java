package com.us47codex.mvvmarch;

import android.app.Application;

public class SunTecApplication extends Application {
    private static final String TAG = SunTecApplication.class.getSimpleName();

    private static SunTecApplication mInstance;
    private SunTecPreferenceManager pref;

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

}
