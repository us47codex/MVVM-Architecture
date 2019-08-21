package com.us47codex.mvvmarch.SplashScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.base.BaseFragment;

import io.reactivex.disposables.CompositeDisposable;

public class SplashScreen extends BaseFragment {
    private static final String TAG = SplashScreen.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected CompositeDisposable getCompositeDisposable() {
        return null;
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
        return 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash_screen_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}

