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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.R;
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
import java.util.ArrayList;
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
public class VisitOthersFragment extends BaseFragment {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private ComplaintViewModel complaintViewModel;
    private List<Complaint> complaintList;
    private TextInputEditText edtOEMDealerName, edtMonthYearOfInstallation, edtNoOfHWG, edtHWGModelSerialNo,
            edtHeatPumpModelSerialNo, edtContactPerson, edtCustomerName, edtReportNo, edtPartsReplaced,
            edtCustomerRemark, edtSuggestionToCustomer, edtDescriptionOfWorkDone, edtObservation, edtNatureOfProblem,
            edtComplaintNoDate, edtPONoDate, edtEquipment, edtDealerPhoneNo, edtDealerAddress, edtNoOfHeatPumps,
            edtDealerContactPerson, edtTax, edtConvayance, edtOthers, edtServiceCharge, edtToFrom, edtRemark, edtReasonIncomplete,
            edtService, edtPreInstallation, edtInstallation, edtCommissioning, edtChargeable, edtWarranty, edtCourtesyVisit, edtCustomerAddress;
    private TextInputLayout tilService, tilPreInstallation, tilInstallation, tilCommissioning, tilChargeable, tilWarranty, tilCourtesyVisit, tilReasonIncomplete;
    private AppCompatButton btnSubmitReport;
    private AppCompatImageView imgMarketingProjectHead, imgSunteRepresentative, imgCustomerSign;
    private AppCompatTextView txvMachineType, txvDate, txvCheckoutDateTime, txvWorkDateTime;
    private RadioGroup rdgQualityOfService, rdgWorkStatus;
    private CheckBox chkCourtesyVisit, chkWarranty, chkChargeable, chkCommissioning, chkInstallation, chkPreInstallation, chkService;
    private long complainId;
    private Complaint complaint;
    private boolean isSignatureCreate;
    private String SignatureFilePath = "";
    private AppCompatImageView signatureImage;

    private Dialog dialogWakeUpCall;

    private File customerSign;
    private File suntecRepreSign;
    private File marketingProjectHeadSign;
    private int CUSTOMER_SIGN_CODE = 0;
    private int SUNTEC_REPRE_SIGN_CODE = 1;
    private int MARKETING_PROJECT_HEAD_SIGN_CODE = 2;
    private String workStatus = "Complete", qualityOfService = "Excellent";
    private ArrayList<String> typeOfCall = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_visit_others;
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
        return R.id.visitOthersFragment;
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
        complaintViewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visit_others, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionBar(view);
        initView(view);
        getComplainFromDB();
        subscribeApiCallStatusObservable();
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
        txvDate = view.findViewById(R.id.txvDate);
        txvCheckoutDateTime = view.findViewById(R.id.txvCheckoutDateTime);
        txvWorkDateTime = view.findViewById(R.id.txvWorkDateTime);

