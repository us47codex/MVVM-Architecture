package com.us47codex.mvvmarch;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;
import com.us47codex.mvvmarch.helper.AppLog;
import com.us47codex.mvvmarch.helper.PublishSubjectEvent;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private TextView txtNoConnection;
    private RelativeLayout relMain;

    private NavController navController;
    private NavGraph graph;
    private NavInflater navInflater;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        initView();
        //internetView();
    }

    private void initView() {
        relMain = findViewById(R.id.relMain);
        txtNoConnection = findViewById(R.id.txtNoConnection);

        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (navHost != null) {
            navController = navHost.getNavController();
        }
    }

    private void setAndClearGraph(){
        if (navController != null) {
            navController.getGraph().clear();
            if (graph != null)
                graph.clear();

            navInflater = navController.getNavInflater();
            graph = navInflater.inflate(R.navigation.navigation_graph);
            navController.setGraph(graph);
        }
    }

    private void internetView() {
        compositeDisposable.add(PublishSubjectEvent.getInstance().internetStatusRelay
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    AppLog.error(TAG, "onCreate: " + throwable.getLocalizedMessage());
                    return false;
                })
                //.filter(jsonObject -> jsonObject != null)
                .doOnError(Throwable::printStackTrace)
                .doOnNext(aBoolean -> {
                    if (aBoolean) {
                        if (txtNoConnection.getVisibility() == View.VISIBLE) {
                            showInternetView(true, txtNoConnection, relMain, this, getString(R.string.back_to_online));
                        }
                    } else {
                        if (txtNoConnection.getVisibility() == View.GONE) {
                            showInternetView(false, txtNoConnection, relMain, this, getString(R.string.no_internet_connection));
                        }
                    }
                })
                .subscribe());
    }

    private void showInternetView(boolean internetStatus, TextView textView, View topView, Context context, String value) {
        if (internetStatus) {
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            textView.setText(value);
            Completable.timer(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(Throwable::printStackTrace)
                    .doOnComplete(() -> {
                        TransitionSet set = new TransitionSet()
                                .addTransition(new Scale(0.7f))
                                .addTransition(new Fade())
                                .setInterpolator(new LinearOutSlowInInterpolator());
                        TransitionManager.beginDelayedTransition((ViewGroup) topView, set);
                        textView.setVisibility(View.GONE);
                    })
                    .subscribe();
        } else {
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.maroon));

            textView.setText(value);
            TransitionSet set = new TransitionSet()
                    .addTransition(new Scale(0.7f))
                    .addTransition(new Fade())
                    .setInterpolator(new LinearOutSlowInInterpolator());
            TransitionManager.beginDelayedTransition((ViewGroup) topView, set);
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        System.gc();
    }
}
