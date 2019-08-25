package com.us47codex.mvvmarch.login;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.base.BaseFragment;
import com.us47codex.mvvmarch.helper.AppUtils;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;

public class LoginFragment extends BaseFragment {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private TextInputLayout inputEmailLayout, inputPasswordLayout;
    private TextInputEditText edtEmail, edtPassword;
    private AppCompatButton btnLogin;
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
        return false;
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        inputEmailLayout = view.findViewById(R.id.inputEmailLayout);
        inputPasswordLayout = view.findViewById(R.id.inputPasswordLayout);

        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);

        btnLogin = view.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {
            if (checkValidations()) {
                String loginId = Objects.requireNonNull(edtEmail.getText()).toString().trim();
                LoginParamModel loginParamModel = new LoginParamModel();
//                if (AppUtils.isValidUserName(loginId))
//                    loginParamModel.username = Objects.requireNonNull(edtEmail.getText()).toString().trim().toLowerCase();
//                else
                loginParamModel.username = Objects.requireNonNull(edtEmail.getText()).toString().trim().toLowerCase();
                loginParamModel.password = Objects.requireNonNull(edtPassword.getText()).toString().trim();
                loginViewModel.callToApi(loginParamModel, LoginViewModel.USER_LOGIN, true);
            }
        }
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
