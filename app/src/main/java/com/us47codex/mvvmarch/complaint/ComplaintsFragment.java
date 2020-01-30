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
import com.us47codex.mvvmarch.roomDatabase.Complaint;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.us47codex.mvvmarch.constant.Constants.KEY_COMPLAIN_ID;

/**
 * Created by Upendra Shah on 30 August, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/
public class ComplaintsFragment extends BaseFragment {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private AppCompatTextView txvNoRecordAvailable;
    private RecyclerView rcvComplaints;
    private ComplaintsAdapter complaintsAdapter;
    private ComplaintViewModel complaintViewModel;
    private List<Complaint> complaintList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String FILTER_COMPLAINT = "all";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_complaints;
    }

    @Override
    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.complaint);
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
        return R.id.complaintsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = Objects.requireNonNull(getActivity()).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            FILTER_COMPLAINT = bundle.getString(Constants.KEY_FILTER_COMPLAINT, "all");
        }
        complaintViewModel = new ViewModelProvider(this).get(ComplaintViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_complaints, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionBar(view);
        initView(view);
        showProgressLoader();
        getCommentListFromServer();
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
        complaintsAdapter = new ComplaintsAdapter(getContext(), complaintList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rcvComplaints.setLayoutManager(mLayoutManager);
        rcvComplaints.setItemAnimator(new DefaultItemAnimator());
        rcvComplaints.setAdapter(complaintsAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::getCommentListFromServer);

        complaintsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (complaintsAdapter != null && txvNoRecordAvailable != null) {
                    if (complaintsAdapter.getItemCount() == 0) {
                        txvNoRecordAvailable.setVisibility(View.VISIBLE);
                        rcvComplaints.setVisibility(View.GONE);
                    } else {
                        txvNoRecordAvailable.setVisibility(View.GONE);
                        rcvComplaints.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        complaintsAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(View view, Object object) {
        super.onItemClick(view, object);
        switch (view.getId()) {
            case R.id.llComplaint:
                Complaint complaint = (Complaint) object;
                if (complaint.getId() != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(KEY_COMPLAIN_ID, complaint.getId());
                    jumpToDestinationFragment(getCurrentFragmentId(), R.id.toComplaintDetailsFragment, frameMain, bundle, false);
                    complaint = null;
                }
                break;
        }
    }

    private void getCommentListFromDB() {
        if (FILTER_COMPLAINT.equalsIgnoreCase(Constants.STATUS_ALL)) {
            getDatabase().complaintDao().getAllComplaints()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<List<Complaint>>() {
                                   @Override
                                   public void onSuccess(List<Complaint> dbComplaintList) {
                                       complaintList.clear();
                                       complaintList.addAll(dbComplaintList);
                                       complaintsAdapter.notifyDataSetChanged();
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       e.printStackTrace();
                                   }
                               }
                    );
        } else {
            getDatabase().complaintDao().getAllComplaintsByStatus(FILTER_COMPLAINT)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<List<Complaint>>() {
                                   @Override
                                   public void onSuccess(List<Complaint> dbComplaintList) {
                                       complaintList.clear();
                                       complaintList.addAll(dbComplaintList);
                                       complaintsAdapter.notifyDataSetChanged();
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       e.printStackTrace();
                                   }
                               }
                    );
        }
    }

    private void getCommentListFromServer() {
        complaintViewModel.callToApi(new HashMap<>(), ComplaintViewModel.COMPLAINT_LIST_API_TAG, true);
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
                        swipeRefreshLayout.setRefreshing(false);
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
                                if (pair.first.equals(ComplaintViewModel.COMPLAINT_LIST_API_TAG)) {
                                    enableDisableView(frameMain, true);
                                    hideProgressLoader();
                                    JSONObject jsonObject = (JSONObject) pair.second;
                                    if (jsonObject != null && jsonObject.getInt(Constants.KEY_SUCCESS) == 1) {
                                        getCommentListFromDB();
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
}

