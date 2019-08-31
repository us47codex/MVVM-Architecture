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
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.base.BaseFragment;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppUtils;
import com.us47codex.mvvmarch.login.LoginViewModel;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Upendra Shah on 30 August, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/
public class ResetPasswordFragment extends BaseFragment {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private TextInputLayout inputMobileLayout, inputOTPLayout, inputPasswordLayout, inputConfirmPasswordLayout;
    private AppCompatButton btnUpdatePassword, btnSendOtp;
    private TextInputEditText edtPassword, edtOTP, edtMobileNo, edtConfirmPassword;
    private LoginViewModel loginViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset_password;
    }

    @Override
    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.resetPassword);
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
        return false;
    }

    @Override
    protected boolean shouldShowDrawer() {
        return false;
    }

    @Override
    protected int getCurrentFragmentId() {
        return R.id.resetPasswordFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = Objects.requireNonNull(getActivity()).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        subscribeApiCallStatusObservable();
    }

    private void initView(View view) {
        frameMain = view.findViewById(R.id.frameMain);
        inputMobileLayout = view.findViewById(R.id.inputMobileLayout);
        inputOTPLayout = view.findViewById(R.id.inputOTPLayout);
        inputPasswordLayout = view.findViewById(R.id.inputPasswordLayout);
        inputConfirmPasswordLayout = view.findViewById(R.id.inputConfirmPasswordLayout);
        btnUpdatePassword = view.findViewById(R.id.btnUpdatePassword);
        btnSendOtp = view.findViewById(R.id.btnSendOtp);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        edtOTP = view.findViewById(R.id.edtOTP);
        edtMobileNo = view.findViewById(R.id.edtMobileNo);

        handleView(false);

        ImageView imgBackButton = view.findViewById(R.id.imageBack);
        getCompositeDisposable().add(
                RxView.clicks(imgBackButton).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> backToPreviousFragment(R.id.loginFragment,
                        imgBackButton, false))
        );
        compositeDisposable.add(
                RxView.clicks(btnSendOtp).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> {
                    if (!AppUtils.isEmpty(edtMobileNo.getText().toString())) {
                        showProgressLoader();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("mobile", edtMobileNo.getText().toString());
                        loginViewModel.callToApi(params, LoginViewModel.OTP_SEND_API_TAG, true);
                    }
                })
        );
        compositeDisposable.add(
                RxView.clicks(btnUpdatePassword).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> {
                    if (!AppUtils.isEmpty(edtOTP.getText().toString())
                            && !AppUtils.isEmpty(edtPassword.getText().toString())) {
                        showProgressLoader();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("otp", edtOTP.getText().toString());
                        params.put("password", edtPassword.getText().toString());
                        params.put("conform_password", edtConfirmPassword.getText().toString());
                        loginViewModel.callToApi(params, LoginViewModel.OTP_SEND_API_TAG, true);
                    }
                })
        );
    }

    @Override
    public void onClick(View view) {

    }

    private void subscribeApiCallStatusObservable() {
        getCompositeDisposable().add(Observable.merge(loginViewModel.getStatusBehaviorRelay(),
                loginViewModel.getErrorRelay(),
                loginViewModel.getResponseRelay())
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
                                enableDisableView(frameMain, true);
                                hideProgressLoader();
                                if (pair.first.equals(LoginViewModel.OTP_SEND_API_TAG)) {
                                    handleView(true);
                                } else if (pair.first.equals(LoginViewModel.OTP_PASSWORD_UPDATE_API_TAG)) {
                                    backToPreviousFragment(R.id.loginFragment, frameMain, false);
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

    private void handleView(boolean isReceiveOTP) {
        inputMobileLayout.setVisibility(isReceiveOTP ? View.GONE : View.VISIBLE);
        inputOTPLayout.setVisibility(isReceiveOTP ? View.VISIBLE : View.GONE);
        inputPasswordLayout.setVisibility(isReceiveOTP ? View.VISIBLE : View.GONE);
        inputConfirmPasswordLayout.setVisibility(isReceiveOTP ? View.VISIBLE : View.GONE);
        btnSendOtp.setVisibility(isReceiveOTP ? View.GONE : View.VISIBLE);
        btnUpdatePassword.setVisibility(isReceiveOTP ? View.VISIBLE : View.GONE);
    }
}
