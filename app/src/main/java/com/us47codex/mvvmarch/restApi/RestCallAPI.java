package com.us47codex.mvvmarch.restApi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import com.us47codex.mvvmarch.helper.AppLog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
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
}