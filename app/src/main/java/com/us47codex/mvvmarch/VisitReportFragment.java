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
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.base.BaseFragment;
import com.us47codex.mvvmarch.complaint.ComplaintViewModel;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.roomDatabase.Complaint;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Upendra Shah on 07 September, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/
public class VisitReportFragment extends BaseFragment {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private ComplaintViewModel complaintViewModel;
    private List<Complaint> complaintList;
    private TextInputEditText edtOEMDealerName, edtMonthYearOfInstallation, edtNoOfHWG, edtHWGModelSerialNo,
            edtHeatPumpModelSerialNo, edtContactPerson, edtCustomerName, edtDate, edtReportNo, edtPartsReplaced,
            edtCustomerRemark, edtSuggestionToCustomer, edtDescriptionOfWorkDone, edtObservation, edtNatureOfProblem,
            edtTypeOfCallReceiveDate, edtComplaintNoDate, edtPONoDate, edtEquipment, edtDealerPhoneNo, edtDealerAddress,
            edtDealerContactPerson, edtTax, edtConvayance, edtOthers, edtServiceCharge;
    private AppCompatButton btnSubmitReport;
    private AppCompatImageView imgMarketingProjectHead, imgSunteRepresentative, imgCustomerSign;
    private AppCompatTextView txvMachineType;
    private RadioGroup rdgQualityOfService, rdgWorkStatus;
    private long complainId;
    private Complaint complaint;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_visit_report;
    }

    @Override
    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.visit_report);
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
        return true;
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
        return R.id.visitReportFragment;
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
        return inflater.inflate(R.layout.fragment_visit_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionBar(view);
        initView(view);
        getComplainFromDB();
        subscribeApiCallStatusObservable();
    }

    private void initActionBar(View view) {
        ImageView imgBackButton = view.findViewById(R.id.imageBack);

        compositeDisposable.add(
                RxView.clicks(imgBackButton).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> backToPreviousFragment(R.id.complaintDetailsFragment,
                        imgBackButton, false))
        );
    }

    private void initView(View view) {
        frameMain = view.findViewById(R.id.frameMain);
        txvMachineType = view.findViewById(R.id.txvMachineType);

        edtOEMDealerName = view.findViewById(R.id.edtOEMDealerName);
        edtMonthYearOfInstallation = view.findViewById(R.id.edtMonthYearOfInstallation);
        edtNoOfHWG = view.findViewById(R.id.edtNoOfHWG);
        edtHWGModelSerialNo = view.findViewById(R.id.edtHWGModelSerialNo);
        edtHeatPumpModelSerialNo = view.findViewById(R.id.edtHeatPumpModelSerialNo);
        edtContactPerson = view.findViewById(R.id.edtContactPerson);
        edtCustomerName = view.findViewById(R.id.edtCustomerName);
        edtDate = view.findViewById(R.id.edtDate);
        edtReportNo = view.findViewById(R.id.edtReportNo);
        edtPartsReplaced = view.findViewById(R.id.edtPartsReplaced);
        edtCustomerRemark = view.findViewById(R.id.edtCustomerRemark);
        edtSuggestionToCustomer = view.findViewById(R.id.edtSuggestionToCustomer);
        edtDescriptionOfWorkDone = view.findViewById(R.id.edtDescriptionOfWorkDone);
        edtObservation = view.findViewById(R.id.edtObservation);
        edtNatureOfProblem = view.findViewById(R.id.edtNatureOfProblem);
        edtTypeOfCallReceiveDate = view.findViewById(R.id.edtTypeOfCallReceiveDate);
        edtComplaintNoDate = view.findViewById(R.id.edtComplaintNoDate);
        edtPONoDate = view.findViewById(R.id.edtPONoDate);
        edtEquipment = view.findViewById(R.id.edtEquipment);
        edtDealerPhoneNo = view.findViewById(R.id.edtDealerPhoneNo);
        edtDealerAddress = view.findViewById(R.id.edtDealerAddress);
        edtDealerContactPerson = view.findViewById(R.id.edtDealerContactPerson);
        edtTax = view.findViewById(R.id.edtTax);
        edtConvayance = view.findViewById(R.id.edtConvayance);
        edtOthers = view.findViewById(R.id.edtOthers);
        edtServiceCharge = view.findViewById(R.id.edtServiceCharge);

        imgMarketingProjectHead = view.findViewById(R.id.imgMarketingProjectHead);
        imgSunteRepresentative = view.findViewById(R.id.imgSunteRepresentative);
        imgCustomerSign = view.findViewById(R.id.imgCustomerSign);

        btnSubmitReport = view.findViewById(R.id.btnSubmitReport);

        compositeDisposable.add(
                RxView.clicks(imgCustomerSign).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                })
        );
        compositeDisposable.add(
                RxView.clicks(imgSunteRepresentative).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                })
        );
        compositeDisposable.add(
                RxView.clicks(imgMarketingProjectHead).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                })
        );
        compositeDisposable.add(
                RxView.clicks(btnSubmitReport).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                })
        );
    }

    @Override
    public void onClick(View view) {

    }

    private void setData(Complaint complaint) {
        this.complaint = complaint;
        txvMachineType.setText(complaint.getMcType());
        edtCustomerName.setText(complaint.getCustomerFullName());
        edtComplaintNoDate.setText(complaint.getComplainNoDate());
        edtOEMDealerName.setText(complaint.getDealerName());
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
                                if (pair.first.equals(ComplaintViewModel.COMPLAINT_LIST_API_TAG)) {
                                    enableDisableView(frameMain, true);
                                    hideProgressLoader();
                                    JSONObject jsonObject = (JSONObject) pair.second;
                                    if (jsonObject != null && jsonObject.getInt(Constants.KEY_SUCCESS) == 1) {

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


