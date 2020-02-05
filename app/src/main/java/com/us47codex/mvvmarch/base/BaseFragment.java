package com.us47codex.mvvmarch.base;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.SunTecApplication;
import com.us47codex.mvvmarch.SunTecPreferenceManager;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.helper.AppLog;
import com.us47codex.mvvmarch.helper.AppUtils;
import com.us47codex.mvvmarch.home.NavigationDrawerAdapter;
import com.us47codex.mvvmarch.interfaces.OnItemClickListener;
import com.us47codex.mvvmarch.models.NavDrawerModel;
import com.us47codex.mvvmarch.roomDatabase.SunTecDatabase;
import com.us47codex.mvvmarch.roomDatabase.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.LOCATION_SERVICE;

public abstract class BaseFragment extends Fragment implements View.OnClickListener, OnItemClickListener {
    private static final String TAG = BaseFragment.class.getSimpleName();
    private Toolbar toolbar;
    private ProgressBar loadingSpinner;
    private DrawerLayout mDrawerLayout;
    private FrameLayout frameMain;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private AppCompatTextView userRole, userName, txtSortName;
    private RecyclerView recyclerViewMenu;
    private final List<NavDrawerModel> navDrawerModelList = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    private final int ALL_PERMISSIONS_REQUEST_CODE = 1111;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private final String[] ALL_PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    protected abstract
    @LayoutRes
    int getLayoutId();

    protected abstract CompositeDisposable getCompositeDisposable();

    protected abstract String getToolbarTitle();

    protected abstract boolean shouldShowToolbar();

    protected abstract boolean shouldShowBackArrow();

    /**
     * @return here put your second tool bar image
     */
    protected abstract boolean shouldShowSecondImageIcon();

    /**
     * @return here put your first tool bar image
     */
    protected abstract boolean shouldShowFirstImageIcon();

    protected abstract boolean shouldLoaderImplement();

    protected abstract boolean shouldShowDrawer();

    protected abstract int getCurrentFragmentId();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getLayoutId() != 0) {
            initView(view);
        }
    }

    private void initView(View view) {
        frameMain = view.findViewById(R.id.frameMain);
        if (shouldLoaderImplement()) {
            loadingSpinner = view.findViewById(R.id.loadingSpinner);
        }
        if (shouldShowToolbar()) {
            AppCompatTextView txtToolBarTitle = view.findViewById(R.id.textToolbarTitle);
            ImageView imgBack = view.findViewById(R.id.imageBack);
            ImageView imageOne = view.findViewById(R.id.imageOne);
            ImageView imageTwo = view.findViewById(R.id.imageTwo);
            RelativeLayout relMainLayout = view.findViewById(R.id.relMainLayout);
            if (shouldShowFirstImageIcon()) {

            }

            if (shouldShowSecondImageIcon()) {
                imageTwo.setVisibility(View.VISIBLE);
            }

            if (!AppUtils.isEmpty(getToolbarTitle())) {
                if (getToolbarTitle().trim().contains(" ")) {
                    String[] splitStr = getToolbarTitle().split(" ");
                    try {
                        txtToolBarTitle.setText(String.format("%s%s %s%s", splitStr[0].substring(0, 1), splitStr[0].substring(1),
                                splitStr[1].substring(0, 1), splitStr[1].substring(1)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        txtToolBarTitle.setText(String.format("%s%s", getToolbarTitle().substring(0, 1), getToolbarTitle().substring(1)));
                    }
                } else {
                    txtToolBarTitle.setText(String.format("%s%s", getToolbarTitle().substring(0, 1), getToolbarTitle().substring(1)));
                }

                txtToolBarTitle.setGravity(Gravity.CENTER_VERTICAL);
                txtToolBarTitle.setAllCaps(false);
//                txtToolBarTitle.setTypeface(SunTecApplication.getInstance().getMontserratSemiBold());

                if (getCurrentFragmentId() == R.id.splashScreen ||
                        getCurrentFragmentId() == R.id.loginFragment) {

                    /*if (getCurrentFragmentId() == R.id.forgotPasswordFragment || getCurrentFragmentId() == R.id.signupFragment) {
                        relMainLayout.setElevation(5.0F);
                        relMainLayout.setTranslationZ(5.0F);
                    } else*/
                    if (getCurrentFragmentId() == R.id.homeFragment) {
                        txtToolBarTitle.setGravity(Gravity.CENTER);
//                        relMainLayout.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.landing_page));
                    }
                } else {
                    relMainLayout.setElevation(10.0F);
                    relMainLayout.setTranslationZ(1.0F);
                }
            }

            if (shouldShowBackArrow()) {
                imgBack.setVisibility(View.VISIBLE);
            } else {
                if (shouldShowDrawer()) {
                    toolbar = view.findViewById(R.id.toolbar);
                    mDrawerLayout = view.findViewById(R.id.drawer_layout);

                    initDrawerFragment(view);
                } else {
                    imgBack.setVisibility(View.GONE);
                }
            }
        }

        mSettingsClient = LocationServices.getSettingsClient(getContext());

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiverForLocationManagerChagne,
                new IntentFilter(Constants.SYSTEM_LOCATION_MANAGER_CHANGE));
    }

    private void initDrawerFragment(View view) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable;
        if (AppUtils.isDeviceSmartPhone(Objects.requireNonNull(getActivity()))) {
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, Objects.requireNonNull(getActivity()).getTheme());
        } else {
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_tablet, Objects.requireNonNull(getActivity()).getTheme());
        }


        toggle.setHomeAsUpIndicator(drawable);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.setToolbarNavigationClickListener(v -> drawerToggle());
        mDrawerLayout.post(toggle::syncState);

        userName = view.findViewById(R.id.userName);
        userRole = view.findViewById(R.id.userRole);
        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu);
        FrameLayout frameNameView = view.findViewById(R.id.frameNameView);
        txtSortName = view.findViewById(R.id.txtSortName);

        setUpRecycleView();

