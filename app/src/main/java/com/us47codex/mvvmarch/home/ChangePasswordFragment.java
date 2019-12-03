package com.us47codex.mvvmarch.home;

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
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.base.BaseFragment;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppUtils;

import org.json.JSONObject;

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
public class ChangePasswordFragment extends BaseFragment {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private AppCompatButton btnChangePassword;
    private TextInputEditText edtConfirmPassword, edtNewPassword, edtOldPassword;
    private TextInputLayout tilConfirmPassword, tilNewPassword, tilOldPassword;
    private HomeViewModel homeViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_change_password;
    }

    @Override
    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.change_password);
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
        return R.id.changePasswordFragment;
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
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionBar(view);
        initView(view);
        subscribeApiCallStatusObservable();
    }

    private void initActionBar(View view) {
        ImageView imgBackButton = view.findViewById(R.id.imageBack);
        compositeDisposable.add(
                RxView.clicks(imgBackButton).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> backToPreviousFragment(R.id.homeFragment,
                        imgBackButton, false))
        );
    }

    private void initView(View view) {
        frameMain = view.findViewById(R.id.frameMain);
        edtOldPassword = view.findViewById(R.id.edtOldPassword);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        tilOldPassword = view.findViewById(R.id.tilOldPassword);
        tilNewPassword = view.findViewById(R.id.tilNewPassword);
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        compositeDisposable.add(
                RxView.clicks(btnChangePassword).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> {
                    if (checkValidations()) {
                        showProgressLoader();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("old_password", edtOldPassword.getText().toString());
                        params.put("password", edtNewPassword.getText().toString());
                        params.put("coform_password", edtConfirmPassword.getText().toString());
                        homeViewModel.callToApi(params, HomeViewModel.CHANGE_PASSWORD_API_TAG, true);
                    }
                })
        );
    }

    private boolean checkValidations() {
        String newPassword = Objects.requireNonNull(edtNewPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(edtConfirmPassword.getText()).toString().trim();
        if (AppUtils.isEmpty(newPassword)) {
            tilNewPassword.setError(getString(R.string.password_length_must_be));
            return false;
        }
        if (AppUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.password_length_must_be));
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.password_must_be_same));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

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
                                if (pair.first.equals(HomeViewModel.CHANGE_PASSWORD_API_TAG)) {
                                    enableDisableView(frameMain, true);
                                    hideProgressLoader();
                                    JSONObject jsonObject = (JSONObject) pair.second;
                                    if (jsonObject != null && jsonObject.getInt(Constants.KEY_SUCCESS) == 1) {
                                        showDialogWithSingleButtons(getContext(), getString(R.string.app_name),
                                                Objects.requireNonNull(getActivity()).getString(R.string.password_updated_successfully), Objects.requireNonNull(getActivity()).getString(R.string.ok), (dialog, which) -> backToPreviousFragment(R.id.homeFragment, frameMain, false), false);
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
