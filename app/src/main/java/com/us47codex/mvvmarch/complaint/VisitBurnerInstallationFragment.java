package com.us47codex.mvvmarch.complaint;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.SunTecApplication;
import com.us47codex.mvvmarch.SunTecPreferenceManager;
import com.us47codex.mvvmarch.base.BaseFragment;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppLog;
import com.us47codex.mvvmarch.helper.AppUtils;
import com.us47codex.mvvmarch.roomDatabase.Complaint;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

import static com.us47codex.mvvmarch.helper.AppUtils.toRequestBody;

/**
 * Created by Upendra Shah on 07 September, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/
public class VisitBurnerInstallationFragment extends BaseFragment {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private ComplaintViewModel complaintViewModel;
    private List<Complaint> complaintList;

    private TextInputEditText edtAttnBy, edtClient, edtAddress, edtQuantity, edtModel, edtCustomerAddress, edtContactPerson, edtCustomerName,
            edtReportNo, edtFuel, edtType, edtSerialNo, edtCode, edtEngineerRemark, edtCommissioningWorkDoneDescription,
            edtInstallationWorkDoneDescription, edtTotalAmount,
            edtProductJobKnowledge, edtCooperationWithYou, edtTimelyCompletion, edtSiteBehaviour, edtPresenceOfMind, edtEffectiveCommunication,
            edtCustomerRemarks, edtName, edtServiceCharge, edtTransport, edtConveyance, edtFoods, edtHotelBill, edtApplication;

    private TextInputLayout tilAttnBy, tilClient,
            tilAddress, tilQuantity, tilCustomerAddress, tilModel, tilContactPerson, tilCustomerName, tilDate, tilReportNo, tilEngineerRemark,
            tilCommissioningWorkDoneDescription, tilCommissioningDateEnd, tilCommissioningDateStart, tilInstallationWorkDoneDescription,
            tilInstallationDateEnd, tilInstallationDateStart, tilFuel, tilType, tilSerialNo, tilCode, tilHotelBill, tilConveyance,
            tilTransport, tilServiceCharge, tilName, tilCheckoutDateTime, tilCustomerRemarks, tilEffectiveCommunication, tilPresenceOfMind,
            tilSiteBehaviour, tilTimelyCompletion, tilCooperationWithYou, tilProductJobKnowledge, tilFoods;

    private AppCompatTextView txvMachineType, txvCommissioningDetail, txvBurnerDetail, txvCheckoutDateTime, txvInstallationDateEnd, txvCommissioningDateStart,
            txvCommissioningDateEnd, txvInstallationDateStart, txvDate, txvFinishDate;
    private RadioGroup rdgTraining;

    private AppCompatButton btnSubmitReport;
    private AppCompatImageView imgSignatureAndStamp, imgCustomerSign;
    private long complainId;
    private Complaint complaint;
    private boolean isSignatureCreate;
    private String SignatureFilePath = "";
    private AppCompatImageView signatureImage;

    private String customerFeedback, training = "no";
    private Dialog dialogWakeUpCall;

    private int CUSTOMER_SIGN_CODE = 0;
    private int SUNTEC_REPRE_SIGN_CODE = 1;

    private File customerSign;
    private File suntecRepreSign;

    private TextWatcher amountWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            setTotalAmount();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_visit_burner_installation;
    }

    @Override
    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.visit);
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
        return true;
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
        return R.id.visitBurnerInstallationFragment;
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
            complainId = bundle.getLong(Constants.KEY_COMPLAIN_ID, 0);
        }
        complaintViewModel = new ViewModelProvider(this).get(ComplaintViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visit_burner_installation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionBar(view);
        initView(view);
        getComplainFromDB();
        subscribeApiCallStatusObservable();
        if(isPermissionGranted(getContext())){
            getLocation(getContext());
        }else{
            requestLocationPermissions();
        }
    }

    private void initActionBar(View view) {
        ImageView imgBackButton = view.findViewById(R.id.imageBack);
        compositeDisposable.add(
                RxView.clicks(imgBackButton).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> backToPreviousFragment(R.id.complaintDetailsFragment,
                        imgBackButton, false))
        );
    }

    private void initView(View view) {
        frameMain = view.findViewById(R.id.frameMain);
        LinearLayoutCompat linSignature = view.findViewById(R.id.linSignature);

        txvMachineType = view.findViewById(R.id.txvMachineType);
        txvBurnerDetail = view.findViewById(R.id.txvBurnerDetail);
        txvCommissioningDetail = view.findViewById(R.id.txvCommissioningDetail);
        txvInstallationDateStart = view.findViewById(R.id.txvInstallationDateStart);
        txvCommissioningDateEnd = view.findViewById(R.id.txvCommissioningDateEnd);
        txvCheckoutDateTime = view.findViewById(R.id.txvCheckoutDateTime);
        txvCommissioningDateStart = view.findViewById(R.id.txvCommissioningDateStart);
        txvInstallationDateEnd = view.findViewById(R.id.txvInstallationDateEnd);
        txvDate = view.findViewById(R.id.txvDate);
        txvFinishDate = view.findViewById(R.id.txvFinishDate);

        edtAttnBy = view.findViewById(R.id.edtAttnBy);
        edtClient = view.findViewById(R.id.edtClient);
        edtAddress = view.findViewById(R.id.edtAddress);
        edtModel = view.findViewById(R.id.edtModel);
        edtQuantity = view.findViewById(R.id.edtQuantity);
        edtCustomerAddress = view.findViewById(R.id.edtCustomerAddress);
        edtContactPerson = view.findViewById(R.id.edtContactPerson);
        edtCustomerName = view.findViewById(R.id.edtCustomerName);
        edtReportNo = view.findViewById(R.id.edtReportNo);
        edtFuel = view.findViewById(R.id.edtFuel);
        edtType = view.findViewById(R.id.edtType);
        edtSerialNo = view.findViewById(R.id.edtSerialNo);
        edtCode = view.findViewById(R.id.edtCode);
        edtEngineerRemark = view.findViewById(R.id.edtEngineerRemark);
        edtCommissioningWorkDoneDescription = view.findViewById(R.id.edtCommissioningWorkDoneDescription);
        edtInstallationWorkDoneDescription = view.findViewById(R.id.edtInstallationWorkDoneDescription);
        edtProductJobKnowledge = view.findViewById(R.id.edtProductJobKnowledge);
        edtCooperationWithYou = view.findViewById(R.id.edtCooperationWithYou);
        edtTimelyCompletion = view.findViewById(R.id.edtTimelyCompletion);
        edtSiteBehaviour = view.findViewById(R.id.edtSiteBehaviour);
        edtPresenceOfMind = view.findViewById(R.id.edtPresenceOfMind);
        edtEffectiveCommunication = view.findViewById(R.id.edtEffectiveCommunication);
        edtCustomerRemarks = view.findViewById(R.id.edtCustomerRemarks);
        edtName = view.findViewById(R.id.edtName);
        edtServiceCharge = view.findViewById(R.id.edtServiceCharge);
        edtTransport = view.findViewById(R.id.edtTransport);
        edtConveyance = view.findViewById(R.id.edtConveyance);
        edtFoods = view.findViewById(R.id.edtFoods);
        edtHotelBill = view.findViewById(R.id.edtHotelBill);
        edtApplication = view.findViewById(R.id.edtApplication);
        edtTotalAmount = view.findViewById(R.id.edtTotalAmount);

        tilName = view.findViewById(R.id.tilName);

        imgSignatureAndStamp = view.findViewById(R.id.imgSignatureAndStamp);
        imgCustomerSign = view.findViewById(R.id.imgCustomerSign);
        rdgTraining = view.findViewById(R.id.rdgTraining);

        btnSubmitReport = view.findViewById(R.id.btnSubmitReport);

        getCompositeDisposable().add(
                RxView.clicks(imgCustomerSign).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    openSignatureDialog(CUSTOMER_SIGN_CODE);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(imgSignatureAndStamp).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    openSignatureDialog(SUNTEC_REPRE_SIGN_CODE);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(btnSubmitReport).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    submitReport();
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(txvCheckoutDateTime).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    showDialogSelectDate(txvCheckoutDateTime);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(txvInstallationDateStart).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    showDialogSelectDate(txvInstallationDateStart);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(txvInstallationDateEnd).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    showDialogSelectDate(txvInstallationDateEnd);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(txvCommissioningDateStart).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    showDialogSelectDate(txvCommissioningDateStart);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(txvCommissioningDateEnd).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    showDialogSelectDate(txvCommissioningDateEnd);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(txvDate).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    showDialogSelectDate(txvDate);
                })
        );

        rdgTraining.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbTrainingYes:
                        training = "Yes";
                        tilName.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rdbTrainingNo:
                        training = "No";
                        edtName.setText("");
                        tilName.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    private void submitReport() {
        if (isValidated()) {
            showProgressLoader();
            complaintViewModel.callToApi(prepareBurnerInstallationParams(), ComplaintViewModel.BURNER_INSTALLATION_COMPLAINT_VISIT_API_TAG, true);
        }
    }

    private void getReportNo() {
        if (complaint != null) {
            showProgressLoader();
            HashMap<String, String> params = new HashMap<>();
            params.put("mc_type", complaint.getMcType());
            complaintViewModel.callToApi(params, ComplaintViewModel.GET_REPORT_NO_API_TAG, true);
        } else {
            Toast.makeText(getContext(), "Complain is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData(Complaint complaint) {
        this.complaint = complaint;
        txvMachineType.setText(String.format("%s %s", complaint.getMcType(), AppUtils.isEmpty(complaint.getVisitType()) ? "" : ": " + complaint.getVisitType()));
//        edtCustomerName.setText(complaint.getCustomerLastName());


        if(!AppUtils.isEmpty(complaint.getAddress())){
            edtAddress.setText(complaint.getAddress());
        }else{
            edtAddress.setText("--");
        }

        if(!AppUtils.isEmpty(complaint.getCustomerFirstName())){
            edtClient.setText(complaint.getCustomerFirstName());
        }else{
            edtClient.setText("--");
        }

        if(!AppUtils.isEmpty(complaint.getCustomerLastName())){
            edtContactPerson.setText(complaint.getCustomerLastName());
        }else{
            edtContactPerson.setText("--");
        }

        if(!AppUtils.isEmpty(complaint.getMcModel())){
            edtModel.setText(complaint.getMcModel());
        }else{
            edtModel.setText("--");
        }

        if(!AppUtils.isEmpty(complaint.getSrNo())){
            edtSerialNo.setText(complaint.getSrNo());
        }else{
            edtSerialNo.setText("--");
        }
        if(!AppUtils.isEmpty(AppUtils.getCurrentDate())){
            txvDate.setText(AppUtils.getCurrentDate());
        }else{
            txvDate.setText("--");
        }

        if(!AppUtils.isEmpty(AppUtils.getCurrentDate())){
            txvFinishDate.setText(AppUtils.getCurrentDate());
        }else{
            txvFinishDate.setText("--");
        }

        if(!AppUtils.isEmpty(AppUtils.getCurrentDate())){
            txvInstallationDateEnd.setText(AppUtils.getCurrentDate());
        }else{
            txvInstallationDateEnd.setText("--");
        }

        if(!AppUtils.isEmpty(AppUtils.getCurrentDate())){
            txvCommissioningDateEnd.setText(AppUtils.getCurrentDate());
        }else{
            txvCommissioningDateEnd.setText("--");
        }

        if(!AppUtils.isEmpty(AppUtils.getCurrentDateTime())){
            txvCheckoutDateTime.setText(AppUtils.getCurrentDateTime());
        }else{
            txvCheckoutDateTime.setText(AppUtils.getCurrentDateTime());
        }


        edtAttnBy.setText(getPreference().getStringValue(SunTecPreferenceManager.PREF_USER_NAME, ""));






        edtModel.setClickable(false);
        edtSerialNo.setClickable(false);
        edtAttnBy.setClickable(false);
        //txvDate.setClickable(false);
        txvFinishDate.setClickable(false);
        edtTotalAmount.setClickable(false);
        txvInstallationDateEnd.setClickable(false);
        txvCommissioningDateEnd.setClickable(false);
        txvCheckoutDateTime.setClickable(false);
        txvCheckoutDateTime.setClickable(false);

        edtFoods.addTextChangedListener(amountWatcher);
        edtHotelBill.addTextChangedListener(amountWatcher);
        edtConveyance.addTextChangedListener(amountWatcher);
        edtTransport.addTextChangedListener(amountWatcher);
        edtServiceCharge.addTextChangedListener(amountWatcher);

        getReportNo();
    }

    private void setTotalAmount() {
        int totalAmount = Integer.parseInt(AppUtils.isEmpty(edtFoods.getText().toString()) ? "0" : edtFoods.getText().toString())
                + Integer.parseInt(AppUtils.isEmpty(edtHotelBill.getText().toString()) ? "0" : edtHotelBill.getText().toString())
                + Integer.parseInt(AppUtils.isEmpty(edtConveyance.getText().toString()) ? "0" : edtConveyance.getText().toString())
                + Integer.parseInt(AppUtils.isEmpty(edtTransport.getText().toString()) ? "0" : edtTransport.getText().toString())
                + Integer.parseInt(AppUtils.isEmpty(edtServiceCharge.getText().toString()) ? "0" : edtServiceCharge.getText().toString());
        edtTotalAmount.setText(String.valueOf(totalAmount));
    }

    private void setReportNo(String reportNo) {
        edtReportNo.setText(reportNo);
    }

    private void getComplainFromDB() {
        getDatabase().complaintDao().getComplaintById(complainId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableMaybeObserver<Complaint>() {
                               @Override
                               public void onSuccess(Complaint complaint) {
                                   setData(complaint);
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                               }

                               @Override
                               public void onComplete() {

                               }
                           }
                );
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
                            showDialogWithSingleButtons(getContext(), getString(R.string.app_name),
                                    String.valueOf(object), Objects.requireNonNull(getActivity()).getString(R.string.ok), (dialog, which) -> {
                                        enableDisableView(frameMain, true);
                                    }, false);

                        } else if (object instanceof Pair) {
                            Pair pair = (Pair) object;
                            if (pair.first != null) {
                                if (pair.first.equals(ComplaintViewModel.BURNER_INSTALLATION_COMPLAINT_VISIT_API_TAG)) {
                                    enableDisableView(frameMain, true);
                                    hideProgressLoader();
                                    JSONObject jsonObject = (JSONObject) pair.second;
                                    if (jsonObject != null && jsonObject.getInt(Constants.KEY_SUCCESS) == 1) {
                                        showDialogWithSingleButtons(getContext(), getString(R.string.app_name),
                                                "Report submitted successfully", Objects.requireNonNull(getActivity()).getString(R.string.ok), (dialog, which) -> {
                                                    backToPreviousFragment(R.id.complaintsFragment, frameMain, false);
                                                }, false);
                                    }
                                } else if (pair.first.equals(ComplaintViewModel.GET_REPORT_NO_API_TAG)) {
                                    enableDisableView(frameMain, true);
                                    hideProgressLoader();
                                    JSONObject jsonObject = (JSONObject) pair.second;
                                    if (jsonObject != null && jsonObject.getInt(Constants.KEY_SUCCESS) == 1) {
                                        setReportNo(jsonObject.optString("data"));
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

    private void openSignatureDialog(int type) {

        final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.setContentView(R.layout.layout_signature_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle(getString(R.string.signature));
        AppCompatImageView imgCancel = dialog.findViewById(R.id.imgCancel);
        AppCompatButton btnSubmit = dialog.findViewById(R.id.btnSubmit);
        AppCompatButton btnClear = dialog.findViewById(R.id.btnClear);
        SignaturePad mSignaturePad = dialog.findViewById(R.id.signature_pad);
        btnSubmit.setVisibility(View.INVISIBLE);
        btnClear.setVisibility(View.INVISIBLE);
        mSignaturePad.setBackgroundColor(Color.WHITE);

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }

            @Override
            public void onSigned() {
                btnSubmit.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                //btnClear.setEnabled(true);
                isSignatureCreate = true;
            }

            @Override
            public void onClear() {
                btnSubmit.setVisibility(View.INVISIBLE);
                btnClear.setVisibility(View.INVISIBLE);
                //btnClear.setEnabled(false);
                isSignatureCreate = false;
            }
        });

        imgCancel.setOnClickListener(view -> dialog.dismiss());

        btnClear.setOnClickListener(v -> {
            mSignaturePad.clear();
        });

        btnSubmit.setOnClickListener(v -> {
            if (mSignaturePad.getSignatureBitmap() != null) {
//                if (type == CUSTOMER_SIGN_CODE) {
//                    if (customerSign == null) {
//                        Toast.makeText(getContext(), "Please Signature here...", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                } else if (type == SUNTEC_REPRE_SIGN_CODE) {
//                    if (suntecRepreSign == null) {
//                        Toast.makeText(getContext(), "Please Signature here...", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                }
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                addJpgSignatureToGallery(signatureBitmap, type);

                RequestOptions requestOptions = new RequestOptions();

                if (type == CUSTOMER_SIGN_CODE) {
                    Glide.with(this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(signatureBitmap)
                            .into(imgCustomerSign);
                } else if (type == SUNTEC_REPRE_SIGN_CODE) {
                    Glide.with(this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(signatureBitmap)
                            .into(imgSignatureAndStamp);
                }
                dialog.dismiss();
            }
        });
        dialog.create();
        dialog.show();
    }

    @SuppressLint("DefaultLocale")
    private boolean addJpgSignatureToGallery(Bitmap signature, int type) {
        boolean result = false;
        try {
            File photo = new File(getContext().getCacheDir(), String.format("Signature_%d.jpg", System.currentTimeMillis()));
//            saveBitmapToJPG(signature, photo);
//            scanMediaFile(photo, type);
            if (type == CUSTOMER_SIGN_CODE) {
                customerSign = saveBitmapToJPG(signature, photo);

            } else if (type == SUNTEC_REPRE_SIGN_CODE) {
                suntecRepreSign = saveBitmapToJPG(signature, photo);
            }
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    private File getAlbumStorageDir() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SignaturePad");
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    private File saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
        return photo;
    }

    private void scanMediaFile(File photo, int type) {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri contentUri = Uri.fromFile(photo);
//        mediaScanIntent.setData(contentUri);
//
//        Uri imgUri = Uri.parse(String.valueOf(contentUri));
//        String path = AppUtils.getRealPathFromURI(Objects.requireNonNull(getContext()), imgUri);
//        SignatureFilePath = path;
//        String str_image1 = AppUtils.convertImageBase64(path);
//        if (type == CUSTOMER_SIGN_CODE) {
//            customerimageSignatureList.add(str_image1);
//        } else if (type == SUNTEC_REPRE_SIGN_CODE) {
//            signatureAndStampList.add(str_image1);
//        }
    }

    private HashMap<String, String> prepareBurnerInstallationParamsold() {
        // Complain Visit Burner
        //installation & commissionin
        //AND
        //Pre-installed


        HashMap<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(complaint.getId()));
        params.put("resolve_image", "");
//        params.put("sign_repre", covertBitmapToBase64(bitmapSignatureAndStamp));
//        params.put("sign_customer", covertBitmapToBase64(bitmapCustomerSign));
        params.put("battn_by", edtAttnBy.getText().toString());
        params.put("bclient", edtClient.getText().toString());
        params.put("badress", edtAddress.getText().toString());
        params.put("tax", edtFoods.getText().toString());
        params.put("others", edtHotelBill.getText().toString());
        params.put("to_form", edtTransport.getText().toString());
        params.put("conveyance", edtConveyance.getText().toString());
        params.put("services_charges", edtServiceCharge.getText().toString());
        params.put("training_given_by", edtName.getText().toString());
        params.put("training_given", training);
        params.put("checkout_date", txvCheckoutDateTime.getText().toString());
        params.put("customer_remark", edtCustomerRemarks.getText().toString());
        params.put("rate_communication", edtEffectiveCommunication.getText().toString());
        params.put("rate_problem", edtPresenceOfMind.getText().toString());
        params.put("rate_behaviour", edtSiteBehaviour.getText().toString());
        params.put("rate_work", edtTimelyCompletion.getText().toString());
        params.put("rate_cooperation", edtCooperationWithYou.getText().toString());
        params.put("rate_knowladge", edtProductJobKnowledge.getText().toString());
        params.put("crate", customerFeedback);
        params.put("engineer_remark", edtEngineerRemark.getText().toString());
        params.put("bcom_work_done", edtCommissioningWorkDoneDescription.getText().toString());
        params.put("com_end_date", txvCommissioningDateEnd.getText().toString());
        params.put("com_start_date", txvCommissioningDateStart.getText().toString());
        params.put("bin_work_des", edtInstallationWorkDoneDescription.getText().toString());
        params.put("in_end_date", txvInstallationDateEnd.getText().toString());
        params.put("in_start_date", txvInstallationDateStart.getText().toString());
        params.put("bfuel", edtFuel.getText().toString());
        params.put("btype", edtType.getText().toString());
        params.put("bsrno", edtSerialNo.getText().toString());
        params.put("bcode", edtCode.getText().toString());
        params.put("bquantity", edtQuantity.getText().toString());
        params.put("bmodel", edtModel.getText().toString());
        params.put("bapplication", edtApplication.getText().toString());
        params.put("contact_person", edtContactPerson.getText().toString());

        return params;

    }

    private HashMap<String, RequestBody> prepareBurnerInstallationParams() {
        // Complain Visit Burner
        //installation & commissionin
        //AND
        //Pre-installed


        HashMap<String, RequestBody> params = new HashMap<>();
        params.put("current_date", toRequestBody(txvDate.getText().toString()));
        params.put("id", toRequestBody(String.valueOf(complaint.getId())));
        params.put("report_no", toRequestBody(edtReportNo.getText().toString()));
        params.put("resolve_image", toRequestBody(""));
        params.put("sign_repre\"; filename=\"sign_repre.jpg\"", toRequestBody(suntecRepreSign));
        params.put("sign_customer\"; filename=\"sign_customer.jpg\"", toRequestBody(customerSign));
        params.put("battn_by", toRequestBody(edtAttnBy.getText().toString()));
        params.put("bclient", toRequestBody(edtClient.getText().toString()));
        params.put("badress", toRequestBody(edtAddress.getText().toString()));
        params.put("tax", toRequestBody(edtFoods.getText().toString()));
        params.put("others", toRequestBody(edtHotelBill.getText().toString()));
        params.put("to_form", toRequestBody(edtTransport.getText().toString()));
        params.put("conveyance", toRequestBody(edtConveyance.getText().toString()));
        params.put("services_charges", toRequestBody(edtServiceCharge.getText().toString()));
        params.put("training_given_by", toRequestBody(edtName.getText().toString()));
        params.put("training_given", toRequestBody(training));
        params.put("checkout_date", toRequestBody(txvCheckoutDateTime.getText().toString()));
        params.put("customer_remark", toRequestBody(edtCustomerRemarks.getText().toString()));
        params.put("rate_communication", toRequestBody(edtEffectiveCommunication.getText().toString()));
        params.put("rate_problem", toRequestBody(edtPresenceOfMind.getText().toString()));
        params.put("rate_behaviour", toRequestBody(edtSiteBehaviour.getText().toString()));
        params.put("rate_work", toRequestBody(edtTimelyCompletion.getText().toString()));
        params.put("rate_cooperation", toRequestBody(edtCooperationWithYou.getText().toString()));
        params.put("rate_knowladge", toRequestBody(edtProductJobKnowledge.getText().toString()));
        params.put("crate", toRequestBody(""));
        params.put("engineer_remark", toRequestBody(edtEngineerRemark.getText().toString()));
        params.put("bcom_work_done", toRequestBody(edtCommissioningWorkDoneDescription.getText().toString()));
        params.put("com_end_date", toRequestBody(txvCommissioningDateEnd.getText().toString()));
        params.put("com_start_date", toRequestBody(txvCommissioningDateStart.getText().toString()));
        params.put("bin_work_des", toRequestBody(edtInstallationWorkDoneDescription.getText().toString()));
        params.put("in_end_date", toRequestBody(txvInstallationDateEnd.getText().toString()));
        params.put("in_start_date", toRequestBody(txvInstallationDateStart.getText().toString()));
        params.put("bfuel", toRequestBody(edtFuel.getText().toString()));
        params.put("btype", toRequestBody(edtType.getText().toString()));
        params.put("bsrno", toRequestBody(edtSerialNo.getText().toString()));
        params.put("bcode", toRequestBody(edtCode.getText().toString()));
        params.put("bquantity", toRequestBody(edtQuantity.getText().toString()));
        params.put("bmodel", toRequestBody(edtModel.getText().toString()));
        params.put("bapplication", toRequestBody(edtApplication.getText().toString()));
        params.put("contact_person", toRequestBody(edtContactPerson.getText().toString()));
        params.put("out_lat", toRequestBody(String.valueOf(SunTecApplication.getInstance().latitude)));
        params.put("out_long", toRequestBody(String.valueOf(SunTecApplication.getInstance().longitude)));
        return params;

    }


    protected void showDialogSelectDate(AppCompatTextView appCompatTextView) {
        if (dialogWakeUpCall != null) {
            if (dialogWakeUpCall.isShowing()) {
                return;
            }
        }
        dialogWakeUpCall = new Dialog(getContext());
        dialogWakeUpCall.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setContentView(R.layout.profile_preference_two);
        dialogWakeUpCall.setContentView(R.layout.dialog_select_date);
//        Objects.requireNonNull(dialogWakeUpCall.getWindow()).setBackgroundDrawableResource(R.drawable.settings_bg_gray);
//        dialogWakeUpCall.getWindow().setLayout(Utils.getWidth(LandingPageActivity.this, 0.05), RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialogWakeUpCall.setCancelable(true);

        Button btn_send = dialogWakeUpCall.findViewById(R.id.btnSetSchedule);
        Button btn_cancel = dialogWakeUpCall.findViewById(R.id.btnCancel);
        TextView txt_dialog_title = dialogWakeUpCall.findViewById(R.id.txt_dialog_title);

        final SingleDateAndTimePicker wakeupDateAndTimePicker = dialogWakeUpCall.findViewById(R.id.single_day_picker);
        wakeupDateAndTimePicker.setIsAmPm(true);
        wakeupDateAndTimePicker.setStepMinutes(1);
        wakeupDateAndTimePicker.setDayFormatter(new SimpleDateFormat("EEE dd MMM", Locale.ENGLISH));

        Calendar minWakeUpDateTimeCalendar = Calendar.getInstance(Locale.ENGLISH);
        minWakeUpDateTimeCalendar.add(Calendar.MINUTE, 5);
        minWakeUpDateTimeCalendar.set(Calendar.SECOND, 0);
        wakeupDateAndTimePicker.setMinDate(minWakeUpDateTimeCalendar.getTime());

        btn_cancel.setOnClickListener(view -> {
            dialogWakeUpCall.dismiss();
            dialogWakeUpCall.cancel();
        });
        btn_send.setOnClickListener(view -> {
            dialogWakeUpCall.dismiss();
            dialogWakeUpCall.cancel();
            try {
//                showProgressLoader();
                appCompatTextView.setText(AppUtils.convertDateToString(wakeupDateAndTimePicker.getDate(), "MM/dd/yyyy"));

            } catch (Exception e) {
                AppLog.error("error", e);
            }
        });

        try {
            dialogWakeUpCall.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidated() {
        /*if (suntecRepreSign == null) {
            Toast.makeText(getContext(), "Please add Suntec Representative Signature", Toast.LENGTH_SHORT).show();
            return false;
        } else */
        if (customerSign == null) {
            Toast.makeText(getContext(), "Please add Customer Signature", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}


