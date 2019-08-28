package com.us47codex.mvvmarch.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.NavigationDrawerAdapter;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.SunTecApplication;
import com.us47codex.mvvmarch.SunTecPreferenceManager;
import com.us47codex.mvvmarch.helper.AppUtils;
import com.us47codex.mvvmarch.interfaces.OnItemClickListener;
import com.us47codex.mvvmarch.models.NavDrawerModel;
import com.us47codex.mvvmarch.roomDatabase.SunTecDatabase;
import com.us47codex.mvvmarch.roomDatabase.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = BaseFragment.class.getSimpleName();
    private Toolbar toolbar;
    private ProgressBar loadingSpinner;
    private DrawerLayout mDrawerLayout;
    private FrameLayout frameMain;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private AppCompatTextView userRole, userName, txtSortName;
    private RecyclerView recyclerViewMenu;
    private final List<NavDrawerModel> navDrawerModelList = new ArrayList<>();

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
                RxView.clicks(linProfile).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> jumpToDestinationFragment(getCurrentFragmentId(),
                        R.id.toUserProfileFragment, frameMain, null, false))
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
                            userName.setText(String.format("%s %s", user.getFirst_name(), user.getLast_name()));
                            txtSortName.setText(String.format("%s%s", user.getFirst_name().substring(0, 1), user.getLast_name().substring(0, 1)));
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

    private void setUpRecycleView() {
        NavigationDrawerAdapter drawerAdapter = new NavigationDrawerAdapter(getContext(), navDrawerModelList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewMenu.setLayoutManager(mLayoutManager);
        recyclerViewMenu.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMenu.setAdapter(drawerAdapter);
        drawerAdapter.setOnClickListener(onItemClickListener);

        navDrawerModelList.clear();

        NavDrawerModel navItemAboutUs = new NavDrawerModel();
        navItemAboutUs.setTitle(getResources().getString(R.string.about_us));
        navItemAboutUs.setImgId(R.drawable.ic_aboutus);
        navDrawerModelList.add(navItemAboutUs);

        NavDrawerModel navItemChangePassword = new NavDrawerModel();
        navItemChangePassword.setTitle(getResources().getString(R.string.change_password));
        navItemChangePassword.setImgId(R.drawable.ic_change_password);
        navDrawerModelList.add(navItemChangePassword);

        NavDrawerModel navItemLogout = new NavDrawerModel();
        navItemLogout.setTitle(getResources().getString(R.string.logout));
        navItemLogout.setImgId(R.drawable.ic_logout);
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

            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        @Override
        public void onItemLongClick(View view, int pos, Object object) {

        }

        @Override
        public void onOtherItemSave(int id, View view, String value) {

        }
    };


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
}
