package com.us47codex.mvvmarch.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.us47codex.mvvmarch.constant.Constants;

import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;

public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        try{
            LocationManager service = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            boolean enabled = false;
            if (service != null) {
                enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }

            // Check if enabled and if not send user to the GPS settings
            if (!enabled) {
                if (Objects.requireNonNull(intent.getAction()).matches("android.location.PROVIDERS_CHANGED")) {
                    /*Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
                            Toast.LENGTH_SHORT).show();*/
                    Log.e("GpsLocationReceiver", "onReceive: ");
                    Intent pushNotification = new Intent(Constants.SYSTEM_LOCATION_MANAGER_CHANGE);
                    pushNotification.putExtra("type", Constants.PUSH_TYPE_SYSTEM_LOCATION_CHANGE);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }





    }
}
