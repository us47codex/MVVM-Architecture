package com.us47codex.mvvmarch.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.base.BaseFragment;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class LoginFragment extends BaseFragment {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private TextInputLayout inputEmailLayout, inputPasswordLayout;
    private TextInputEditText edtEmail, edtPassword;
    private LoginViewModel loginViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @Override
    protected String getToolbarTitle() {
        return null;
    }

    @Override
    protected boolean shouldShowToolbar() {
        return false;
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
        return false;
    }

    @Override
    protected int getCurrentFragmentId() {
        return R.id.loginFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        handleApiCAllStatusObservable();
    }

    @SuppressWarnings("unchecked")
    private void handleApiCAllStatusObservable() {
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
                                if (pair.first.equals(LoginViewModel.USER_LOGIN)) {
                                    enableDisableView(frameMain, true);
                                    hideProgressLoader();
                                    Toast.makeText(getContext(), "login success", Toast.LENGTH_LONG).show();
                                    jumpToDestinationFragment(getCurrentFragmentId(),R.id.toHomeFragment,frameMain,null,false);
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

    private void initView(View view) {
        frameMain = view.findViewById(R.id.frameMain);
        inputEmailLayout = view.findViewById(R.id.inputEmailLayout);
        inputPasswordLayout = view.findViewById(R.id.inputPasswordLayout);
        frameMain = view.findViewById(R.id.frameMain);

        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);

        AppCompatButton btnLogin = view.findViewById(R.id.btnLogin);

        getCompositeDisposable().add(
                RxView.clicks(btnLogin).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> {
                    if (checkValidations()) {
                        showProgressLoader();
                        String loginId = Objects.requireNonNull(edtEmail.getText()).toString().trim();
                        LoginParamModel loginParamModel = new LoginParamModel();
                        loginParamModel.username = Objects.requireNonNull(loginId);
                        loginParamModel.password = Objects.requireNonNull(edtPassword.getText()).toString().trim();
                        loginViewModel.callToApi(loginParamModel, LoginViewModel.USER_LOGIN, true);
                    }
                })
        );
    }


    private boolean checkValidations() {
        String loginId = Objects.requireNonNull(edtEmail.getText()).toString().trim();
        String loginPassword = Objects.requireNonNull(edtPassword.getText()).toString().trim();
        if (AppUtils.isEmpty(loginId)) {
            inputEmailLayout.setError(getString(R.string.war_enter_email));
            return false;
//        } else if (loginId.length() == 7 && !loginId.contains(".")) {
//            if (!AppUtils.isValidUserName(loginId)) {
//                inputEmailLayout.setError(getString(R.string.war_enter_user_name));
//                return false;
//            }
//        } else {
//            boolean isNumber;
//            try {
//                Long.parseLong(loginId);
//                isNumber = true;
//            } catch (NumberFormatException e) {
//                isNumber = false;
//            }
//            if (isNumber) {
//                if (AppUtils.isValidMobile(loginId)) {
//                    inputEmailLayout.setError(getString(R.string.war_enter_valid_mobile));
//                    return false;
//                }
//            } else if (AppUtils.isValidEmailId(loginId)) {
//                inputEmailLayout.setError(getString(R.string.war_enter_valid_email));
//                return false;
//            }
        }

        if (AppUtils.isEmpty(loginPassword)) {
            inputPasswordLayout.setError(getString(R.string.war_password_empty));
            return false;
        } else if (!(edtPassword.length() >= 4 && edtPassword.length() <= 16)) {
            inputPasswordLayout.setError(getString(R.string.password_length_must_be_four));
            return false;
        }
        return true;
    }
}
