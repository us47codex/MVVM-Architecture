package com.us47codex.mvvmarch.helper;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.DefaultErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.WalledGardenInternetObservingStrategy;
import com.jakewharton.rxrelay2.PublishRelay;
import com.us47codex.mvvmarch.constant.Endpoint;

import java.net.HttpURLConnection;

import io.reactivex.schedulers.Schedulers;

public class PublishSubjectEvent {
    private static final PublishSubjectEvent ourInstance = new PublishSubjectEvent();

    public static PublishSubjectEvent getInstance() {
        return ourInstance;
    }

    public final PublishRelay<ErrorMessageHandlerModel> authErrorCodeRelay = PublishRelay.create();
    public final PublishRelay<String> globalAppRefreshRelay = PublishRelay.create();
    public final PublishRelay<Long> complainDeleteRelay = PublishRelay.create();
    public final PublishRelay<String> departmentNotAvailableRelay = PublishRelay.create();

    public final PublishRelay<String> saveTokenRelay = PublishRelay.create();
    public final PublishRelay<Long> socketLogOutRelay = PublishRelay.create();
    public final PublishRelay<String> socketTopicUnSubscribeRelay = PublishRelay.create();
    public final PublishRelay<Boolean> internetStatusRelay = PublishRelay.create();
    private boolean internetStatus = false;

    private PublishSubjectEvent() {
        int initialInterval = 0;
        int interval = 2000;
        String host = Endpoint.NETWORK_PING_URL;
        int port = 80;
        int timeout = 5000;
        int httpResponse = HttpURLConnection.HTTP_NO_CONTENT;
        ErrorHandler errorHandler = new DefaultErrorHandler();
        InternetObservingStrategy strategy = new WalledGardenInternetObservingStrategy();

        InternetObservingSettings settings = InternetObservingSettings.builder()
                .initialInterval(initialInterval)
                .interval(interval)
                .host(host)
                .port(port)
                .timeout(timeout)
                .httpResponse(httpResponse)
                .errorHandler(errorHandler)
                .strategy(strategy)
                .build();

        ReactiveNetwork.observeInternetConnectivity(settings)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .onErrorReturn(throwable -> {
                    throwable.printStackTrace();
                    return false;
                })
                .doOnError(Throwable::printStackTrace)
                .doOnNext(aBoolean -> {
                    internetStatusRelay.accept(aBoolean);
                    setInternetStatus(aBoolean);
                })
                .subscribe();
    }

    public boolean isInternetStatus() {
        return internetStatus;
    }

    private void setInternetStatus(boolean internetStatus) {
        this.internetStatus = internetStatus;
    }
}
