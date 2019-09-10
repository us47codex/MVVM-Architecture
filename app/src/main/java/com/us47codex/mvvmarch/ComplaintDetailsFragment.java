package com.us47codex.mvvmarch;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProviders;

import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.base.BaseFragment;
import com.us47codex.mvvmarch.complaint.ComplaintViewModel;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.home.HomeViewModel;
import com.us47codex.mvvmarch.roomDatabase.Complaint;

import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

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
            txvAssignee, txvComplaintDate, txvScheduleDate, txvCloseDate;
    private ComplaintViewModel complaintViewModel;
    private long complainId;

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
        getCommentFromDB();
        subscribeApiCallStatusObservable();
    }

    private void initActionBar(View view) {
        ImageView imgBackButton = view.findViewById(R.id.imageBack);
        ImageView imageOne = view.findViewById(R.id.imageOne);
        imageOne.setVisibility(View.VISIBLE);
        imageOne.setImageDrawable(Objects.requireNonNull(getContext()).getDrawable(R.drawable.ic_profile_edit));

        compositeDisposable.add(
                RxView.clicks(imgBackButton).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> backToPreviousFragment(R.id.complaintsFragment,
                        imgBackButton, false))
        );

        compositeDisposable.add(
                RxView.clicks(imageOne).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    jumpToDestinationFragment(getCurrentFragmentId(), R.id.toVisitReportFragment, frameMain, null, false);
                })
        );
    }

    private void initView(View view) {
        frameMain = view.findViewById(R.id.frameMain);

        txvComplaintNo = view.findViewById(R.id.txvComplaintNo);
        txvStatus = view.findViewById(R.id.txvStatus);
        txvCustomerName = view.findViewById(R.id.txvCustomerName);
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

        compositeDisposable.add(
                RxView.clicks(txvComplaintNo).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {

                })
        );
    }

    private void setData(Complaint complaint) {
        txvComplaintNo.setText(String.valueOf(complaint.getId()));
        txvStatus.setText(complaint.getStatus());
        txvCustomerName.setText(complaint.getCustomerFullName());
        txvMobile.setText(complaint.getMno());
        txvEmail.setText(complaint.getEmail());
        txvDealerName.setText(complaint.getDealerName());
        txvMachineType.setText(complaint.getMcType());
        txvMachineModel.setText(complaint.getMcModel());
        txvSerialNo.setText(complaint.getSrNo());
        txvReferenceAlternateNumber.setText(complaint.getAlternateNum());
        txvAddress.setText(complaint.getAddress());
        txvProblemDescription.setText(complaint.getDescription());
        txvComplaintBy.setText(complaint.getComplainForm());
        txvAssignee.setText(complaint.getComplainTo());
        txvComplaintDate.setText(complaint.getAssignDate());
        txvScheduleDate.setText(complaint.getScheduleDate());
//        txvCloseDate.setText(complaint.getclose());
    }

    private void getCommentFromDB() {
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
                                if (pair.first.equals(HomeViewModel.POST_PROFILE_API_TAG)) {
                                    enableDisableView(frameMain, true);
                                    hideProgressLoader();
                                    JSONObject jsonObject = (JSONObject) pair.second;
                                    if (jsonObject != null && jsonObject.getInt(Constants.KEY_SUCCESS) == 1) {
                                        showDialogWithSingleButtons(getContext(), getString(R.string.app_name),
                                                Objects.requireNonNull(getActivity()).getString(R.string.profile_update_msg), Objects.requireNonNull(getActivity()).getString(R.string.ok), (dialog, which) -> backToPreviousFragment(R.id.homeFragment, frameMain, false), false);
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
}