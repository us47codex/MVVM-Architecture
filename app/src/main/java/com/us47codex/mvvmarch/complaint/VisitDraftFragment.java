package com.us47codex.mvvmarch.complaint;

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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.base.BaseFragment;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppUtils;
import com.us47codex.mvvmarch.roomDatabase.VisitDraft;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

import static com.us47codex.mvvmarch.helper.AppUtils.toRequestBody;

/**
 * Created by Upendra Shah on 30 August, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/
public class VisitDraftFragment extends BaseFragment {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private AppCompatTextView txvNoRecordAvailable;
    private RecyclerView rcvComplaints;
    private VisitDraftAdapter visitDraftAdapter;
    private List<VisitDraft> complaintList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ComplaintViewModel complaintViewModel;
    private VisitDraft visitDraft;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_visit_draft;
    }

    @Override
    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.draft);
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
        return R.id.visitDraftFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = Objects.requireNonNull(getActivity()).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
        complaintViewModel = new ViewModelProvider(this).get(ComplaintViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visit_draft, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionBar(view);
        initView(view);
        showProgressLoader();
        getVisitDraftsFromDB();
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

        txvNoRecordAvailable = view.findViewById(R.id.txvNoRecordAvailable);
        rcvComplaints = view.findViewById(R.id.rcvComplaints);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        complaintList = new ArrayList<>();
        visitDraftAdapter = new VisitDraftAdapter(getContext(), complaintList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rcvComplaints.setLayoutManager(mLayoutManager);
        rcvComplaints.setItemAnimator(new DefaultItemAnimator());
        rcvComplaints.setAdapter(visitDraftAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::getVisitDraftsFromDB);

        visitDraftAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (visitDraftAdapter != null && txvNoRecordAvailable != null) {
                    if (visitDraftAdapter.getItemCount() == 0) {
                        txvNoRecordAvailable.setVisibility(View.VISIBLE);
                        rcvComplaints.setVisibility(View.GONE);
                    } else {
                        txvNoRecordAvailable.setVisibility(View.GONE);
                        rcvComplaints.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        visitDraftAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(View view, Object object) {
        super.onItemClick(view, object);
        switch (view.getId()) {
            case R.id.llComplaint:
                VisitDraft visitDraft = (VisitDraft) object;
                if (visitDraft.getId() != 0) {
                    if (visitDraft.getMcType().equalsIgnoreCase(Constants.BURNER)) {
                        if (visitDraft.getVisitType().equalsIgnoreCase(Constants.INSTALLATION_AND_COMMISSIONING)
                                || visitDraft.getVisitType().equalsIgnoreCase(Constants.PRE_INSTALLED)) {
//                            VisitBurnerInstallationFragment
                            complaintViewModel.callToApi(generateHashmapForBurnerInstallation(visitDraft), ComplaintViewModel.BURNER_INSTALLATION_COMPLAINT_VISIT_API_TAG, true);
                        } else {
//                            VisitBurnerServiceFragment
                            complaintViewModel.callToApi(generateHashmapForBurnerService(visitDraft), ComplaintViewModel.BURNER_SERVICE_COMPLAINT_VISIT_API_TAG, true);
                        }
                    } else {
//                        VisitOthersFragment
                        complaintViewModel.callToApi((generateHashmapForVisitOther(visitDraft)), ComplaintViewModel.HEAT_PUMP_COMPLAIN_VISIT_API_TAG, true);
                    }
                }
                break;
        }
    }

    private void getVisitDraftsFromDB() {
        getDatabase().visitDraftDao().getAllVisitDrafts()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<VisitDraft>>() {
                               @Override
                               public void onSuccess(List<VisitDraft> dbVisitDraftList) {
                                   hideProgressLoader();
                                   complaintList.clear();
                                   complaintList.addAll(dbVisitDraftList);
                                   visitDraftAdapter.notifyDataSetChanged();
                               }

                               @Override
                               public void onError(Throwable e) {
                                   hideProgressLoader();
                                   e.printStackTrace();
                               }
                           }
                );
    }

    private void removeVisitDraftFromDb(VisitDraft visitDraft) {
        getDatabase().visitDraftDao().deleteVisitDraft(visitDraft.getId())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        getVisitDraftsFromDB();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void subscribeApiCallStatusObservable() {
        getCompositeDisposable().add(Observable.merge(complaintViewModel.getStatusBehaviorRelay(),
                complaintViewModel.getErrorRelay(),
                complaintViewModel.getResponseRelay())
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
                            String msg = object + "\nVisit Report saved to draft. Please check in Draft";
                            showDialogWithSingleButtons(getContext(), getString(R.string.app_name),
                                    String.valueOf(errorCode), Objects.requireNonNull(getActivity()).getString(R.string.ok), (dialog, which) -> {
                                        enableDisableView(frameMain, true);
                                        backToPreviousFragment(R.id.complaintsFragment, frameMain, false);
                                    }, false);

                        } else if (object instanceof Pair) {
                            Pair pair = (Pair) object;
                            if (pair.first != null) {

                                enableDisableView(frameMain, true);
                                hideProgressLoader();
                                JSONObject jsonObject = (JSONObject) pair.second;
                                if (jsonObject != null && jsonObject.getInt(Constants.KEY_SUCCESS) == 1) {
                                    showDialogWithSingleButtons(getContext(), getString(R.string.app_name),
                                            "Report submitted successfully", Objects.requireNonNull(getActivity()).getString(R.string.ok), (dialog, which) -> {
                                                if (visitDraft != null) {
                                                    removeVisitDraftFromDb(visitDraft);
                                                    visitDraft = null;
                                                }
                                            }, false);
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

    private HashMap<String, RequestBody> generateHashmapForVisitOther(VisitDraft visitDraft) {
        HashMap<String, RequestBody> params = new HashMap<String, RequestBody>();
        try {
            HashMap<String, String> imageParams = stringToMapForImage(visitDraft.getVisitData());
            params = stringToMap(visitDraft.getVisitData());
            if (AppUtils.isEmpty(imageParams.get("sign_customer\"; filename=\"sign_customer.jpg\""))) {
                params.put("sign_customer\"; filename=\"sign_customer.jpg\"", toRequestBody(new File(imageParams.get("sign_customer\"; filename=\"sign_customer.jpg\""))));
            }
            if (AppUtils.isEmpty(imageParams.get("sign_repre\"; filename=\"sign_repre.jpg\""))) {
                params.put("sign_repre\"; filename=\"sign_repre.jpg\"", toRequestBody(new File(imageParams.get("sign_repre\"; filename=\"sign_repre.jpg\""))));
            }
            if (AppUtils.isEmpty(imageParams.get("sign_marketing\"; filename=\"sign_marketing.jpg\""))) {
                params.put("sign_marketing\"; filename=\"sign_marketing.jpg\"", toRequestBody(new File(imageParams.get("sign_marketing\"; filename=\"sign_marketing.jpg\""))));
            }
            return params;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    private HashMap<String, RequestBody> generateHashmapForBurnerService(VisitDraft
                                                                                 visitDraft) {
        HashMap<String, RequestBody> params = new HashMap<String, RequestBody>();
        try {
            HashMap<String, String> imageParams = stringToMapForImage(visitDraft.getVisitData());
            params = stringToMap(visitDraft.getVisitData());
            if (AppUtils.isEmpty(imageParams.get("sign_customer\"; filename=\"sign_customer.jpg\""))) {
                params.put("sign_customer\"; filename=\"sign_customer.jpg\"", toRequestBody(new File(imageParams.get("sign_customer\"; filename=\"sign_customer.jpg\""))));
            }
            if (AppUtils.isEmpty(imageParams.get("sign_repre\"; filename=\"sign_repre.jpg\""))) {
                params.put("sign_repre\"; filename=\"sign_repre.jpg\"", toRequestBody(new File(imageParams.get("sign_repre\"; filename=\"sign_repre.jpg\""))));
            }
            return params;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    private HashMap<String, RequestBody> generateHashmapForBurnerInstallation(VisitDraft
                                                                                      visitDraft) {
        HashMap<String, RequestBody> params = new HashMap<String, RequestBody>();
        try {
            HashMap<String, String> imageParams = stringToMapForImage(visitDraft.getVisitData());
            params = stringToMap(visitDraft.getVisitData());
            if (AppUtils.isEmpty(imageParams.get("sign_customer\"; filename=\"sign_customer.jpg\""))) {
                params.put("sign_customer\"; filename=\"sign_customer.jpg\"", toRequestBody(new File(imageParams.get("sign_customer\"; filename=\"sign_customer.jpg\""))));
            }
            if (AppUtils.isEmpty(imageParams.get("sign_repre\"; filename=\"sign_repre.jpg\""))) {
                params.put("sign_repre\"; filename=\"sign_repre.jpg\"", toRequestBody(new File(imageParams.get("sign_repre\"; filename=\"sign_repre.jpg\""))));
            }
            return params;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public HashMap<String, RequestBody> stringToMap(String t) throws JSONException {

        HashMap<String, RequestBody> map = new HashMap<String, RequestBody>();
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = jObject.getString(key);
            map.put(key, toRequestBody(value));
        }
        return map;
    }

    public HashMap<String, String> stringToMapForImage(String t) throws JSONException {

        HashMap<String, String> map = new HashMap<String, String>();
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = jObject.getString(key);
            if (key.equalsIgnoreCase("sign_customer\"; filename=\"sign_customer.jpg\"")
                    || key.equalsIgnoreCase("sign_marketing\"; filename=\"sign_marketing.jpg\"")
                    || key.equalsIgnoreCase("sign_repre\"; filename=\"sign_repre.jpg\"")) {
                map.put(key, value);
            }
        }
        return map;
    }
}

