package com.us47codex.mvvmarch.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.SunTecPreferenceManager;
import com.us47codex.mvvmarch.base.BaseFragment;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppLog;
import com.us47codex.mvvmarch.helper.AppUtils;
import com.us47codex.mvvmarch.roomDatabase.User;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

import static com.us47codex.mvvmarch.helper.AppUtils.toRequestBody;

/**
 * Created by Upendra Shah on 30 August, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/
public class UserProfileFragment extends BaseFragment {
    private static final String TAG = UserProfileFragment.class.getSimpleName();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private TextInputEditText edtPhoneNumber, edtMailId, edtLastName, edtMiddleName, edtFirstName, edtUsername, edtDepartment;
    private TextInputLayout txtPhoneInputLayout, txtEmailInputLayout, txtLastNameInputLayout, txtMiddleInputLayout, txtFirstNameInputLayout,
            txtUsernameInputLayout, txtDepartmentInputLayout;
    private CircleImageView imgUserPic;
    private ProgressBar loadingSpinner;
    private AppCompatButton btnSaveProfile;
    private HomeViewModel homeViewModel;

    private final int ALL_PERMISSIONS_REQUEST_CODE = 1111;
    private final String[] ALL_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private static final int REQUEST_CAMERA = 1001;
    private static final int REQUEST_GALLERY = 1002;
    private String realPath = "";
    private Bitmap mBitmap;
    private String imageUrl = "";
    private File pictureFile;

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
        return true;
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
        imgUserPic = view.findViewById(R.id.imgUserPic);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);

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
                        HashMap<String, RequestBody> params = new HashMap<>();
                        params.put("id", toRequestBody(getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_ID, "")));
                        params.put("first_name", toRequestBody(edtFirstName.getEditableText().toString()));
                        params.put("middle_name", toRequestBody(edtMiddleName.getEditableText().toString()));
                        params.put("last_name", toRequestBody(edtLastName.getEditableText().toString()));
                        params.put("mno", toRequestBody(edtPhoneNumber.getEditableText().toString()));
                        params.put("username", toRequestBody(edtUsername.getEditableText().toString()));
                        params.put("department", toRequestBody(edtDepartment.getEditableText().toString()));
                        params.put("email", toRequestBody(edtMailId.getEditableText().toString()));
                        if (pictureFile != null)
                            params.put("profile\"; filename=\"profile.jpg\"", toRequestBody(pictureFile));
                        homeViewModel.callToApi(params, HomeViewModel.POST_PROFILE_API_TAG, true);
                    }
                })
        );
        compositeDisposable.add(
                RxView.clicks(imgUserPic).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    requestAllPermissions();
                })
        );
    }

    private void handleView(boolean allowEdit) {
        edtMailId.setEnabled(false);
        edtUsername.setEnabled(false);
        edtDepartment.setEnabled(false);

        edtPhoneNumber.setEnabled(allowEdit);
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

        Glide.with(this)
                .setDefaultRequestOptions(new RequestOptions().error(R.drawable.ic_place_holder).placeholder(R.drawable.ic_place_holder))
                .load(getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_PROFILE, ""))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@io.reactivex.annotations.Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                        e.printStackTrace();
                        loadingSpinner.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loadingSpinner.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imgUserPic);
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

    private void requestAllPermissions() {
        compositeDisposable.add(
                Completable.timer(1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.newThread())
                        .doOnError(Throwable::printStackTrace)
                        .doOnComplete(() -> {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                                return;
                            if (!hasPermissions(getActivity(), ALL_PERMISSIONS)) {
                                ActivityCompat.requestPermissions(getActivity(), ALL_PERMISSIONS,
                                        ALL_PERMISSIONS_REQUEST_CODE);
                            } else {
                                initCameraIntent();
                            }
                        })
                        .subscribe()
        );
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == ALL_PERMISSIONS_REQUEST_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    AppLog.error(TAG, permissions[i] + " :: PERMISSION_GRANTED");
                } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (!hasPermissions(getActivity(), ALL_PERMISSIONS)) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{permissions[i]},
                                ALL_PERMISSIONS_REQUEST_CODE);
                    }
                }

            }

            if (hasPermissions(getActivity(), ALL_PERMISSIONS)) {
                initCameraIntent();
            }
        }
    }

    private void initCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(getActivity(), "Photo file can't be created, please try again", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri selectedImageUri = FileProvider.getUriForFile(getContext(), Objects.requireNonNull(getContext()).getPackageName() +
                        ".provider", pictureFile);


                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            }
        }
    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH).format(new Date());
        String pictureFile = Objects.requireNonNull(getActivity()).getString(R.string.app_name) + timeStamp;
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile, ".jpg", storageDir);
        realPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File imgFile = new File(realPath);
                if (imgFile.exists()) {
                    imgUserPic.setImageURI(Uri.fromFile(imgFile));
                    loadImage(imgFile);
                }
            }
        }
    }

    private void loadImage(File file) {
        AppLog.error(TAG, "file :" + file.getAbsolutePath());
        if (file.exists()) {
            Bitmap myBitmap;

            getCompositeDisposable().add(
                    getDatabase().userDao().getUser()
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorReturn(throwable -> {
                                throwable.printStackTrace();
                                return new User();
                            })
                            .doOnSuccess(user -> {
                                user.setProfile(file.getAbsolutePath());
                                getDatabase().userDao().insertUser(user);
                            })
                            .doOnError(Throwable::printStackTrace)
                            .subscribe()
            );

            myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_place_holder);
            requestOptions.error(R.drawable.ic_place_holder);

            try {
                Glide.with(Objects.requireNonNull(getContext()))
                        .setDefaultRequestOptions(requestOptions)
                        .load(myBitmap)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@io.reactivex.annotations.Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                                loadingSpinner.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                loadingSpinner.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(imgUserPic);
            } catch (Exception e) {
                e.printStackTrace();
                loadingSpinner.setVisibility(View.GONE);
            }
        }
    }

}
