package com.us47codex.mvvmarch.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.base.BaseViewModel;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.constant.EndPoints;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppLog;
import com.us47codex.mvvmarch.restApi.RestApiClient;
import com.us47codex.mvvmarch.restApi.RestCallAPI;
import com.us47codex.mvvmarch.roomDatabase.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import retrofit2.Response;

import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_AUTHENTICATION_TOKEN;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_DEPARTMENT;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_EMAIL;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_FIRST_NAME;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_ID;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_LAST_NAME;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_MIDDLE_NAME;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_MNO;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_NAME;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_PROFILE;
import static com.us47codex.mvvmarch.constant.Constants.SERVICE_UNAVAILABLE;

public class LoginViewModel extends BaseViewModel {

    private final String TAG = LoginViewModel.class.getSimpleName();

    public static final String USER_LOGIN = "user_login";

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void callToApi(Object params, String apiTag, boolean shouldShowLoader) {
        if (shouldShowLoader)
            getStatusBehaviorRelay().accept(ApiCallStatus.LOADING);

        InternetObservingSettings settings = InternetObservingSettings.builder()
                .strategy(new SocketInternetObservingStrategy())
                .build();

//        getCompositeDisposable().add(
//                AppUtils.checkHardInternetConnection()
//                        .subscribeOn(Schedulers.io())
//                        .doOnSuccess(aBoolean -> {
//                            if (aBoolean) {
        try {
            if (USER_LOGIN.equals(apiTag)) {
//                callToUserLogin((LoginParamModel) params, apiTag, shouldShowLoader);
                callToUserLogin((HashMap<String, String>) params, apiTag, shouldShowLoader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//                            }
//                        })
//                        .doOnError(e -> {
//                            getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
//                            getErrorRelay().accept(Objects.requireNonNull(e.getLocalizedMessage()));
//                        })
//                        .subscribe());
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
                            processLoginData(jsonObject, apiTag, shouldShowLoader);
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

    private void callToUserLogin(HashMap<String, String> params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                RestCallAPI.restCallAPI(
                        EndPoints.LOGIN,
                        new HashMap<>(),
                        params,
                        new DisposableSingleObserver<Response<ResponseBody>>() {
                            @Override
                            public void onSuccess(Response<ResponseBody> response) {
                                try {
                                    JSONObject jsonObject = parseOnSuccess(response, apiTag, shouldShowLoader);
                                    if (jsonObject != null) {
                                        AppLog.error(TAG, "User Login :" + jsonObject.toString());
                                        JSONObject data = jsonObject.getJSONObject("data");
                                        getPreference().putStringValue(PREF_AUTHENTICATION_TOKEN, data.getString("token"));
                                        callToUserProfile(new HashMap<>(), apiTag, shouldShowLoader);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (shouldShowLoader)
                                        getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                                    getErrorRelay().accept(Objects.requireNonNull(e.getLocalizedMessage()));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                parseOnError(e, apiTag, shouldShowLoader);
                            }
                        }
                ));
    }
    private void processLoginData(JSONObject jsonObject,String apiTag,boolean shouldShowLoader){
        getCompositeDisposable().add(
                Single.just(Objects.requireNonNull(jsonObject.optJSONObject(Constants.KEY_DATA)))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorReturn(throwable -> {
                            throwable.printStackTrace();
                            return new JSONObject();
                        })
                        .filter(jsonObject1 -> !(null == jsonObject1))
                        .doOnSuccess(data -> {
                            getPreference().putStringValue(PREF_AUTHENTICATION_TOKEN, data.getString("token"));
                            callToUserProfile(new HashMap<>(), apiTag, shouldShowLoader);

                        })
                        .doOnComplete(() -> {
                            if (shouldShowLoader)
                                getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                            getErrorRelay().accept(getContextBaseViewModel().getString(R.string.something_went_wrong));
                        })
                        .subscribe()
        );
    }


    private void callToUserProfile(HashMap<String, String> params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                RestCallAPI.restCallAPI(
                        EndPoints.PROFILE,
                        new HashMap<>(),
                        params,
                        new DisposableSingleObserver<Response<ResponseBody>>() {
                            @Override
                            public void onSuccess(Response<ResponseBody> response) {
                                try {
                                    JSONObject jsonObject = parseOnSuccess(response, apiTag, shouldShowLoader);
                                    if (jsonObject != null) {
                                        AppLog.error(TAG, "User Profile :" + jsonObject.toString());
                                        JSONObject data = jsonObject.getJSONObject("data");
                                        processUserData(data)
                                                .subscribeOn(Schedulers.io())
                                                .doOnComplete(() -> {
                                                    if (shouldShowLoader) {
                                                        getStatusBehaviorRelay().accept(ApiCallStatus.SUCCESS);
                                                    }
                                                    getResponseRelay().accept(new Pair<>(apiTag, jsonObject));
                                                }).doOnError(throwable -> {
                                            if (shouldShowLoader)
                                                getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                                            getErrorRelay().accept(Objects.requireNonNull(throwable.getLocalizedMessage()));
                                        }).subscribe();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (shouldShowLoader)
                                        getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                                    getErrorRelay().accept(Objects.requireNonNull(e.getLocalizedMessage()));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                parseOnError(e, apiTag, shouldShowLoader);
                            }
                        }
                ));
    }

    private Completable processUserData(JSONObject data) {
        return Single.just(data)
                .subscribeOn(Schedulers.computation())
                .filter(jsonObject -> !(null == jsonObject))
                .toObservable()
                .concatMapSingle((Function<JSONObject, SingleSource<User>>) jsonObject -> {
                    User user = new User();
                    user.setId(jsonObject.optInt("id"));
                    user.setFirst_name(jsonObject.getString("first_name"));
                    user.setMiddle_name(jsonObject.getString("middle_name"));
                    user.setLast_name(jsonObject.getString("last_name"));
                    user.setUsername(jsonObject.getString("username"));
                    user.setMno(jsonObject.getString("mno"));
                    user.setEmail(jsonObject.getString("email"));
                    user.setProfile(jsonObject.getString("profile"));
                    user.setDepartment(jsonObject.getString("department"));

                    getPreference().putStringValue(PREF_USER_ID, String.valueOf(user.getId()));
                    getPreference().putStringValue(PREF_USER_FIRST_NAME, user.getFirst_name());
                    getPreference().putStringValue(PREF_USER_MIDDLE_NAME, user.getMiddle_name());
                    getPreference().putStringValue(PREF_USER_LAST_NAME, user.getLast_name());
                    getPreference().putStringValue(PREF_USER_NAME, user.getUsername());
                    getPreference().putStringValue(PREF_USER_MNO, user.getMno());
                    getPreference().putStringValue(PREF_USER_EMAIL, user.getEmail());
                    getPreference().putStringValue(PREF_USER_PROFILE, user.getProfile());
                    getPreference().putStringValue(PREF_USER_DEPARTMENT, user.getDepartment());


                    return Single.just(user);
                })
                .onErrorReturn(throwable -> {
                    throwable.printStackTrace();
                    return new User();
                })
                .filter(user -> user != null && user.getId() != 0)
                .concatMapCompletable(user -> getDatabase().userDao().insertUser(user));
    }
}
