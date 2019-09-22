package com.us47codex.mvvmarch.restApi;

import android.content.Context;

import com.us47codex.mvvmarch.helper.AppLog;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;


public class RestCallAPI {
    private static String TAG = RestCallAPI.class.getSimpleName();
    private Context context;

    public static Disposable restCallAPI(String url,
                                         Map<String, String> headers,
                                         Object params,
                                         DisposableSingleObserver<Response<ResponseBody>> responseHandler) {

        HashMap<String, String> encryptedParams = new HashMap<>();
        if (params instanceof Map) {
            encryptedParams = (HashMap<String, String>) params;
        }

        return RestApiClient.apiService
                .callPostApi(url, headers, encryptedParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    AppLog.error(TAG, throwable);
                    responseHandler.onError(throwable);
                    return null;
                })
                .subscribeWith(new DisposableSingleObserver<Response<ResponseBody>>() {
                    @Override
                    public void onSuccess(Response<ResponseBody> responseBodyResponse) {
                        responseHandler.onSuccess(responseBodyResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseHandler.onError(e);
                    }
                });
    }

    public static Disposable restMultipartCallAPI(String url,
                                                  Map<String, String> headers,
                                                  Object params,
                                                  DisposableSingleObserver<Response<ResponseBody>> responseHandler) {

        HashMap<String, RequestBody> encryptedParams = new HashMap<>();
        if (params instanceof Map) {
            encryptedParams = (HashMap<String, RequestBody>) params;
        }

        return RestApiClient.apiService
                .callMultipartApi(url, headers, encryptedParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    AppLog.error(TAG, throwable);
                    responseHandler.onError(throwable);
                    return null;
                })
                .subscribeWith(new DisposableSingleObserver<Response<ResponseBody>>() {
                    @Override
                    public void onSuccess(Response<ResponseBody> responseBodyResponse) {
                        responseHandler.onSuccess(responseBodyResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseHandler.onError(e);
                    }
                });
    }

    public static Disposable restMultipartCallAPI1(String url,
                                                   Map<String, String> headers,
                                                   Object params,
                                                   HashMap<String, MultipartBody.Part> params1,
                                                   DisposableSingleObserver<Response<ResponseBody>> responseHandler) {

        HashMap<String, RequestBody> encryptedParams = new HashMap<>();
        if (params instanceof Map) {
            encryptedParams = (HashMap<String, RequestBody>) params;
        }

        return RestApiClient.apiService
                .callMultipartApi1(url, headers, encryptedParams, params1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    AppLog.error(TAG, throwable);
                    responseHandler.onError(throwable);
                    return null;
                })
                .subscribeWith(new DisposableSingleObserver<Response<ResponseBody>>() {
                    @Override
                    public void onSuccess(Response<ResponseBody> responseBodyResponse) {
                        responseHandler.onSuccess(responseBodyResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseHandler.onError(e);
                    }
                });
    }

    public static Disposable restGetCallAPI(String url,
                                            Map<String, String> headers,
                                            Object params,
                                            DisposableSingleObserver<Response<ResponseBody>> responseHandler) {

        HashMap<String, String> encryptedParams = new HashMap<>();
        if (params instanceof Map) {
            encryptedParams = (HashMap<String, String>) params;
        }

        return RestApiClient.apiService
                .callGetApi(url, headers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    AppLog.error(TAG, throwable);
                    responseHandler.onError(throwable);
                    return null;
                })
                .subscribeWith(new DisposableSingleObserver<Response<ResponseBody>>() {
                    @Override
                    public void onSuccess(Response<ResponseBody> responseBodyResponse) {
                        responseHandler.onSuccess(responseBodyResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        responseHandler.onError(e);
                    }
                });
    }
}