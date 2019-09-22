package com.us47codex.mvvmarch.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy;
import com.us47codex.mvvmarch.base.BaseViewModel;
import com.us47codex.mvvmarch.constant.EndPoints;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppLog;
import com.us47codex.mvvmarch.restApi.RestCallAPI;
import com.us47codex.mvvmarch.roomDatabase.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_DEPARTMENT;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_EMAIL;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_FIRST_NAME;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_ID;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_LAST_NAME;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_MIDDLE_NAME;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_MNO;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_NAME;
import static com.us47codex.mvvmarch.SunTecPreferenceManager.PREF_USER_PROFILE;

public class HomeViewModel extends BaseViewModel {

    private final String TAG = HomeViewModel.class.getSimpleName();

    public static final String POST_PROFILE_API_TAG = "post_profile_api_tag";
    public static final String CHANGE_PASSWORD_API_TAG = "change_password_api_tag";
    public static final String DASHBOARD_API_TAG = "DASHBOARD_API_TAG";

    public HomeViewModel(@NonNull Application application) {
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
            switch ((apiTag)) {
                case DASHBOARD_API_TAG:
                    callToDashboard((HashMap<String, String>) params, apiTag, shouldShowLoader);
                    break;
                case POST_PROFILE_API_TAG:
                    callToUpdateProfile((HashMap<String, RequestBody>) params, apiTag, shouldShowLoader);
                    break;
                case CHANGE_PASSWORD_API_TAG:
                    callToChangePassword((HashMap<String, String>) params, apiTag, shouldShowLoader);
                    break;
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

    private void callToUpdateProfile(HashMap<String, RequestBody> params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                RestCallAPI.restMultipartCallAPI(
                        EndPoints.UPDATE_PROFILE,
                        getHeaders(),
                        params,
                        new DisposableSingleObserver<Response<ResponseBody>>() {
                            @Override
                            public void onSuccess(Response<ResponseBody> response) {
                                try {
                                    JSONObject jsonObject = parseOnSuccess(response, apiTag, shouldShowLoader);
                                    if (jsonObject != null) {
                                        AppLog.error(TAG, "User Login :" + jsonObject.toString());
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

    private void callToChangePassword(HashMap<String, String> params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                RestCallAPI.restCallAPI(
                        EndPoints.CHANGE_PASSWORD,
                        getHeaders(),
                        params,
                        new DisposableSingleObserver<Response<ResponseBody>>() {
                            @Override
                            public void onSuccess(Response<ResponseBody> response) {
                                try {
                                    JSONObject jsonObject = parseOnSuccess(response, apiTag, shouldShowLoader);
                                    if (jsonObject != null) {
                                        AppLog.error(TAG, "User Login :" + jsonObject.toString());
                                        if (shouldShowLoader) {
                                            getStatusBehaviorRelay().accept(ApiCallStatus.SUCCESS);
                                        }
                                        getResponseRelay().accept(new Pair<>(apiTag, jsonObject));
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

    private void callToDashboard(HashMap<String, String> params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                RestCallAPI.restGetCallAPI(
                        EndPoints.DASHBOARD,
                        getHeaders(),
                        params,
                        new DisposableSingleObserver<Response<ResponseBody>>() {
                            @Override
                            public void onSuccess(Response<ResponseBody> response) {
                                try {
                                    JSONObject jsonObject = parseOnSuccess(response, apiTag, shouldShowLoader);
                                    if (jsonObject != null) {
                                        AppLog.error(TAG, "User Login :" + jsonObject.toString());
                                        JSONObject data = jsonObject.getJSONObject("data");
//                                        processDashboardData(data)
//                                                .subscribeOn(Schedulers.io())
//                                                .doOnComplete(() -> {
                                        if (shouldShowLoader) {
                                            getStatusBehaviorRelay().accept(ApiCallStatus.SUCCESS);
                                        }
                                        getResponseRelay().accept(new Pair<>(apiTag, jsonObject));
//                                                }).doOnError(throwable -> {
//                                            if (shouldShowLoader)
//                                                getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
//                                            getErrorRelay().accept(Objects.requireNonNull(throwable.getLocalizedMessage()));
//                                        }).subscribe();
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

    private Completable processDashboardData(JSONObject data) {
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
