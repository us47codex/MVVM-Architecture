package com.us47codex.mvvmarch.complaint;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProviders;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.SunTecApplication;
import com.us47codex.mvvmarch.base.BaseFragment;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppLog;
import com.us47codex.mvvmarch.helper.AppUtils;
import com.us47codex.mvvmarch.roomDatabase.Complaint;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

import static com.us47codex.mvvmarch.constant.Constants.KEY_COMPLAIN_ID;

/**
 * Created by Upendra Shah on 30 August, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/
public class ComplaintDetailsFragment extends BaseFragment {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private AppCompatTextView txvComplaintNo, txvStatus, txvCustomerName, txvMobile, txvEmail, txvDealerName, txvMachineType,
            txvMachineModel, txvSerialNo, txvReferenceAlternateNumber, txvAddress, txvProblemDescription, txvComplaintBy,
            txvAssignee, txvComplaintDate, txvScheduleDate, txvCloseDate, txvCustomerCompanyName;
    private AppCompatButton btnStarWork, btnSchedule, btnVisitReport;
    private ComplaintViewModel complaintViewModel;
    private long complainId;
    private Dialog dialogWakeUpCall;
    private Complaint complaint;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_complaint_details;
    }

    @Override
    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.complaint);
    }

    @Override
    protected boolean shouldShowToolbar() {
        return true;
    }

    @Override
    protected boolean shouldShowBackArrow() {
        return true;
    }

    @Override
    protected boolean shouldShowSecondImageIcon() {
        return false;
    }

    @Override
    protected boolean shouldShowFirstImageIcon() {
        return false;
    }

    @Override
    protected boolean shouldLoaderImplement() {
        return true;
    }

    @Override
    protected boolean shouldShowDrawer() {
        return false;
    }

    @Override
    protected int getCurrentFragmentId() {
        return R.id.complaintDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = Objects.requireNonNull(getActivity()).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            complainId = bundle.getLong(Constants.KEY_COMPLAIN_ID, 0);
        }
        complaintViewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_complaint_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionBar(view);
        initView(view);
        getComplainFromDB();
        subscribeApiCallStatusObservable();
        //getLocation();
        if (hasPermissions()) {
            getLocation(getContext());
        } else {
            requestLocationPermissions();
        }
    }

    private void initActionBar(View view) {
        ImageView imgBackButton = view.findViewById(R.id.imageBack);
        ImageView imageOne = view.findViewById(R.id.imageOne);
        imageOne.setVisibility(View.GONE);
        imageOne.setImageDrawable(Objects.requireNonNull(getContext()).getDrawable(R.drawable.ic_profile_edit));

        compositeDisposable.add(
                RxView.clicks(imgBackButton).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> backToPreviousFragment(R.id.complaintsFragment,
                        imgBackButton, false))
        );

        compositeDisposable.add(
                RxView.clicks(imageOne).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    if (complaint != null) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(KEY_COMPLAIN_ID, complaint.getId());
                        if (complaint.getMcType().equalsIgnoreCase(Constants.BURNER)) {
                            if (complaint.getVisitType().equalsIgnoreCase(Constants.INSTALLATION_AND_COMMISSIONING)
                                    || complaint.getVisitType().equalsIgnoreCase(Constants.PRE_INSTALLED)) {
                                jumpToDestinationFragment(getCurrentFragmentId(), R.id.toVisitBurnerInstallationFragment, frameMain, bundle, false);
                            } else {
                                jumpToDestinationFragment(getCurrentFragmentId(), R.id.toVisitBurnerServiceFragment, frameMain, bundle, false);
                            }
                        } else {
                            jumpToDestinationFragment(getCurrentFragmentId(), R.id.toVisitOthersFragment, frameMain, bundle, false);
                        }
                    }
                })
        );
    }

    private void initView(View view) {
        frameMain = view.findViewById(R.id.frameMain);

        txvComplaintNo = view.findViewById(R.id.txvComplaintNo);
        txvStatus = view.findViewById(R.id.txvStatus);
        txvCustomerName = view.findViewById(R.id.txvCustomerName);
        txvCustomerCompanyName = view.findViewById(R.id.txvCustomerCompanyName);
        txvMobile = view.findViewById(R.id.txvMobile);
        txvEmail = view.findViewById(R.id.txvEmail);
        txvDealerName = view.findViewById(R.id.txvDealerName);
        txvMachineType = view.findViewById(R.id.txvMachineType);
        txvMachineModel = view.findViewById(R.id.txvMachineModel);
        txvSerialNo = view.findViewById(R.id.txvSerialNo);
        txvReferenceAlternateNumber = view.findViewById(R.id.txvReferenceAlternateNumber);
        txvAddress = view.findViewById(R.id.txvAddress);
        txvProblemDescription = view.findViewById(R.id.txvProblemDescription);
        txvComplaintBy = view.findViewById(R.id.txvComplaintBy);
        txvAssignee = view.findViewById(R.id.txvAssignee);
        txvComplaintDate = view.findViewById(R.id.txvComplaintDate);
        txvScheduleDate = view.findViewById(R.id.txvScheduleDate);
        txvCloseDate = view.findViewById(R.id.txvCloseDate);

        btnSchedule = view.findViewById(R.id.btnSchedule);
        btnStarWork = view.findViewById(R.id.btnStarWork);
        btnVisitReport = view.findViewById(R.id.btnVisitReport);

        compositeDisposable.add(
                RxView.clicks(btnSchedule).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    showDialogForSchedule();
                })
        );
        compositeDisposable.add(
                RxView.clicks(btnStarWork).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    showProgressLoader();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("complain_id", String.valueOf(complainId));
                    params.put("in_lat", String.valueOf(SunTecApplication.getInstance().latitude));
                    params.put("in_long", String.valueOf(SunTecApplication.getInstance().longitude));
                    complaintViewModel.callToApi(params, ComplaintViewModel.WORK_START_API_TAG, false);

                })
        );
        compositeDisposable.add(
                RxView.clicks(btnVisitReport).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    if (complaint != null) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(KEY_COMPLAIN_ID, complaint.getId());
                        if (complaint.getMcType().equalsIgnoreCase(Constants.BURNER)) {
                            if (complaint.getVisitType().equalsIgnoreCase(Constants.INSTALLATION_AND_COMMISSIONING)
                                    || complaint.getVisitType().equalsIgnoreCase(Constants.PRE_INSTALLED)) {
                                jumpToDestinationFragment(getCurrentFragmentId(), R.id.toVisitBurnerInstallationFragment, frameMain, bundle, false);
                            } else {
                                jumpToDestinationFragment(getCurrentFragmentId(), R.id.toVisitBurnerServiceFragment, frameMain, bundle, false);
                            }
                        } else {
                            jumpToDestinationFragment(getCurrentFragmentId(), R.id.toVisitOthersFragment, frameMain, bundle, false);
                        }
                    }
                })
        );
    }

    private void setData(Complaint complaint) {
        this.complaint = complaint;

        if (complaint.getStatus().equalsIgnoreCase(Constants.STATUS_OPEN)) {
            btnSchedule.setText("Schedule");
            btnSchedule.setVisibility(View.VISIBLE);
            btnStarWork.setVisibility(View.GONE);
            btnVisitReport.setVisibility(View.GONE);
        } else if (complaint.getStatus().equalsIgnoreCase(Constants.STATUS_SCHEDULE)) {
            btnSchedule.setText("Reschedule");
            if (AppUtils.isEmpty(complaint.getInStatus())) {
                btnSchedule.setVisibility(View.VISIBLE);
                btnStarWork.setVisibility(View.VISIBLE);
                btnVisitReport.setVisibility(View.GONE);
            } else {
                btnSchedule.setVisibility(View.GONE);
                btnStarWork.setVisibility(View.GONE);
                btnVisitReport.setVisibility(View.VISIBLE);
            }
//        } else if (complaint.getStatus().equalsIgnoreCase(Constants.STATUS_CLOSED)) {
//            btnSchedule.setVisibility(View.GONE);
//            btnVisit.setVisibility(View.GONE);
//            btnVisitReport.setVisibility(View.VISIBLE);
        } else {
            btnSchedule.setVisibility(View.GONE);
            btnStarWork.setVisibility(View.GONE);
            btnVisitReport.setVisibility(View.GONE);
        }

        txvComplaintNo.setText(complaint.getId() != 0 ? String.valueOf(complaint.getId()) : "0");
        txvStatus.setText(!AppUtils.isEmpty(complaint.getStatus()) ? complaint.getStatus() : "--");
        txvCustomerName.setText(!AppUtils.isEmpty(complaint.getCustomerLastName()) ? complaint.getCustomerLastName() : "--");
        txvCustomerCompanyName.setText(!AppUtils.isEmpty(complaint.getCustomerFirstName()) ? complaint.getCustomerFirstName() : "--");
        txvMobile.setText(!AppUtils.isEmpty(complaint.getMno()) ? complaint.getMno() : "--");
        txvEmail.setText(!AppUtils.isEmpty(complaint.getEmail()) ? complaint.getEmail() : "--");
        txvDealerName.setText(!AppUtils.isEmpty(complaint.getDealerName()) ? complaint.getDealerName() : "--");
        txvMachineType.setText(!AppUtils.isEmpty(complaint.getMcType()) ? complaint.getMcType() : "--");
        txvMachineModel.setText(!AppUtils.isEmpty(complaint.getMcModel()) ? complaint.getMcModel() : "--");
        txvSerialNo.setText(!AppUtils.isEmpty(complaint.getSrNo()) ? complaint.getSrNo() : "--");
        txvReferenceAlternateNumber.setText(!AppUtils.isEmpty(complaint.getAlternateNum()) ? complaint.getAlternateNum() : "--");
        txvAddress.setText(!AppUtils.isEmpty(complaint.getAddress()) ? complaint.getAddress() : "--");
        txvProblemDescription.setText(!AppUtils.isEmpty(complaint.getDescription()) ? complaint.getDescription() : "--");
        txvComplaintBy.setText(!AppUtils.isEmpty(complaint.getComplainForm()) ? complaint.getComplainForm() : "-");
        txvAssignee.setText(!AppUtils.isEmpty(complaint.getComplainTo()) ? complaint.getComplainTo() : "--");
        txvComplaintDate.setText(!AppUtils.isEmpty(complaint.getAssignDate()) ? complaint.getAssignDate() : "--");
        txvScheduleDate.setText(!AppUtils.isEmpty(complaint.getScheduleDate()) ? complaint.getScheduleDate() : "--");
        txvCloseDate.setText("--");
    }

    protected void showDialogForSchedule() {
        if (dialogWakeUpCall != null) {
            if (dialogWakeUpCall.isShowing()) {
                return;
            }
        }
        dialogWakeUpCall = new Dialog(getContext());
        dialogWakeUpCall.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setContentView(R.layout.profile_preference_two);
        dialogWakeUpCall.setContentView(R.layout.dialog_schedule);
//        Objects.requireNonNull(dialogWakeUpCall.getWindow()).setBackgroundDrawableResource(R.drawable.settings_bg_gray);
//        dialogWakeUpCall.getWindow().setLayout(Utils.getWidth(LandingPageActivity.this, 0.05), RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialogWakeUpCall.setCancelable(true);

        Button btn_send = dialogWakeUpCall.findViewById(R.id.btnSetSchedule);
        Button btn_cancel = dialogWakeUpCall.findViewById(R.id.btnCancel);
        TextView txt_dialog_title = dialogWakeUpCall.findViewById(R.id.txt_dialog_title);

        final SingleDateAndTimePicker wakeupDateAndTimePicker = dialogWakeUpCall.findViewById(R.id.single_day_picker);
        wakeupDateAndTimePicker.setIsAmPm(true);
        wakeupDateAndTimePicker.setStepMinutes(1);
        wakeupDateAndTimePicker.setDayFormatter(new SimpleDateFormat("EEE dd MMM", Locale.ENGLISH));

        Calendar minWakeUpDateTimeCalendar = Calendar.getInstance(Locale.ENGLISH);
        minWakeUpDateTimeCalendar.add(Calendar.MINUTE, 5);
        minWakeUpDateTimeCalendar.set(Calendar.SECOND, 0);
        wakeupDateAndTimePicker.setMinDate(minWakeUpDateTimeCalendar.getTime());

        btn_cancel.setOnClickListener(view -> {
            dialogWakeUpCall.dismiss();
            dialogWakeUpCall.cancel();
        });
        btn_send.setOnClickListener(view -> {
            dialogWakeUpCall.dismiss();
            dialogWakeUpCall.cancel();
            try {
                showProgressLoader();
                HashMap<String, String> params = new HashMap<>();
                params.put("schedule_date", AppUtils.convertDateToString(wakeupDateAndTimePicker.getDate(), "MM/dd/yyyy hh:mm a"));
                params.put("schedule_id", String.valueOf(complainId));
                complaintViewModel.callToApi(params, ComplaintViewModel.COMPLAIN_SCHEDULE_API_TAG, false);

            } catch (Exception e) {
                AppLog.error("error", e);
            }
        });

        try {
            dialogWakeUpCall.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getComplainFromDB() {
        getDatabase().complaintDao().getComplaintById(complainId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableMaybeObserver<Complaint>() {
                               @Override
                               public void onSuccess(Complaint complaint) {
                                   setData(complaint);
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                               }

                               @Override
                               public void onComplete() {

                               }
                           }
                );
    }

    @Override
    public void onClick(View view) {

    }

    private void subscribeApiCallStatusObservable() {
        getCompositeDisposable().add(Observable.merge(complaintViewModel.getStatusBehaviorRelay(),
                complaintViewModel.getErrorRelay(),
                complaintViewModel.getResponseRelay())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    throwable.printStackTrace();
                    return new Object();
                })
                .filter(object -> !(null == object))
                .doOnNext(object -> {
                    try {
                        if (object instanceof ApiCallStatus) {
                            ApiCallStatus apiCallStatus = (ApiCallStatus) object;
                            if (apiCallStatus == ApiCallStatus.ERROR) {
                                hideProgressLoader();
                            }
                        } else if (object instanceof String) {
                            String errorCode = (String) object;
                            showDialogWithSingleButtons(getContext(), getString(R.string.app_name),
                                    String.valueOf(object), Objects.requireNonNull(getActivity()).getString(R.string.ok), (dialog, which) -> {
                                        enableDisableView(frameMain, true);
                                    }, false);

                        } else if (object instanceof Pair) {
                            Pair pair = (Pair) object;
                            if (pair.first != null) {
                                if (pair.first.equals(ComplaintViewModel.COMPLAIN_SCHEDULE_API_TAG)) {
                                    hideProgressLoader();
                                    enableDisableView(frameMain, true);
                                    JSONObject jsonObject = (JSONObject) pair.second;
                                    if (jsonObject != null && jsonObject.getInt(Constants.KEY_SUCCESS) == 1) {
                                        String data = jsonObject.getString("data");
                                        showDialogWithSingleButtons(getContext(), getString(R.string.app_name),
                                                data, Objects.requireNonNull(getActivity()).getString(R.string.ok), (dialog, which) -> {
                                                    backToPreviousFragment(R.id.complaintsFragment, frameMain, false);
                                                }, false);
                                    }
                                } else if (pair.first.equals(ComplaintViewModel.WORK_START_API_TAG)) {
                                    hideProgressLoader();
                                    enableDisableView(frameMain, true);
                                    JSONObject jsonObject = (JSONObject) pair.second;
                                    if (jsonObject != null && jsonObject.getInt(Constants.KEY_SUCCESS) == 1) {
                                        String data = jsonObject.getString("data");
                                        showDialogWithSingleButtons(getContext(), getString(R.string.app_name),
                                                data, Objects.requireNonNull(getActivity()).getString(R.string.ok), (dialog, which) -> {
                                                    btnStarWork.setVisibility(View.GONE);
                                                    btnVisitReport.setVisibility(View.VISIBLE);
                                                }, false);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .doOnError(Throwable::printStackTrace)
                .subscribe()
        );
    }

    private void getLocation() {
        try {
            LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
            }
//        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            lm.requestSingleUpdate(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    Toast.makeText(getContext(), "Lat :: " + latitude + " Long :: " + longitude, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            }, Looper.myLooper());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
