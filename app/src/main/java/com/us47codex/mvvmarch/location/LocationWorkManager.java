package com.us47codex.mvvmarch.location;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class LocationWorkManager extends Worker {

    private static String TAG = LocationWorkManager.class.getSimpleName();
    private Context context;


    public LocationWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG, "doWork: ");
        context.stopService(new Intent(context,LocationManagerServices.class));
        context.startService(new Intent(getApplicationContext(), LocationManagerServices.class));
        //SunTecApplication.getInstance().getPreferenceManager().getBooleanValue()
        return Result.success();

    }
}