//        txtSortName.setTypeface(SunTecApplication.getInstance().getMontserratRegular());
//        userName.setTypeface(SunTecApplication.getInstance().getMontserratSemiBold());
//        userRole.setTypeface(SunTecApplication.getInstance().getMontserratMedium());

        AppCompatTextView txtVersion = view.findViewById(R.id.txtVersion);
//        txtVersion.setTypeface(SunTecApplication.getInstance().getMontserratMedium());

        LinearLayoutCompat linProfile = view.findViewById(R.id.linProfile);
        try {
            String currentVersion = Objects.requireNonNull(getActivity()).getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            txtVersion.setText(String.format("%s %s", getResources().getString(R.string.version), currentVersion));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        compositeDisposable.add(
                RxView.clicks(linProfile).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    jumpToDestinationFragment(getCurrentFragmentId(),
                            R.id.toUserProfileFragment, frameMain, null, false);
                })
        );

        compositeDisposable.add(
                SunTecApplication.getInstance().getDatabase().userDao().getUser()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorReturn(throwable -> {
                            throwable.printStackTrace();
                            return new User();
                        })
                        .doOnSuccess(user -> {
                            userName.setText(String.format("%s %s", getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_FIRST_NAME, "Please"), getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_LAST_NAME, "Login Again")));
                            txtSortName.setText(String.format("%s%s", getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_FIRST_NAME, "Please").substring(0, 1), getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_LAST_NAME, "Login Again").substring(0, 1)));
                        })
                        .doOnError(Throwable::printStackTrace)
                        .subscribe()
        );
    }

    private void drawerToggle() {
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    protected void backToPreviousFragment(int parentId, View view, boolean inclusive) {
        try {
            Navigation.findNavController(view).popBackStack(parentId, inclusive);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpRecycleView() {
        NavigationDrawerAdapter drawerAdapter = new NavigationDrawerAdapter(getContext(), navDrawerModelList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewMenu.setLayoutManager(mLayoutManager);
        recyclerViewMenu.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMenu.setAdapter(drawerAdapter);
        drawerAdapter.setOnClickListener(onItemClickListener);

        navDrawerModelList.clear();

        NavDrawerModel navItemChangePassword = new NavDrawerModel();
        navItemChangePassword.setId(Constants.CHANGE_PASSWORD_ID);
        navItemChangePassword.setTitle(getResources().getString(R.string.change_password));
        navItemChangePassword.setImgId(R.drawable.ic_change_password);
        navDrawerModelList.add(navItemChangePassword);

        NavDrawerModel navItemComplaints = new NavDrawerModel();
        navItemComplaints.setId(Constants.COMPLAINTS_ID);
        navItemComplaints.setTitle(getResources().getString(R.string.complaint));
        navItemComplaints.setImgId(R.drawable.ic_assignment);
        navDrawerModelList.add(navItemComplaints);

        NavDrawerModel navItemAboutUs = new NavDrawerModel();
        navItemAboutUs.setId(Constants.DRAFT_ID);
        navItemAboutUs.setTitle(getResources().getString(R.string.draft));
        navItemAboutUs.setImgId(R.drawable.ic_drafts);
        navDrawerModelList.add(navItemAboutUs);

        NavDrawerModel navItemLogout = new NavDrawerModel();
        navItemLogout.setId(Constants.LOGOUT_ID);
        navItemLogout.setTitle(getResources().getString(R.string.logout));
        navItemLogout.setImgId(R.drawable.ic_power);
        navDrawerModelList.add(navItemLogout);
        drawerAdapter.notifyDataSetChanged();

    }

    private final OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int pos) {

        }

        @Override
        public void onItemClick(View view, Object object) {

        }

        @Override
        public void onItemClick(View view, int pos, Object object) {
            NavDrawerModel navDrawerModel = (NavDrawerModel) object;

            if (navDrawerModel.getId() == Constants.LOGOUT_ID) {
                showDialogWithTwoButtons(BaseFragment.this.getActivity(), Objects.requireNonNull(getActivity()).getString(R.string.app_name), getActivity().getString(R.string.logout_warning_msg),
                        Objects.requireNonNull(getActivity()).getString(R.string.yes), getActivity().getString(R.string.cancel), (dialog, which) -> clearAllDataFromApp(), null, false);
            } else if (navDrawerModel.getId() == Constants.COMPLAINTS_ID) {
                jumpToDestinationFragment(getCurrentFragmentId(),
                        R.id.toComplaintsFragment, frameMain, null, false);
            } else if (navDrawerModel.getId() == Constants.CHANGE_PASSWORD_ID) {
                jumpToDestinationFragment(getCurrentFragmentId(),
                        R.id.toChangePasswordFragment, frameMain, null, false);
            } else if (navDrawerModel.getId() == Constants.DRAFT_ID) {
                jumpToDestinationFragment(getCurrentFragmentId(),
                        R.id.toVisitDraftFragment, frameMain, null, false);
            } else {
                showDialogWithSingleButtons(getContext(), getString(R.string.app_name),
                        Objects.requireNonNull(getActivity()).getString(R.string.coming_soon),
                        Objects.requireNonNull(getActivity()).getString(R.string.ok), (MaterialDialog dialog, DialogAction which) -> {
                        }, false);
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        @Override
        public void onItemLongClick(View view, int pos, Object object) {

        }

        @Override
        public void onOtherItemSave(int id, View view, String value) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!checkGPSISEnable()) {
            startLocationUpdates();
        }
    }

    protected void showDialogWithSingleButtons(Context context, String title, String msg, String positiveButtonName,
                                               MaterialDialog.SingleButtonCallback positiveButtonCallback, boolean cancelable) {
        try {
            new MaterialDialog.Builder(context)
                    .title(title)
                    .content(msg)
                    .positiveText(positiveButtonName)
                    .onPositive(positiveButtonCallback)
                    .cancelable(cancelable)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void enableDisableView(View view, boolean enabled) {
        if (view != null) {
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                for (int idx = 0; idx < group.getChildCount(); idx++) {
                    enableDisableView(group.getChildAt(idx), enabled);
                }
            }
        }
    }

    protected void jumpToDestinationFragment(int parentId, int destinationId, View view, Bundle bundle, boolean inclusive) {
        try {
            Navigation.findNavController(view).navigate(destinationId, bundle,
                    new NavOptions.Builder()
                            .setPopUpTo(parentId, inclusive).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getLayoutId() != 0) {
            hideKeyBoard(frameMain, getActivity());
        }
        compositeDisposable.clear();
        if (getCompositeDisposable() != null && getCompositeDisposable().size() > 0) {
            getCompositeDisposable().clear();
        }

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRegistrationBroadcastReceiverForLocationManagerChagne);
        System.gc();
    }

    public void hideKeyBoard(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View view) {

    }

    public SunTecDatabase getDatabase() {
        return SunTecApplication.getInstance().getDatabase();
    }

    public SunTecPreferenceManager getPreference() {
        return SunTecApplication.getInstance().getPreferenceManager();
    }

    protected void showProgressLoader() {
        if (shouldLoaderImplement()) {
            loadingSpinner.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressLoader() {
        if (shouldLoaderImplement()) {
            loadingSpinner.setVisibility(View.GONE);
        }
    }

    protected void showDialogWithTwoButtons(Context context, String title, String msg, String positiveButtonName,
                                            String NegativeButtonName,
                                            MaterialDialog.SingleButtonCallback positiveButtonCallback,
                                            MaterialDialog.SingleButtonCallback negativeButtonCallback, boolean cancelable) {

        try {
            new MaterialDialog.Builder(context)
                    .title(title)
                    .content(msg)
                    .positiveText(positiveButtonName)
                    .negativeText(NegativeButtonName)
                    .onPositive(positiveButtonCallback)
                    .onNegative(negativeButtonCallback)
                    .cancelable(cancelable)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void clearAllDataFromApp() {
        compositeDisposable.add(
                AppUtils.clearPreference()
                        .andThen(getDatabase().userDao().deleteAllUser())
                        .andThen(getDatabase().complaintDao().deleteAllComplaint())
                        .andThen(getDatabase().visitDraftDao().deleteAllVisitDrafts())
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {
                            WorkManager.getInstance(Objects.requireNonNull(getContext())).cancelAllWork();
                            jumpToDestinationFragment(getCurrentFragmentId(), R.id.toLoginFragment, frameMain, null, true);
                        })
                        .doOnError(Throwable::printStackTrace)
                        .subscribe()
        );
    }

    @Override
    public void onItemClick(int pos) {

    }

    @Override
    public void onItemClick(View view, Object object) {

    }

    @Override
    public void onItemClick(View view, int pos, Object object) {

    }

    @Override
    public void onItemLongClick(View view, int pos, Object object) {

    }

    @Override
    public void onOtherItemSave(int id, View view, String value) {

    }

    public boolean isPermissionGranted(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    public void getLocation(Context context) {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            mSettingsClient = LocationServices.getSettingsClient(context);
            mLocationCallback = new LocationCallback() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    mCurrentLocation = locationResult.getLastLocation();
                    makeUseOfNewLocation(context, mCurrentLocation);
                }
            };

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1);
            mLocationRequest.setFastestInterval(1);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            mLocationSettingsRequest = builder.build();
            startLocationUpdates(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void makeUseOfNewLocation(Context context, Location location) {
        if (location != null) {
            if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                if (!AppUtils.isEmpty(SunTecApplication.getInstance().getPreferenceManager().getStringValue(SunTecPreferenceManager.PREF_USER_ID, ""))) {

                    Log.e(TAG, "latitude >>: " + location.getLatitude());
                    Log.e(TAG, "longitude >>: " + location.getLongitude());

                    SunTecApplication.getInstance().latitude = location.getLatitude();
                    SunTecApplication.getInstance().longitude = location.getLongitude();

//                    Toast.makeText(context, "lat : long :: " + location.getLatitude() + " / " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                } else {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }

            } else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    return;
                }
            }
        } else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                return;
            }

        }

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void startLocationUpdates(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
    }

    public void requestLocationPermissions() {
        compositeDisposable.add(
                Completable.timer(1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.newThread())
                        .doOnError(Throwable::printStackTrace)
                        .doOnComplete(() -> {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                                return;
                            if (!hasPermissions()) {
                                ActivityCompat.requestPermissions(getActivity(), ALL_PERMISSIONS, ALL_PERMISSIONS_REQUEST_CODE);
                            } else {
                                getLocation(getContext());
                            }
                        })
                        .subscribe()
        );
    }

    public boolean hasPermissions() {
        if (getActivity() != null && ALL_PERMISSIONS != null) {
            for (String permission : ALL_PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private boolean checkGPSISEnable() {
        LocationManager service = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
//        if (!enabled) {
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
//        }
        return enabled;
    }

    public BroadcastReceiver mRegistrationBroadcastReceiverForLocationManagerChagne = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.SYSTEM_LOCATION_MANAGER_CHANGE)) {
                startLocationUpdates();
            } else {
                AppLog.error(TAG, "else  ");
            }
        }
    };

    private void startLocationUpdates() {
        showDialogWithTwoButtons(getContext(), getString(R.string.app_name), getString(R.string.location_str) + " " + getString(R.string.app_name), getString(R.string.cancel), getString(R.string.ok), new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                startLocationUpdates();
            }
        }, (dialog, which) -> {
            if (!checkGPSISEnable()) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
                dialog.dismiss();
            }
        }, true);
    }
}
