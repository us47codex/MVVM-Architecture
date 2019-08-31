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
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppUtils;
import com.us47codex.mvvmarch.home.HomeViewModel;

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
public class UserProfileFragment extends BaseFragment {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private TextInputEditText edtPhoneNumber, edtMailId, edtLastName, edtMiddleName, edtFirstName, edtUsername, edtDepartment;
    private TextInputLayout txtPhoneInputLayout, txtEmailInputLayout, txtLastNameInputLayout, txtMiddleInputLayout, txtFirstNameInputLayout,
            txtUsernameInputLayout, txtDepartmentInputLayout;
    private AppCompatButton btnSaveProfile;
    private HomeViewModel homeViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_profile;
    }

    @Override
    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.profile);
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
        return R.id.userProfileFragment;
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
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionBar(view);
        initView(view);
        handleView(false);
        setData();
        subscribeApiCallStatusObservable();
    }

    private void initActionBar(View view) {
        ImageView imgBackButton = view.findViewById(R.id.imageBack);
        ImageView imageOne = view.findViewById(R.id.imageOne);
        imageOne.setVisibility(View.VISIBLE);
        imageOne.setImageDrawable(Objects.requireNonNull(getContext()).getDrawable(R.drawable.ic_profile_edit));

        compositeDisposable.add(
                RxView.clicks(imgBackButton).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> backToPreviousFragment(R.id.homeFragment,
                        imgBackButton, false))
        );

        compositeDisposable.add(
                RxView.clicks(imageOne).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    handleView(true);
                })
        );
    }

    private void initView(View view) {
        frameMain = view.findViewById(R.id.frameMain);

        txtPhoneInputLayout = view.findViewById(R.id.txtPhoneInputLayout);
        txtEmailInputLayout = view.findViewById(R.id.txtEmailInputLayout);
        txtLastNameInputLayout = view.findViewById(R.id.txtLastNameInputLayout);
        txtMiddleInputLayout = view.findViewById(R.id.txtMiddleInputLayout);
        txtFirstNameInputLayout = view.findViewById(R.id.txtFirstNameInputLayout);
        txtUsernameInputLayout = view.findViewById(R.id.txtUsernameInputLayout);
        txtDepartmentInputLayout = view.findViewById(R.id.txtDepartmentInputLayout);

        edtPhoneNumber = view.findViewById(R.id.edtPhoneNumber);
        edtMailId = view.findViewById(R.id.edtMailId);
        edtLastName = view.findViewById(R.id.edtLastName);
        edtMiddleName = view.findViewById(R.id.edtMiddleName);
        edtFirstName = view.findViewById(R.id.edtFirstName);
        edtFirstName = view.findViewById(R.id.edtFirstName);
        edtFirstName = view.findViewById(R.id.edtFirstName);
        edtUsername = view.findViewById(R.id.edtUsername);
        edtDepartment = view.findViewById(R.id.edtDepartment);

        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);

        compositeDisposable.add(
                RxView.clicks(btnSaveProfile).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    if (checkValidations()) {
                        showProgressLoader();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("first_name", edtFirstName.getText().toString());
                        params.put("middle_name", edtMiddleName.getEditableText().toString());
                        params.put("last_name", edtLastName.getEditableText().toString());
                        params.put("mno", edtPhoneNumber.getEditableText().toString());
                        params.put("username", edtUsername.getEditableText().toString());
                        params.put("department", edtDepartment.getEditableText().toString());
                        params.put("email", edtMailId.getEditableText().toString());
                        homeViewModel.callToApi(params, HomeViewModel.POST_PROFILE_API_TAG, true);
                    }
                })
        );
    }

    private void handleView(boolean allowEdit) {
        edtMailId.setEnabled(false);
        edtPhoneNumber.setEnabled(false);
        edtUsername.setEnabled(false);
        edtDepartment.setEnabled(false);

        edtFirstName.setEnabled(allowEdit);
        edtMiddleName.setEnabled(allowEdit);
        edtLastName.setEnabled(allowEdit);

        btnSaveProfile.setVisibility(allowEdit ? View.VISIBLE : View.GONE);
    }

    private void setData() {
        edtMailId.setText(getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_EMAIL, ""));
        edtFirstName.setText(getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_FIRST_NAME, ""));
        edtMiddleName.setText(getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_MIDDLE_NAME, ""));
        edtLastName.setText(getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_LAST_NAME, ""));
        edtPhoneNumber.setText(getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_MNO, ""));
        edtUsername.setText(getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_NAME, ""));
        edtDepartment.setText(getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_DEPARTMENT, ""));
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

    private boolean checkValidations() {
        if (AppUtils.isEmpty(edtFirstName.getText().toString())) {
            txtFirstNameInputLayout.setError(getString(R.string.war_enter_first_name));
            return false;
        }
        if (AppUtils.isEmpty(edtMiddleName.getText().toString())) {
            txtMiddleInputLayout.setError(getString(R.string.war_enter_middle_name));
            return false;
        }
        if (AppUtils.isEmpty(edtLastName.getText().toString())) {
            txtLastNameInputLayout.setError(getString(R.string.war_enter_last_name));
            return false;
        }
        if (AppUtils.isEmpty(edtPhoneNumber.getText().toString())) {
            txtPhoneInputLayout.setError(getString(R.string.war_enter_your_mobile));
            return false;
        }
        return true;
    }
}
