package com.us47codex.mvvmarch.complaint;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy;
import com.us47codex.mvvmarch.base.BaseViewModel;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.constant.EndPoints;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppLog;
import com.us47codex.mvvmarch.restApi.RestCallAPI;
import com.us47codex.mvvmarch.roomDatabase.Complaint;
import com.us47codex.mvvmarch.roomDatabase.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
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

public class ComplaintViewModel extends BaseViewModel {

    private final String TAG = ComplaintViewModel.class.getSimpleName();

    public static final String COMPLAINT_DETAIL_API_TAG = "COMPLAINT_DETAIL_API_TAG";
    public static final String COMPLAINT_LIST_API_TAG = "COMPLAINT_LIST_API_TAG";
    public static final String SCHEDULE_DATE_API_TAG = "SCHEDULE_DATE_API_TAG";
    public static final String COMPLAIN_SCHEDULE_API_TAG = "COMPLAIN_SCHEDULE_API_TAG";
    public static final String HEAT_PUMP_COMPLAIN_VISIT_API_TAG = "HEAT_PUMP_COMPLAIN_VISIT_API_TAG";
    public static final String BURNER_INSTALLATION_COMPLAINT_VISIT_API_TAG = "BURNER_INSTALLATION_COMPLAINT_VISIT_API_TAG";
    public static final String BURNER_SERVICE_COMPLAINT_VISIT_API_TAG = "BURNER_SERVICE_COMPLAINT_VISIT_API_TAG";

