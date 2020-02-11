package com.us47codex.mvvmarch.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Astics INC-08 on 05-Oct-16.
 */
public class InternetConnection {
    /**
     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
     */
    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            // Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

            // connected to the mobile provider's data plan
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
               /* String command = "ping -c 1 google.com";
                try {
                    return (Runtime.getRuntime().exec (command).waitFor() == 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                return true;
            } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    public static boolean checkConnectionHard(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            // Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

            // connected to the mobile provider's data plan
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                String command = "ping -c 1 google.com";
                try {
                    return (Runtime.getRuntime().exec(command).waitFor() == 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    /**
     * Check here soft internet connection using rxjava
     */
    public static Observable<Boolean> checkSoftInternetConnectionStatusRx(Context context) {
        return Observable
                .create((ObservableOnSubscribe<Boolean>) emitter -> {
                    if (!emitter.isDisposed()) {

                        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetworkInfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;

                        if (activeNetworkInfo != null) { // connected to the internet
                            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                                emitter.onNext(true);
                                emitter.onComplete();
                            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                                emitter.onNext(true);
                                emitter.onComplete();
                            }
                        }
                        emitter.onNext(false);
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io());
    }

    /**
     * Check here soft internet connection using rxjava
     */
    public static Single<Boolean> checkSoftInternetConnectionStatusRxSingle(Context context) {
        return Single.just(context)
                .subscribeOn(Schedulers.io())
                .map(context1 -> {
                    final ConnectivityManager connMgr = (ConnectivityManager) context1.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetworkInfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;

                    if (activeNetworkInfo != null) { // connected to the internet
                        if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            return true;
                        } else
                            return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
                    }
                    return false;
                });
    }

    /**
     * Check here hard internet connection using rxjava
     */
    public static Observable<Boolean> checkHardInternetConnectionStatusRx(Context context) {
        return Observable
                .create((ObservableOnSubscribe<Boolean>) emitter -> {
                    if (!emitter.isDisposed()) {
                        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetworkInfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;

                        if (activeNetworkInfo != null) { // connected to the internet
                            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                                // connected to wifi
                                String command = "ping -c 1 google.com";
                                try {
                                    boolean b = (Runtime.getRuntime().exec(command).waitFor() == 0);
                                    emitter.onNext(b);
                                    emitter.onComplete();
                                } catch (InterruptedException | IOException e) {
                                    e.printStackTrace();
                                    emitter.onError(e);
                                    emitter.onComplete();
                                }
                                emitter.onNext(true);
                                emitter.onComplete();
                            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                                emitter.onNext(true);
                                emitter.onComplete();
                            }
                        }
                        emitter.onNext(false);
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io());
    }
}
