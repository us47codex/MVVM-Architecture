package com.us47codex.mvvmarch.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.SunTecApplication;
import com.us47codex.mvvmarch.SunTecPreferenceManager;
import com.us47codex.mvvmarch.roomDatabase.SunTecDatabase;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = BaseFragment.class.getSimpleName();
    private Toolbar toolbar;


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
}
