package com.us47codex.mvvmarch.restApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.us47codex.mvvmarch.BuildConfig;
import com.us47codex.mvvmarch.SunTecApplication;
import com.us47codex.mvvmarch.SunTecPreferenceManager;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.constant.EndPoints;
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

import static com.us47codex.mvvmarch.constant.Constants.API_CALL_TIMEOUT;

public class RestApiClient {
    private static Retrofit retrofit = null;

    private static final String TAG = RestApiClient.class.getSimpleName();

    public static final RestApiInterface apiService = RestApiClient.getClient().create(RestApiInterface.class);

    private static Retrofit getClient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (AppLog.DO_API_LOG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(API_CALL_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(API_CALL_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(API_CALL_TIMEOUT, TimeUnit.SECONDS)
                .build();


        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(EndPoints.BASE_URL)
                    .client(client)
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
                    .readTimeout(API_CALL_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(API_CALL_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(API_CALL_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request request = chain.request();
                        Response response = chain.proceed(request);

                        AppLog.error(TAG, String.valueOf(response.code()));
                        if (response.code() == Constants.UNAUTHORIZED) {
                            ErrorMessageHandlerModel message = response.body() != null ? new Gson().fromJson(response.body().charStream(), ErrorMessageHandlerModel.class) : new ErrorMessageHandlerModel();
                            message.code = response.code();
                            PublishSubjectEvent.getInstance().authErrorCodeRelay.accept(message);
                        }

                        return response;
                    })
                    .addNetworkInterceptor(
                            chain -> {
                                Request request;
                                Request original = chain.request();
                                Request.Builder requestBuilder = original.newBuilder()
                                        .addHeader("Authorization", "Bearer " + SunTecApplication.getInstance().getPreferenceManager().getStringValue(SunTecPreferenceManager.PREF_AUTHENTICATION_TOKEN, ""));
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

                        AppLog.error(TAG, String.valueOf(response.code()));
                        if (response.code() == Constants.UNAUTHORIZED) {
                            ErrorMessageHandlerModel message = response.body() != null ? new Gson().fromJson(response.body().charStream(), ErrorMessageHandlerModel.class) : new ErrorMessageHandlerModel();
                            message.code = response.code();
                            PublishSubjectEvent.getInstance().authErrorCodeRelay.accept(message);
                        }
                        return response;
                    })
                    .addNetworkInterceptor(
                            chain -> {
                                Request request;
                                Request original = chain.request();
                                Request.Builder requestBuilder = original.newBuilder()
                                        .addHeader("Authorization", "Bearer " + SunTecApplication.getInstance().getPreferenceManager().getStringValue(SunTecPreferenceManager.PREF_AUTHENTICATION_TOKEN, ""));
                                request = requestBuilder.build();
                                return chain.proceed(request);
                            })
                    .build();
        }

    }
}
