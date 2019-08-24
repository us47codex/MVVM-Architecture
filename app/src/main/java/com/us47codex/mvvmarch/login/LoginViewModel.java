package com.us47codex.mvvmarch.login;

import android.app.Application;

import androidx.annotation.NonNull;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.us47codex.mvvmarch.base.BaseViewModel;
import com.us47codex.mvvmarch.enums.ApiCallStatus;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends BaseViewModel {

    private final String TAG = LoginViewModel.class.getSimpleName();

    public static final String USER_LOGIN = "user_login";

    protected LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void callToApi(Object params, String apiTag, boolean shouldShowLoader) {
        if (shouldShowLoader)
            getStatusBehaviorRelay().accept(ApiCallStatus.LOADING);

            getCompositeDisposable().add(
                    ReactiveNetwork.checkInternetConnectivity()
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if(aBoolean){
                                try{
                                    switch (apiTag) {
                                        case USER_LOGIN:

                                            break;
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    })
                    .doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    })
                    .subscribe());
    }



}