    public ComplaintViewModel(@NonNull Application application) {
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
                case COMPLAINT_LIST_API_TAG:
                    callToComplaintList((HashMap<String, String>) params, apiTag, shouldShowLoader);
                    break;

                case COMPLAINT_DETAIL_API_TAG:
                    callToComplaintDetail((HashMap<String, String>) params, apiTag, shouldShowLoader);
                    break;

                case BURNER_SERVICE_COMPLAINT_VISIT_API_TAG:
                    callToBurnerServiceComplainVisit((HashMap<String, String>) params, apiTag, shouldShowLoader);
                    break;

                case BURNER_INSTALLATION_COMPLAINT_VISIT_API_TAG:
                    callToBurnerInstallationComplainVisit((HashMap<String, String>) params, apiTag, shouldShowLoader);
                    break;

                case HEAT_PUMP_COMPLAIN_VISIT_API_TAG:
                    callToHeatPumpComplainVisit((HashMap<String, String>) params, apiTag, shouldShowLoader);
                    break;

                case COMPLAIN_SCHEDULE_API_TAG:
                    callToComplainSchedule((HashMap<String, String>) params, apiTag, shouldShowLoader);
                    break;

                case SCHEDULE_DATE_API_TAG:
                    callToScheduleDate((HashMap<String, String>) params, apiTag, shouldShowLoader);
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

    private void callToComplaintDetail(HashMap<String, String> params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                RestCallAPI.restCallAPI(
                        EndPoints.COMPLAINT_DETAIL,
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

    private void callToComplaintList(HashMap<String, String> params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                RestCallAPI.restCallAPI(
                        EndPoints.COMPLAINT_LIST,
                        getHeaders(),
                        params,
                        new DisposableSingleObserver<Response<ResponseBody>>() {
                            @Override
                            public void onSuccess(Response<ResponseBody> response) {
                                try {
                                    JSONObject jsonObject = parseOnSuccess(response, apiTag, shouldShowLoader);
                                    if (jsonObject != null) {
                                        AppLog.error(TAG, "User Login :" + jsonObject.toString());
                                        JSONArray data = jsonObject.getJSONArray("data");
                                        processToComplaintList(data, apiTag, shouldShowLoader);
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

    private void callToBurnerServiceComplainVisit(HashMap<String, String> params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                RestCallAPI.restCallAPI(
                        EndPoints.BURNER_SERVICE_COMPLAINT_VISIT,
                        getHeaders(),
                        params,
                        new DisposableSingleObserver<Response<ResponseBody>>() {
                            @Override
                            public void onSuccess(Response<ResponseBody> response) {
                                try {
                                    JSONObject jsonObject = parseOnSuccess(response, apiTag, shouldShowLoader);
                                    if (jsonObject != null) {
                                        AppLog.error(TAG, "User Login :" + jsonObject.toString());
                                        JSONArray data = jsonObject.getJSONArray("data");
                                        processToComplaintList(data, apiTag, shouldShowLoader);
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

    private void callToBurnerInstallationComplainVisit(HashMap<String, String> params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                RestCallAPI.restCallAPI(
                        EndPoints.BURNER_INSTALLATION_COMPLAINT_VISIT,
                        getHeaders(),
                        params,
                        new DisposableSingleObserver<Response<ResponseBody>>() {
                            @Override
                            public void onSuccess(Response<ResponseBody> response) {
                                try {
                                    JSONObject jsonObject = parseOnSuccess(response, apiTag, shouldShowLoader);
                                    if (jsonObject != null) {
                                        AppLog.error(TAG, "User Login :" + jsonObject.toString());
                                        JSONArray data = jsonObject.getJSONArray("data");
                                        processToComplaintList(data, apiTag, shouldShowLoader);
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

    private void callToHeatPumpComplainVisit(HashMap<String, String> params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                RestCallAPI.restCallAPI(
                        EndPoints.HEAT_PUMP_COMPLAIN_VISIT,
                        getHeaders(),
                        params,
                        new DisposableSingleObserver<Response<ResponseBody>>() {
                            @Override
                            public void onSuccess(Response<ResponseBody> response) {
                                try {
                                    JSONObject jsonObject = parseOnSuccess(response, apiTag, shouldShowLoader);
                                    if (jsonObject != null) {
                                        AppLog.error(TAG, "User Login :" + jsonObject.toString());
                                        JSONArray data = jsonObject.getJSONArray("data");
                                        processToComplaintList(data, apiTag, shouldShowLoader);
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

    private void callToComplainSchedule(HashMap<String, String> params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                RestCallAPI.restCallAPI(
                        EndPoints.COMPLAIN_SCHEDULE,
                        getHeaders(),
                        params,
                        new DisposableSingleObserver<Response<ResponseBody>>() {
                            @Override
                            public void onSuccess(Response<ResponseBody> response) {
                                try {
                                    JSONObject jsonObject = parseOnSuccess(response, apiTag, shouldShowLoader);
                                    if (jsonObject != null) {
                                        AppLog.error(TAG, "User Login :" + jsonObject.toString());
                                        String data = jsonObject.getString("data");
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

    private void callToScheduleDate(HashMap<String, String> params, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                RestCallAPI.restCallAPI(
                        EndPoints.SCHEDULE_DATE,
                        getHeaders(),
                        params,
                        new DisposableSingleObserver<Response<ResponseBody>>() {
                            @Override
                            public void onSuccess(Response<ResponseBody> response) {
                                try {
                                    JSONObject jsonObject = parseOnSuccess(response, apiTag, shouldShowLoader);
                                    if (jsonObject != null) {
                                        AppLog.error(TAG, "User Login :" + jsonObject.toString());
                                        JSONArray data = jsonObject.getJSONArray("data");
                                        processToComplaintList(data, apiTag, shouldShowLoader);
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

    private void processToComplaintList(JSONArray jsonArray, String apiTag, boolean shouldShowLoader) {
        getCompositeDisposable().add(
                Observable.range(0, jsonArray.length())
                        .subscribeOn(Schedulers.computation())
                        .map(integer -> (JSONObject) jsonArray.get(integer))
                        .onErrorReturn(throwable -> {
                            throwable.printStackTrace();
                            return new JSONObject();
                        })
                        .filter(jsonObject -> !(null == jsonObject))
                        .map(commentJsonObject -> {
//                            Complaint  sfs = new Complaint();
//                            complaint.set(complaint.set(commentJsonObject.optString("customer_name"));
                            Complaint complaint = new Complaint();
                            complaint.setId(commentJsonObject.optLong("id"));
                            complaint.setCustomerName(commentJsonObject.optString("customer_name"));
                            complaint.setMno(commentJsonObject.optString("mno"));
                            complaint.setEmail(commentJsonObject.optString("email"));
                            complaint.setMcType(commentJsonObject.optString("mc_type"));
                            complaint.setMcModel(commentJsonObject.optString("mc_model"));
                            complaint.setSrNo(commentJsonObject.optString("sr_no"));
                            complaint.setDescription(commentJsonObject.optString("description"));
                            complaint.setAlternateNum(commentJsonObject.optString("alternate_num"));
                            complaint.setAddress(commentJsonObject.optString("address"));
                            complaint.setDealerName(commentJsonObject.optString("dealer_name"));
                            complaint.setImage(commentJsonObject.optString("image"));
                            complaint.setComplainForm(commentJsonObject.optString("complain_form"));
                            complaint.setComplainTo(commentJsonObject.optString("complain_to"));
                            complaint.setAssignDate(commentJsonObject.optString("assign_date"));
                            complaint.setPriority(commentJsonObject.optString("priority"));
                            complaint.setPaymentCustomer(commentJsonObject.optString("payment_customer"));
                            complaint.setHold(commentJsonObject.optString("hold"));
                            complaint.setNote(commentJsonObject.optString("note"));
                            complaint.setCreatedAt(commentJsonObject.optString("created_at"));
                            complaint.setUpdatedAt(commentJsonObject.optString("updated_at"));
                            complaint.setStatus(commentJsonObject.optString("status"));
                            complaint.setScheduleDate(commentJsonObject.optString("schedule_date"));
                            complaint.setObservation(commentJsonObject.optString("observation"));
                            complaint.setWorkDate(commentJsonObject.optString("work_date"));
                            complaint.setSpareReplace(commentJsonObject.optString("spare_replace"));
                            complaint.setCheckoutDate(commentJsonObject.optString("checkout_date"));
                            complaint.setResolveImage(commentJsonObject.optString("resolve_image"));
                            complaint.setResolveDate(commentJsonObject.optString("resolve_date"));
                            complaint.setCustomerFullName(commentJsonObject.optString("customer_full_name"));
                            complaint.setComplainToFullName(commentJsonObject.optString("complain_to_full_name"));
                            complaint.setReportNo(commentJsonObject.optString("report_no"));
                            complaint.setContactPerson(commentJsonObject.optString("contact_person"));
                            complaint.setHeatPSrNo(commentJsonObject.optString("heat_p_sr_no"));
                            complaint.setNoOfHeat(commentJsonObject.optString("no_of_heat"));
                            complaint.setHwgSrNo(commentJsonObject.optString("hwg_sr_no"));
                            complaint.setNoOfHwg(commentJsonObject.optString("no_of_hwg"));
                            complaint.setMonthYearInsta(commentJsonObject.optString("month_year_insta"));
                            complaint.setOemDealerName(commentJsonObject.optString("oem_dealer_name"));
                            complaint.setDContactPerson(commentJsonObject.optString("d_contact_person"));
                            complaint.setDAddress(commentJsonObject.optString("d_address"));
                            complaint.setDMno(commentJsonObject.optString("d_mno"));
                            complaint.setEquipment(commentJsonObject.optString("equipment"));
                            complaint.setPoNoDate(commentJsonObject.optString("po_no_date"));
                            complaint.setComplainNoDate(commentJsonObject.optString("complain_no_date"));
                            complaint.setTypeOfCall(commentJsonObject.optString("type_of_call"));
                            complaint.setServices(commentJsonObject.optString("services"));
                            complaint.setPreInstallation(commentJsonObject.optString("pre_installation"));
                            complaint.setInstallation(commentJsonObject.optString("installation"));
                            complaint.setCommissioning(commentJsonObject.optString("commissioning"));
                            complaint.setChargeable(commentJsonObject.optString("chargeable"));
                            complaint.setWarranty(commentJsonObject.optString("warranty"));
                            complaint.setCourtesyVisit(commentJsonObject.optString("courtesy_visit"));
                            complaint.setNatureProblem(commentJsonObject.optString("nature_problem"));
                            complaint.setDescriptionWork(commentJsonObject.optString("description_work"));
                            complaint.setSuggetionCustomer(commentJsonObject.optString("suggetion_customer"));
                            complaint.setCustomerRemark(commentJsonObject.optString("customer_remark"));
                            complaint.setWorkStatus(commentJsonObject.optString("work_status"));
                            complaint.setQualityService(commentJsonObject.optString("quality_service"));
                            complaint.setServicesCharges(commentJsonObject.optString("services_charges"));
                            complaint.setConveyance(commentJsonObject.optString("conveyance"));
                            complaint.setToForm(commentJsonObject.optString("to_form"));
                            complaint.setOthers(commentJsonObject.optString("others"));
                            complaint.setTax(commentJsonObject.optString("tax"));
                            complaint.setSignCustomer(commentJsonObject.optString("sign_customer"));
                            complaint.setSignRepre(commentJsonObject.optString("sign_repre"));
                            complaint.setSignMarketing(commentJsonObject.optString("sign_marketing"));
                            complaint.setReasonIncomplete(commentJsonObject.optString("reason_incomplete"));
                            complaint.setCustomerFirstName(commentJsonObject.optString("customer_first_name"));
                            complaint.setCustomerLastName(commentJsonObject.optString("customer_last_name"));
                            complaint.setComplainFormFirstName(commentJsonObject.optString("complain_form_first_name"));
                            complaint.setComplainFormLastName(commentJsonObject.optString("complain_form_last_name"));
                            complaint.setEngineerFirstName(commentJsonObject.optString("engineer_first_name"));
                            complaint.setEngineerLastName(commentJsonObject.optString("engineer_last_name"));
                            return complaint;
                        })
                        .onErrorReturn(throwable -> {
                            throwable.printStackTrace();
                            return new Complaint();
                        })
                        .filter(complaint -> complaint != null && complaint.getId() != 0)
                        .toList()
                        .filter(complainCommentEntityList -> complainCommentEntityList.size() > 0)
                        .flatMapCompletable(complainCommentEntityList -> getDatabase().complaintDao().insertComplaints(complainCommentEntityList))
                        .doOnComplete(() -> {
                            JSONObject object = new JSONObject();
                            object.put(Constants.KEY_SUCCESS, 1);
                            object.put(Constants.KEY_API_TAG, apiTag);

                            if (shouldShowLoader) {
                                getStatusBehaviorRelay().accept(ApiCallStatus.SUCCESS);
                            }
                            getResponseRelay().accept(new Pair<>(apiTag, object));
                        })
                        .subscribe()
        );
    }


}