        edtOEMDealerName = view.findViewById(R.id.edtOEMDealerName);
        edtMonthYearOfInstallation = view.findViewById(R.id.edtMonthYearOfInstallation);
        edtNoOfHWG = view.findViewById(R.id.edtNoOfHWG);
        edtHWGModelSerialNo = view.findViewById(R.id.edtHWGModelSerialNo);
        edtHeatPumpModelSerialNo = view.findViewById(R.id.edtHeatPumpModelSerialNo);
        edtContactPerson = view.findViewById(R.id.edtContactPerson);
        edtCustomerName = view.findViewById(R.id.edtCustomerName);
        edtReportNo = view.findViewById(R.id.edtReportNo);
        edtPartsReplaced = view.findViewById(R.id.edtPartsReplaced);
        edtCustomerRemark = view.findViewById(R.id.edtCustomerRemark);
        edtSuggestionToCustomer = view.findViewById(R.id.edtSuggestionToCustomer);
        edtDescriptionOfWorkDone = view.findViewById(R.id.edtDescriptionOfWorkDone);
        edtObservation = view.findViewById(R.id.edtObservation);
        edtNatureOfProblem = view.findViewById(R.id.edtNatureOfProblem);
        edtComplaintNoDate = view.findViewById(R.id.edtComplaintNoDate);
        edtPONoDate = view.findViewById(R.id.edtPONoDate);
        edtEquipment = view.findViewById(R.id.edtEquipment);
        edtDealerPhoneNo = view.findViewById(R.id.edtDealerPhoneNo);
        edtDealerAddress = view.findViewById(R.id.edtDealerAddress);
        edtDealerContactPerson = view.findViewById(R.id.edtDealerContactPerson);
        edtTax = view.findViewById(R.id.edtTax);
        edtConvayance = view.findViewById(R.id.edtConvayance);
        edtOthers = view.findViewById(R.id.edtOthers);
        edtServiceCharge = view.findViewById(R.id.edtServiceCharge);
        edtToFrom = view.findViewById(R.id.edtToFrom);
        edtNoOfHeatPumps = view.findViewById(R.id.edtNoOfHeatPumps);
        edtRemark = view.findViewById(R.id.edtRemark);
        edtService = view.findViewById(R.id.edtService);
        edtPreInstallation = view.findViewById(R.id.edtPreInstallation);
        edtInstallation = view.findViewById(R.id.edtInstallation);
        edtCommissioning = view.findViewById(R.id.edtCommissioning);
        edtChargeable = view.findViewById(R.id.edtChargeable);
        edtWarranty = view.findViewById(R.id.edtWarranty);
        edtCourtesyVisit = view.findViewById(R.id.edtCourtesyVisit);
        edtReasonIncomplete = view.findViewById(R.id.edtReasonIncomplete);
        edtCustomerAddress = view.findViewById(R.id.edtCustomerAddress);

        tilService = view.findViewById(R.id.tilService);
        tilPreInstallation = view.findViewById(R.id.tilPreInstallation);
        tilInstallation = view.findViewById(R.id.tilInstallation);
        tilCommissioning = view.findViewById(R.id.tilCommissioning);
        tilChargeable = view.findViewById(R.id.tilChargeable);
        tilWarranty = view.findViewById(R.id.tilWarranty);
        tilCourtesyVisit = view.findViewById(R.id.tilCourtesyVisit);
        tilReasonIncomplete = view.findViewById(R.id.tilReasonIncomplete);

        chkCourtesyVisit = view.findViewById(R.id.chkCourtesyVisit);
        chkWarranty = view.findViewById(R.id.chkWarranty);
        chkChargeable = view.findViewById(R.id.chkChargeable);
        chkCommissioning = view.findViewById(R.id.chkCommissioning);
        chkInstallation = view.findViewById(R.id.chkInstallation);
        chkPreInstallation = view.findViewById(R.id.chkPreInstallation);
        chkService = view.findViewById(R.id.chkService);

        rdgWorkStatus = view.findViewById(R.id.rdgWorkStatus);
        rdgQualityOfService = view.findViewById(R.id.rdgQualityOfService);

        signatureImage = view.findViewById(R.id.signatureImage);

        imgMarketingProjectHead = view.findViewById(R.id.imgMarketingProjectHead);
        imgSunteRepresentative = view.findViewById(R.id.imgSunteRepresentative);
        imgCustomerSign = view.findViewById(R.id.imgCustomerSign);

        btnSubmitReport = view.findViewById(R.id.btnSubmitReport);
        chkCourtesyVisit.setOnCheckedChangeListener((compoundButton, b) -> {
            tilCourtesyVisit.setVisibility(b ? View.VISIBLE : View.GONE);
            if (b) typeOfCall.add("Courtesy Visit");
            else typeOfCall.remove("Courtesy Visit");
        });
        chkWarranty.setOnCheckedChangeListener((compoundButton, b) -> {
            tilWarranty.setVisibility(b ? View.VISIBLE : View.GONE);
            if (b) typeOfCall.add("warranty");
            else typeOfCall.remove("warranty");
        });
        chkChargeable.setOnCheckedChangeListener((compoundButton, b) -> {
            tilChargeable.setVisibility(b ? View.VISIBLE : View.GONE);
            if (b) typeOfCall.add("Chargeable");
            else typeOfCall.remove("Chargeable");
        });
        chkCommissioning.setOnCheckedChangeListener((compoundButton, b) -> {
            tilCommissioning.setVisibility(b ? View.VISIBLE : View.GONE);
            if (b) typeOfCall.add("Commissioning");
            else typeOfCall.remove("Commissioning");
        });
        chkInstallation.setOnCheckedChangeListener((compoundButton, b) -> {
            tilInstallation.setVisibility(b ? View.VISIBLE : View.GONE);
            if (b) typeOfCall.add("Installation");
            else typeOfCall.remove("Installation");
        });
        chkPreInstallation.setOnCheckedChangeListener((compoundButton, b) -> {
            tilPreInstallation.setVisibility(b ? View.VISIBLE : View.GONE);
            if (b) typeOfCall.add("Pre-Installation");
            else typeOfCall.remove("Pre-Installation");
        });
        chkService.setOnCheckedChangeListener((compoundButton, b) -> {
            tilService.setVisibility(b ? View.VISIBLE : View.GONE);
            if (b) typeOfCall.add("Service");
            else typeOfCall.remove("Service");
        });

        rdgWorkStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbComplete:
                        workStatus = "Complete";
                        tilReasonIncomplete.setVisibility(View.GONE);
                        break;
                    case R.id.rdbIncomplete:
                        workStatus = "Incompelete";
                        tilReasonIncomplete.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        rdgQualityOfService.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbExcellent:
                        qualityOfService = "Excellent";
                        break;
                    case R.id.rdbGood:
                        qualityOfService = "Good";
                        break;
                    case R.id.rdbPoor:
                        qualityOfService = "Poor";
                        break;
                }
            }
        });

        getCompositeDisposable().add(
                RxView.clicks(imgCustomerSign).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    openSignatureDialog(CUSTOMER_SIGN_CODE);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(imgSunteRepresentative).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    openSignatureDialog(SUNTEC_REPRE_SIGN_CODE);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(imgMarketingProjectHead).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    openSignatureDialog(MARKETING_PROJECT_HEAD_SIGN_CODE);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(txvDate).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    showDialogSelectDate(txvDate);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(txvCheckoutDateTime).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    showDialogSelectDate(txvCheckoutDateTime);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(txvWorkDateTime).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    showDialogSelectDate(txvWorkDateTime);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(btnSubmitReport).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    submitReport();
                })
        );
    }

    @Override
    public void onClick(View view) {

    }

    private void setData(Complaint complaint) {
        this.complaint = complaint;
        txvMachineType.setText(String.format("%s %s", complaint.getMcType(), AppUtils.isEmpty(complaint.getVisitType()) ? "" : ": " + complaint.getVisitType()));
        edtCustomerName.setText(complaint.getCustomerFirstName());
        edtContactPerson.setText(complaint.getCustomerLastName());
        edtCustomerAddress.setText(complaint.getAddress());
//        edtComplaintNoDate.setText(complaint.getComplainNoDate());
        edtOEMDealerName.setText(complaint.getDealerName());
        txvDate.setText(AppUtils.getCurrentDate());
        txvWorkDateTime.setText(AppUtils.getCurrentDateTime());
        txvCheckoutDateTime.setText(AppUtils.getCurrentDateTime());
        edtHeatPumpModelSerialNo.setText(String.format("%s %s", complaint.getMcModel(), complaint.getHeatPSrNo()));

        edtHeatPumpModelSerialNo.setClickable(false);
        txvDate.setClickable(false);
        txvWorkDateTime.setClickable(false);
        txvCheckoutDateTime.setClickable(false);
        getReportNo();
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
                                if (pair.first.equals(ComplaintViewModel.HEAT_PUMP_COMPLAIN_VISIT_API_TAG)) {
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
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                addJpgSignatureToGallery(signatureBitmap, type);

                RequestOptions requestOptions = new RequestOptions();

                if (type == 0) {
                    Glide.with(this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(signatureBitmap)
                            .into(imgCustomerSign);
                } else if (type == 1) {
                    Glide.with(this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(signatureBitmap)
                            .into(imgSunteRepresentative);
                } else if (type == 2) {
                    Glide.with(this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(signatureBitmap)
                            .into(imgMarketingProjectHead);
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
            } else if (type == MARKETING_PROJECT_HEAD_SIGN_CODE) {
                marketingProjectHeadSign = saveBitmapToJPG(signature, photo);
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
//        if (type == 0) {
//            customerimageSignatureList.add(str_image1);
//        } else if (type == 1) {
//            imgSunteRepresentativeSignatureList.add(str_image1);
//        } else if (type == 2) {
//            imgMarketingProjectHeadsignatureList.add(str_image1);
//        }
    }

    private void submitReport() {
        if (isValidated()) {
            showProgressLoader();
            complaintViewModel.callToApi(prepareParam(), ComplaintViewModel.HEAT_PUMP_COMPLAIN_VISIT_API_TAG, true);
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

    private HashMap<String, String> prepareParamOld() {
        //Complain Visit Heat Pump, HOT Water Generator, Dyare


        HashMap<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(complaint.getId()));
        params.put("resolve_image", "");
//        params.put("sign_customer", covertBitmapToBase64(bitmapCustomerSign));
//        params.put("sign_marketing", covertBitmapToBase64(bitmapMarketingProjectHead));
//        params.put("sign_repre", covertBitmapToBase64(bitmapSunteRepresentative));
        params.put("checkout_date", txvCheckoutDateTime.getText().toString());
        params.put("spare_replace", edtPartsReplaced.getText().toString());
        params.put("work_date", txvWorkDateTime.getText().toString());
        params.put("tax", edtTax.getText().toString());
        params.put("others", edtOthers.getText().toString());
        params.put("to_form", edtToFrom.getText().toString());
        params.put("conveyance", edtConvayance.getText().toString());
        params.put("services_charges", edtServiceCharge.getText().toString());
        params.put("quality_service", qualityOfService);
        params.put("work_status", workStatus);
        params.put("customer_remark", edtCustomerRemark.getText().toString());
        params.put("suggetion_customer", edtSuggestionToCustomer.getText().toString());
        params.put("description_work", edtDescriptionOfWorkDone.getText().toString());
        params.put("observation", edtObservation.getText().toString());
        params.put("nature_problem", edtNatureOfProblem.getText().toString());
        params.put("courtesy_visit", chkCourtesyVisit.isChecked() ? edtCourtesyVisit.getText().toString() : "");
        params.put("warranty", chkWarranty.isChecked() ? edtWarranty.getText().toString() : "");
        params.put("chargeable", chkChargeable.isChecked() ? edtChargeable.getText().toString() : "");
        params.put("commissioning", chkCommissioning.isChecked() ? edtCommissioning.getText().toString() : "");
        params.put("installation", chkInstallation.isChecked() ? edtInstallation.getText().toString() : "");
        params.put("pre_installation", chkPreInstallation.isChecked() ? edtPreInstallation.getText().toString() : "");
        params.put("services", chkService.isChecked() ? edtService.getText().toString() : "");
        params.put("type_of_call", typeOfCall.toString());
        params.put("complain_no_date", edtComplaintNoDate.getText().toString());
        params.put("po_no_date", edtPONoDate.getText().toString());
        params.put("equipment", edtEquipment.getText().toString());
        params.put("d_mno", edtDealerPhoneNo.getText().toString());
        params.put("d_address", edtDealerAddress.getText().toString());
        params.put("d_contact_person", edtDealerContactPerson.getText().toString());
        params.put("oem_dealer_name", edtOEMDealerName.getText().toString());
        params.put("month_year_insta", edtMonthYearOfInstallation.getText().toString());
        params.put("no_of_hwg", edtNoOfHWG.getText().toString());
        params.put("hwg_sr_no", edtHWGModelSerialNo.getText().toString());
        params.put("no_of_heat", edtNoOfHeatPumps.getText().toString());
        params.put("heat_p_sr_no", edtHeatPumpModelSerialNo.getText().toString());
        params.put("contact_person", edtContactPerson.getText().toString());
        params.put("report_no", edtReportNo.getText().toString());
        params.put("reason_incomplte", edtReasonIncomplete.getText().toString());

        return params;
    }

    private HashMap<String, RequestBody> prepareParam() {
        //Complain Visit Heat Pump, HOT Water Generator, Dyare


        HashMap<String, RequestBody> params = new HashMap<>();
        params.put("id", toRequestBody(String.valueOf(complaint.getId())));
        params.put("resolve_image", toRequestBody(""));
        params.put("sign_customer\"; filename=\"sign_customer.jpg\"", toRequestBody(customerSign));
//        params.put("sign_customer", toRequestBody(customerSign));
        params.put("sign_marketing\"; filename=\"sign_marketing.jpg\"", toRequestBody(marketingProjectHeadSign));
        params.put("sign_repre\"; filename=\"sign_repre.jpg\"", toRequestBody(suntecRepreSign));
        params.put("checkout_date", toRequestBody(txvCheckoutDateTime.getText().toString()));
        params.put("spare_replace", toRequestBody(edtPartsReplaced.getText().toString()));
        params.put("work_date", toRequestBody(txvWorkDateTime.getText().toString()));
        params.put("tax", toRequestBody(edtTax.getText().toString()));
        params.put("others", toRequestBody(edtOthers.getText().toString()));
        params.put("to_form", toRequestBody(edtToFrom.getText().toString()));
        params.put("conveyance", toRequestBody(edtConvayance.getText().toString()));
        params.put("services_charges", toRequestBody(edtServiceCharge.getText().toString()));
        params.put("quality_service", toRequestBody(qualityOfService));
        params.put("work_status", toRequestBody(workStatus));
        params.put("customer_remark", toRequestBody(edtCustomerRemark.getText().toString()));
        params.put("suggetion_customer", toRequestBody(edtSuggestionToCustomer.getText().toString()));
        params.put("description_work", toRequestBody(edtDescriptionOfWorkDone.getText().toString()));
        params.put("observation", toRequestBody(edtObservation.getText().toString()));
        params.put("nature_problem", toRequestBody(edtNatureOfProblem.getText().toString()));
        params.put("courtesy_visit", toRequestBody(chkCourtesyVisit.isChecked() ? edtCourtesyVisit.getText().toString() : ""));
        params.put("warranty", toRequestBody(chkWarranty.isChecked() ? edtWarranty.getText().toString() : ""));
        params.put("chargeable", toRequestBody(chkChargeable.isChecked() ? edtChargeable.getText().toString() : ""));
        params.put("commissioning", toRequestBody(chkCommissioning.isChecked() ? edtCommissioning.getText().toString() : ""));
        params.put("installation", toRequestBody(chkInstallation.isChecked() ? edtInstallation.getText().toString() : ""));
        params.put("pre_installation", toRequestBody(chkPreInstallation.isChecked() ? edtPreInstallation.getText().toString() : ""));
        params.put("services", toRequestBody(chkService.isChecked() ? edtService.getText().toString() : ""));
        params.put("type_of_call", toRequestBody(typeOfCall.toString().replace("[", "").replace("]", "")));
        params.put("complain_no_date", toRequestBody(edtComplaintNoDate.getText().toString()));
        params.put("po_no_date", toRequestBody(edtPONoDate.getText().toString()));
        params.put("equipment", toRequestBody(edtEquipment.getText().toString()));
        params.put("d_mno", toRequestBody(edtDealerPhoneNo.getText().toString()));
        params.put("d_address", toRequestBody(edtDealerAddress.getText().toString()));
        params.put("d_contact_person", toRequestBody(edtDealerContactPerson.getText().toString()));
        params.put("oem_dealer_name", toRequestBody(edtOEMDealerName.getText().toString()));
        params.put("month_year_insta", toRequestBody(edtMonthYearOfInstallation.getText().toString()));
        params.put("no_of_hwg", toRequestBody(edtNoOfHWG.getText().toString()));
        params.put("hwg_sr_no", toRequestBody(edtHWGModelSerialNo.getText().toString()));
        params.put("no_of_heat", toRequestBody(edtNoOfHeatPumps.getText().toString()));
        params.put("heat_p_sr_no", toRequestBody(edtHeatPumpModelSerialNo.getText().toString()));
        params.put("contact_person", toRequestBody(edtContactPerson.getText().toString()));
        params.put("report_no", toRequestBody(edtReportNo.getText().toString()));
        params.put("reason_incomplete", toRequestBody(edtReasonIncomplete.getText().toString()));
        params.put("out_long", toRequestBody(""));
        params.put("out_lat", toRequestBody(""));
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
        } else*/
        if (customerSign == null) {
            Toast.makeText(getContext(), "Please add Customer Signature", Toast.LENGTH_SHORT).show();
            return false;
        } /*else if (marketingProjectHeadSign == null) {
            Toast.makeText(getContext(), "Please add Marketing / Project Head Signature", Toast.LENGTH_SHORT).show();
            return false;
        } */ else {
            return true;
        }
    }
}


