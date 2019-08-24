package com.us47codex.mvvmarch.login;

import android.app.Application;

import androidx.annotation.NonNull;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.us47codex.mvvmarch.base.BaseViewModel;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppLog;
import com.us47codex.mvvmarch.restApi.RestApiClient;

import org.json.JSONObject;

import java.util.Objects;

import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.http.RealResponseBody;
import retrofit2.Response;

import static com.us47codex.mvvmarch.constant.Constant.SERVICE_UNAVAILABLE;

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
                    .doOnSuccess(aBoolean -> {
                        if(aBoolean){
                            try{
                                if (USER_LOGIN.equals(apiTag)) {
                                    callToUserLogin((LoginParamModel) params, apiTag, shouldShowLoader);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    })
                    .doOnError(e -> {
                        getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                        getErrorRelay().accept(Objects.requireNonNull(e.getLocalizedMessage()));
                    })
                    .subscribe());
    }

    private void callToUserLogin(LoginParamModel params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(RestApiClient.apiService
                .userLogin(params)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> {
                    throwable.printStackTrace();
                    return Response.error(SERVICE_UNAVAILABLE, new RealResponseBody("", 0, null));
                })
                .doOnSuccess(response -> {
                    try {
                        JSONObject jsonObject = parseOnSuccess(response, apiTag, shouldShowLoader);
                        if (jsonObject != null) {

                            AppLog.loge(TAG,"User Login :"+ jsonObject.toString());
                            //processIntoData(jsonObject, apiTag, shouldShowLoader);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (shouldShowLoader)
                            getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                        getErrorRelay().accept(Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                })
                .doOnError(error -> parseOnError(error, apiTag, shouldShowLoader)).subscribe()
        );
    }


}
