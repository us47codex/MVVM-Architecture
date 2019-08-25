package com.us47codex.mvvmarch.base;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;

import com.google.gson.Gson;
import com.jakewharton.rxrelay2.PublishRelay;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.SunTecApplication;
import com.us47codex.mvvmarch.SunTecPreferenceManager;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppLog;
import com.us47codex.mvvmarch.helper.AppUtils;
import com.us47codex.mvvmarch.helper.ErrorMessageHandlerModel;
import com.us47codex.mvvmarch.roomDatabase.SunTecDatabase;

import org.json.JSONObject;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public abstract class BaseViewModel extends AndroidViewModel {

    private static final String TAG = BaseViewModel.class.getSimpleName();

    private final CompositeDisposable mCompositeDisposable;

    private final Context context;
    private boolean isMyRoleHigher;

    private final PublishRelay<ApiCallStatus> statusBehaviorRelay = PublishRelay.create();
    private final PublishRelay<String> errorRelay = PublishRelay.create();
    /* private final PublishRelay<String> errorCodeRelay = PublishRelay.create();*/
    private final PublishRelay<Pair<String, Object>> responseRelay = PublishRelay.create();

    protected BaseViewModel(@NonNull Application application) {
        super(application);
        this.mCompositeDisposable = new CompositeDisposable();
        this.context = application;
    }

    protected CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    protected Context getContextBaseViewModel() {
        return context;
    }

    public SunTecDatabase getDatabase() {
        return SunTecApplication.getInstance().getDatabase();
    }

    public SunTecPreferenceManager getPreference() {
        return SunTecApplication.getInstance().getPreferenceManager();
    }

    protected Boolean getMyRoleIsHigher() {
        return isMyRoleHigher;
    }

    @Override
    protected void onCleared() {
        getCompositeDisposable().clear();
        System.gc();
        super.onCleared();
    }

  /*  protected long getCustomerId(){
        return CustomerApplication.getInstance().getSessionManager().getLongValue(SessionManager.CUSTOMER_ID, 0);
    }

    protected long getEmployeeId(){
        return CustomerApplication.getInstance().getSessionManager().getLongValue(SessionManager.EMPLOYEE_ID, 0);
    }*/

    public PublishRelay<ApiCallStatus> getStatusBehaviorRelay() {
        return statusBehaviorRelay;
    }

    public PublishRelay<String> getErrorRelay() {
        return errorRelay;
    }

   /* public PublishRelay<String> getErrorCodeRelay() {
        return errorCodeRelay;
    }*/

    public PublishRelay<Pair<String, Object>> getResponseRelay() {
        return responseRelay;
    }

    /**
     * Function will handle onSuccess Response of Retrofit call
     *
     * @param response        Retrofit Response
     * @param API_TAG         for Identify API call
     * @param showProgressBar is need to handle Progress Bar
     * @return JSONObject with added API_TAG
     */
    protected JSONObject parseOnSuccess(Response<ResponseBody> response, String API_TAG, boolean showProgressBar) throws NullPointerException {
        if (AppUtils.isResponseSuccess(response.code())) {
            try {
                String res = response.body() != null ? response.body().string() : "";
                if (!AppUtils.isEmpty(res)) {
                    JSONObject obj = new JSONObject(res);
                    if (obj.has(Constants.KEY_CODE)) {
                        AppLog.error(TAG, "parseOnSuccess :: API TAG : " + API_TAG + ":: Error : " + obj.getString(Constants.KEY_CODE));
                        if (showProgressBar)
                            getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                        String errMessage = AppUtils.getMessageForErrorCode(obj, getContextBaseViewModel());
                        getErrorRelay().accept(errMessage);
                    } else {
                        obj.put(Constants.KEY_API_TAG, API_TAG);
                        /*if (showProgressBar)
                            complainListStatusBehaviorRelay.accept(ApiCallStatus.SUCCESS);*/
//                        responseNavigator.handleSuccess(obj);
                        return obj;
                    }
                } else {
                    AppLog.error(TAG, "parseOnSuccess :: API TAG : " + API_TAG + ":: Error : Response Null");
                    if (showProgressBar)
                        getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                    getErrorRelay().accept(context.getString(R.string.something_went_wrong));
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppLog.error(TAG, "parseOnSuccess :: API TAG : " + API_TAG + ":: Error : " + e);
                if (showProgressBar)
                    getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                getErrorRelay().accept(Objects.requireNonNull(e.getLocalizedMessage()));
            }
        } else {
            AppLog.error(TAG, String.valueOf(response.code()));
            if (/*response.code() == Constants.BAD_REQUEST ||*/
                /*response.code() == Constants.UNAUTHORIZED ||*/
                    response.code() == Constants.FORBIDDEN ||
                            response.code() == Constants.NOT_FOUND ||
                            response.code() == Constants.REQUEST_TIMEOUT ||
                            response.code() == Constants.SERVER_BROKEN ||
                            response.code() == Constants.BAD_GATEWAY ||
                            response.code() == Constants.SERVICE_UNAVAILABLE ||
                            response.code() == Constants.GATEWAY_TIMEOUT) {

                getCompositeDisposable().add(
                        AppUtils.getErrorMessageFRomErrorCode(response.code())
                                .subscribeOn(Schedulers.io())
                                .onErrorReturn(throwable -> {
                                    throwable.printStackTrace();
                                    return throwable.getLocalizedMessage();
                                })
                                .doOnSuccess(message -> {
                                    AppLog.error(TAG, "parseOnSuccess :: API TAG : " + API_TAG + ":: Error : " + message);
                                    if (showProgressBar)
                                        getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                                    getErrorRelay().accept(message);
                                })
                                .subscribe()
                );
            } else {
                if (response.code() != Constants.UNAUTHORIZED) {
                    Gson gson = new Gson();
                    ErrorMessageHandlerModel message = null;
                    if (response.errorBody() != null) {
                        message = gson.fromJson(response.errorBody().charStream(), ErrorMessageHandlerModel.class);
                    }

                    if (message == null && response.body() != null) {
                        message = gson.fromJson(response.body().charStream(),
                                ErrorMessageHandlerModel.class);
                    }

                    getCompositeDisposable().add(
                            AppUtils.getMyLanguageErrorMessage(message)
                                    .subscribeOn(Schedulers.io())
                                    .onErrorReturn(Throwable::getLocalizedMessage)
                                    .doOnSuccess(message1 -> {
                                        if (showProgressBar)
                                            getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                                        getErrorRelay().accept(message1);
                                    })
                                    .subscribe()

                    );
                } else {
                    if (showProgressBar)
                        getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                }
            }
        }
        return null;
    }


    /**
     * Function will handle onSuccess Response of Retrofit call
     *
     * @param error           Throwable error
     * @param API_TAG         for Identify API call
     * @param showProgressBar is need to handle Progress Bar
     */
    protected void parseOnError(Throwable error, String API_TAG, boolean showProgressBar) {
        try {
            if (error.getCause() == null) {
                if (showProgressBar)
                    getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);
                getErrorRelay().accept(context.getString(R.string.please_try_again));
                /*getErrorCodeRelay().accept(AppUtils.isEmpty(error.getLocalizedMessage()) ?
                        Constants.ERR_SOMETHING_WRONG : error.getLocalizedMessage());*/
            } else {
                AppLog.error(TAG, "parseOnError :: API TAG : " + API_TAG + ":: Error : " + error.getLocalizedMessage());
                if (showProgressBar)
                    getStatusBehaviorRelay().accept(ApiCallStatus.ERROR);

                if (Objects.requireNonNull(error.getLocalizedMessage()).equalsIgnoreCase("timeout")
                        || error.getLocalizedMessage().equalsIgnoreCase("time out")) {

                    getErrorRelay().accept(context.getString(R.string.please_try_again));
                } else {
                    getErrorRelay().accept(AppUtils.isEmpty(error.getLocalizedMessage()) ? Constants.ERR_SOMETHING_WRONG : error.getLocalizedMessage());
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
