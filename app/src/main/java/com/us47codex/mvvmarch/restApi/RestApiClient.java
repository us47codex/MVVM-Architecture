package com.us47codex.mvvmarch.restApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.us47codex.mvvmarch.BuildConfig;
import com.us47codex.mvvmarch.MyApplication;
import com.us47codex.mvvmarch.MyPreferenceManager;
import com.us47codex.mvvmarch.constant.Constant;
import com.us47codex.mvvmarch.constant.Endpoint;
import com.us47codex.mvvmarch.helper.AppLog;
import com.us47codex.mvvmarch.helper.ErrorMessageHandlerModel;
import com.us47codex.mvvmarch.helper.PublishSubjectEvent;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiClient {
    private static Retrofit retrofit = null;

    private static final String TAG = RestApiClient.class.getSimpleName();

    public static final RestApiInterface apiService = RestApiClient.getClient().create(RestApiInterface.class);

    private static Retrofit getClient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Endpoint.BASE_URL)
                    .client(getHeader())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }

    private static OkHttpClient getHeader() {
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            return new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request request = chain.request();
                        Response response = chain.proceed(request);

                        AppLog.loge(TAG, String.valueOf(response.code()));
                        if (response.code() == Constant.UNAUTHORIZED) {

                            Gson gson = new Gson();
                            ErrorMessageHandlerModel message = null;

                            if (response.body() != null) {
                                message = gson.fromJson(response.body().charStream(), ErrorMessageHandlerModel.class);
                                message.code = response.code();
                            }

                            if (message == null) {
                                message = new ErrorMessageHandlerModel();
                                message.code = response.code();
                            }

                            PublishSubjectEvent.getInstance().authErrorCodeRelay.accept(message);
                        }

                        return response;
                    })
                    .addNetworkInterceptor(
                            chain -> {
                                Request request;
                                Request original = chain.request();
                                Request.Builder requestBuilder = original.newBuilder()
                                        .addHeader("Authorization", "Bearer "+ MyApplication.getInstance().getPrefrenceManager().getStringValue(MyPreferenceManager.AUTHENTICATION_TOKEN, ""));
                                request = requestBuilder.build();
                                return chain.proceed(request);
                            })
                    .build();
        } else {
            return new OkHttpClient.Builder()
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request request = chain.request();
                        Response response = chain.proceed(request);

                        AppLog.loge(TAG, String.valueOf(response.code()));
                        if (response.code() == Constant.UNAUTHORIZED) {

                            Gson gson = new Gson();
                            ErrorMessageHandlerModel message = null;

                            if (response.body() != null) {
                                message = gson.fromJson(response.body().charStream(), ErrorMessageHandlerModel.class);
                                message.code = response.code();
                            }

                            if (message == null) {
                                message = new ErrorMessageHandlerModel();
                                message.code = response.code();
                            }

                            PublishSubjectEvent.getInstance().authErrorCodeRelay.accept(message);
                        }

                        return response;
                    })
                    .addNetworkInterceptor(
                            chain -> {
                                Request request;
                                Request original = chain.request();
                                Request.Builder requestBuilder = original.newBuilder()
                                        .addHeader("Authorization", "Bearer "+MyApplication.getInstance().getPrefrenceManager().getStringValue(MyPreferenceManager.AUTHENTICATION_TOKEN, ""));
                                request = requestBuilder.build();
                                return chain.proceed(request);
                            })
                    .build();
        }

    }
}
