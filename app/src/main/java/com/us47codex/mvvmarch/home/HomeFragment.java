package com.us47codex.mvvmarch.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.SunTecApplication;
import com.us47codex.mvvmarch.SunTecPreferenceManager;
import com.us47codex.mvvmarch.base.BaseFragment;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.us47codex.mvvmarch.constant.Constants.KEY_FILTER_COMPLAINT;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private AppCompatTextView txtApprovalPendingCount, txtTotalComplaintsCount, txtClosedComplaintsCount, txtScheduleComplaintsCount, txtOpenComplaintsCount;
    private LinearLayout llTotalComplaints, llClosedComplaints, llScheduleComplaints, llOpenComplaints;
    private HomeViewModel homeViewModel;
    private String total_complain, open_complain, closed_complain, today_schedule_complain;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.home);
    }

    @Override
    protected boolean shouldShowToolbar() {
        return true;
    }

    @Override
    protected boolean shouldShowBackArrow() {
        return false;
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
        return true;
    }

    @Override
    protected int getCurrentFragmentId() {
        return R.id.homeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = Objects.requireNonNull(getActivity()).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        getDashboardDataFromServer();
        subscribeApiCallStatusObservable();
        checkLocation();
    }

    private void checkLocation() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (!isWorkScheduled()) {
                AppUtils.callWorkManager(getContext());
            }
        }
    }

    private void initView(View view) {
        frameMain = view.findViewById(R.id.frameMain);
        txtApprovalPendingCount = view.findViewById(R.id.txtApprovalPendingCount);
        txtTotalComplaintsCount = view.findViewById(R.id.txtTotalComplaintsCount);
        txtClosedComplaintsCount = view.findViewById(R.id.txtClosedComplaintsCount);
        txtScheduleComplaintsCount = view.findViewById(R.id.txtScheduleComplaintsCount);
        txtOpenComplaintsCount = view.findViewById(R.id.txtOpenComplaintsCount);

        llOpenComplaints = view.findViewById(R.id.llOpenComplaints);
        llScheduleComplaints = view.findViewById(R.id.llScheduleComplaints);
        llClosedComplaints = view.findViewById(R.id.llClosedComplaints);
        llTotalComplaints = view.findViewById(R.id.llTotalComplaints);

        compositeDisposable.add(
                RxView.clicks(llOpenComplaints).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_FILTER_COMPLAINT, Constants.STATUS_OPEN);
                    jumpToDestinationFragment(getCurrentFragmentId(), R.id.toComplaintsFragment, frameMain, bundle, false);
                })
        );
        compositeDisposable.add(
                RxView.clicks(llScheduleComplaints).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_FILTER_COMPLAINT, Constants.STATUS_SCHEDULE);
                    jumpToDestinationFragment(getCurrentFragmentId(), R.id.toComplaintsFragment, frameMain, bundle, false);
                })
        );
        compositeDisposable.add(
                RxView.clicks(llClosedComplaints).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_FILTER_COMPLAINT, Constants.STATUS_CLOSED);
                    jumpToDestinationFragment(getCurrentFragmentId(), R.id.toComplaintsFragment, frameMain, bundle, false);
                })
        );
        compositeDisposable.add(
                RxView.clicks(llTotalComplaints).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_FILTER_COMPLAINT, Constants.STATUS_ALL);
                    jumpToDestinationFragment(getCurrentFragmentId(), R.id.toComplaintsFragment, frameMain, bundle, false);
                })
        );
    }

    @Override
    public void onClick(View view) {

    }

    private void setData() {
        txtTotalComplaintsCount.setText(total_complain);
        txtClosedComplaintsCount.setText(closed_complain);
        txtScheduleComplaintsCount.setText(today_schedule_complain);
        txtOpenComplaintsCount.setText(open_complain);
    }

    private void getDashboardDataFromServer() {
        showProgressLoader();
        homeViewModel.callToApi(new HashMap<>(), HomeViewModel.DASHBOARD_API_TAG, true);
    }

    private void subscribeApiCallStatusObservable() {
        getCompositeDisposable().add(Observable.merge(homeViewModel.getStatusBehaviorRelay(),
                homeViewModel.getErrorRelay(),
                homeViewModel.getResponseRelay())
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
                                if (pair.first.equals(HomeViewModel.DASHBOARD_API_TAG)) {
                                    enableDisableView(frameMain, true);
                                    hideProgressLoader();
                                    JSONObject jsonObject = (JSONObject) pair.second;
                                    if (jsonObject != null && jsonObject.getInt(Constants.KEY_SUCCESS) == 1) {
                                        JSONObject data = jsonObject.getJSONObject("data");
                                        total_complain = data.optString("total_complain", "0");
                                        open_complain = data.optString("open_complain", "0");
                                        closed_complain = data.optString("closed_complain", "0");
                                        today_schedule_complain = data.optString("today_schedule_complain", "0");
                                        setData();
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


    private boolean isWorkScheduled() {
        try {
            boolean running = false;
            if (TextUtils.isEmpty(SunTecApplication.getInstance().getPreferenceManager().getStringValue(SunTecPreferenceManager.PREF_LOCATION_ID, ""))) {
                return false;
            }
            String strUUID = SunTecApplication.getInstance().getPreferenceManager().getStringValue(SunTecPreferenceManager.PREF_LOCATION_ID, "");
            //if(UUID.fromString(MyApplication.getInstance().getPrefManager().getLocationId().equals("null"))
            if (!TextUtils.isEmpty(strUUID) && !strUUID.equals("null")) {
                strUUID = String.valueOf(UUID.fromString(strUUID));
                ListenableFuture<WorkInfo> workStatus1 = WorkManager.getInstance().getWorkInfoById(UUID.fromString(SunTecApplication.getInstance().getPreferenceManager().getStringValue(SunTecPreferenceManager.PREF_LOCATION_ID, "")));
                // workStatus1.get().getState();
                try {
                    if (workStatus1.get().getState() == WorkInfo.State.ENQUEUED) {
                        running = true;
                    }
                    //running = workStatus1.get().getState() == State.RUNNING | workStatus1.get().getState() == State.ENQUEUED;
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return running;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



}