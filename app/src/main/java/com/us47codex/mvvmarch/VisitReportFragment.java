package com.us47codex.mvvmarch;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
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
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.base.BaseFragment;
import com.us47codex.mvvmarch.complaint.ComplaintViewModel;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.enums.ApiCallStatus;
import com.us47codex.mvvmarch.helper.AppUtils;
import com.us47codex.mvvmarch.roomDatabase.Complaint;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Upendra Shah on 07 September, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/
public class VisitReportFragment extends BaseFragment {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FrameLayout frameMain;
    private ComplaintViewModel complaintViewModel;
    private List<Complaint> complaintList;
    private TextInputEditText edtOEMDealerName, edtMonthYearOfInstallation, edtNoOfHWG, edtHWGModelSerialNo,
            edtHeatPumpModelSerialNo, edtContactPerson, edtCustomerName, edtDate, edtReportNo, edtPartsReplaced,
            edtCustomerRemark, edtSuggestionToCustomer, edtDescriptionOfWorkDone, edtObservation, edtNatureOfProblem,
            edtTypeOfCallReceiveDate, edtComplaintNoDate, edtPONoDate, edtEquipment, edtDealerPhoneNo, edtDealerAddress,
            edtDealerContactPerson, edtTax, edtConvayance, edtOthers, edtServiceCharge;
    private AppCompatButton btnSubmitReport;
    private AppCompatImageView imgMarketingProjectHead, imgSunteRepresentative, imgCustomerSign;
    private AppCompatTextView txvMachineType;
    private RadioGroup rdgQualityOfService, rdgWorkStatus;
    private long complainId;
    private Complaint complaint;
    private boolean isSignatureCreate;
    private String SignatureFilePath = "";
    private AppCompatImageView signatureImage;
    ArrayList<String> imgMarketingProjectHeadsignatureList = new ArrayList<>();
    ArrayList<String> customerimageSignatureList = new ArrayList<>();
    ArrayList<String> imgSunteRepresentativeSignatureList = new ArrayList<>();


    private int customerSignature = 0;
    private int imgSunteRepresentativeSignature = 1;
    private int imgMarketingProjectHeadsignature = 2;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_visit_report;
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
        return R.id.visitReportFragment;
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
        return inflater.inflate(R.layout.fragment_visit_report, container, false);
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

        edtOEMDealerName = view.findViewById(R.id.edtOEMDealerName);
        edtMonthYearOfInstallation = view.findViewById(R.id.edtMonthYearOfInstallation);
        edtNoOfHWG = view.findViewById(R.id.edtNoOfHWG);
        edtHWGModelSerialNo = view.findViewById(R.id.edtHWGModelSerialNo);
        edtHeatPumpModelSerialNo = view.findViewById(R.id.edtHeatPumpModelSerialNo);
        edtContactPerson = view.findViewById(R.id.edtContactPerson);
        edtCustomerName = view.findViewById(R.id.edtCustomerName);
        edtDate = view.findViewById(R.id.edtDate);
        edtReportNo = view.findViewById(R.id.edtReportNo);
        edtPartsReplaced = view.findViewById(R.id.edtPartsReplaced);
        edtCustomerRemark = view.findViewById(R.id.edtCustomerRemark);
        edtSuggestionToCustomer = view.findViewById(R.id.edtSuggestionToCustomer);
        edtDescriptionOfWorkDone = view.findViewById(R.id.edtDescriptionOfWorkDone);
        edtObservation = view.findViewById(R.id.edtObservation);
        edtNatureOfProblem = view.findViewById(R.id.edtNatureOfProblem);
        edtTypeOfCallReceiveDate = view.findViewById(R.id.edtTypeOfCallReceiveDate);
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
        signatureImage = view.findViewById(R.id.signatureImage);

        imgMarketingProjectHead = view.findViewById(R.id.imgMarketingProjectHead);
        imgSunteRepresentative = view.findViewById(R.id.imgSunteRepresentative);
        imgCustomerSign = view.findViewById(R.id.imgCustomerSign);

        btnSubmitReport = view.findViewById(R.id.btnSubmitReport);

        getCompositeDisposable().add(
                RxView.clicks(imgCustomerSign).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    openSignatureDialog(customerSignature);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(imgSunteRepresentative).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    openSignatureDialog(imgSunteRepresentativeSignature);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(imgMarketingProjectHead).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                    openSignatureDialog(imgMarketingProjectHeadsignature);
                })
        );
        getCompositeDisposable().add(
                RxView.clicks(btnSubmitReport).throttleFirst(500,
                        TimeUnit.MILLISECONDS).subscribe(o -> {
                })
        );
    }

    @Override
    public void onClick(View view) {

    }

    private void setData(Complaint complaint) {
        this.complaint = complaint;
        txvMachineType.setText(complaint.getMcType());
        edtCustomerName.setText(complaint.getCustomerFullName());
        edtComplaintNoDate.setText(complaint.getComplainNoDate());
        edtOEMDealerName.setText(complaint.getDealerName());
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
                                if (pair.first.equals(ComplaintViewModel.COMPLAINT_LIST_API_TAG)) {
                                    enableDisableView(frameMain, true);
                                    hideProgressLoader();
                                    JSONObject jsonObject = (JSONObject) pair.second;
                                    if (jsonObject != null && jsonObject.getInt(Constants.KEY_SUCCESS) == 1) {

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
        dialog.setTitle(getString(R.string.signature));
        AppCompatImageView imgCancel = dialog.findViewById(R.id.imgCancel);
        AppCompatButton btnSubmit = dialog.findViewById(R.id.btnSubmit);
        AppCompatButton btnClear = dialog.findViewById(R.id.btnClear);
        SignaturePad mSignaturePad = dialog.findViewById(R.id.signature_pad);
        btnSubmit.setVisibility(View.GONE);
        btnClear.setVisibility(View.GONE);
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
                btnSubmit.setVisibility(View.GONE);
                btnClear.setVisibility(View.GONE);
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
                if (type == 0) {
                    if (customerimageSignatureList == null) {
                        Toast.makeText(getContext(), "Please Signature here...", Toast.LENGTH_LONG).show();
                        return;
                    }
                } else if (type == 1) {
                    if (imgSunteRepresentativeSignatureList == null) {
                        Toast.makeText(getContext(), "Please Signature here...", Toast.LENGTH_LONG).show();
                        return;
                    }
                } else if (type == 3) {
                    if (imgMarketingProjectHeadsignatureList == null) {
                        Toast.makeText(getContext(), "Please Signature here...", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
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
            File photo = new File(getAlbumStorageDir(), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo, type);
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

    private void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    private void scanMediaFile(File photo, int type) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);

        Uri imgUri = Uri.parse(String.valueOf(contentUri));
        String path = AppUtils.getRealPathFromURI(Objects.requireNonNull(getContext()), imgUri);
        SignatureFilePath = path;
        String str_image1 = AppUtils.convertImageBase64(path);
        if (type == 0) {
            customerimageSignatureList.add(str_image1);
        } else if (type == 1) {
            imgSunteRepresentativeSignatureList.add(str_image1);
        } else if (type == 2) {
            imgMarketingProjectHeadsignatureList.add(str_image1);
        }
    }

    private void createParam() {
        //Complain Visit Heat Pump, HOT Water Generator, Dyare


        HashMap<String, String> params = new HashMap<>();
        params.put("id", edtCustomerName.getText().toString());
        params.put("resolve_image", edtCustomerName.getText().toString());
        params.put("sign_marketing", edtCustomerName.getText().toString());
        params.put("sign_repre", edtCustomerName.getText().toString());
        params.put("checkout_date", edtCustomerName.getText().toString());
        params.put("spare_replace", edtCustomerName.getText().toString());
        params.put("work_date", edtCustomerName.getText().toString());
        params.put("tax", edtCustomerName.getText().toString());
        params.put("others", edtCustomerName.getText().toString());
        params.put("to_form", edtCustomerName.getText().toString());
        params.put("conveyance", edtCustomerName.getText().toString());
        params.put("services_charges", edtCustomerName.getText().toString());
        params.put("quality_service", edtCustomerName.getText().toString());
        params.put("work_status", edtCustomerName.getText().toString());
        params.put("customer_remark", edtCustomerName.getText().toString());
        params.put("suggetion_customer", edtCustomerName.getText().toString());
        params.put("description_work", edtCustomerName.getText().toString());
        params.put("observation", edtCustomerName.getText().toString());
        params.put("nature_problem", edtCustomerName.getText().toString());
        params.put("courtesy_visit", edtCustomerName.getText().toString());
        params.put("warranty", edtCustomerName.getText().toString());
        params.put("chargeable", edtCustomerName.getText().toString());
        params.put("commissioning", edtCustomerName.getText().toString());
        params.put("installation", edtCustomerName.getText().toString());
        params.put("pre_installation", edtCustomerName.getText().toString());
        params.put("services", edtCustomerName.getText().toString());
        params.put("type_of_call", edtCustomerName.getText().toString());
        params.put("complain_no_date", edtCustomerName.getText().toString());
        params.put("po_no_date", edtCustomerName.getText().toString());
        params.put("equipment", edtCustomerName.getText().toString());
        params.put("d_mno", edtCustomerName.getText().toString());
        params.put("d_address", edtCustomerName.getText().toString());
        params.put("d_contact_person", edtCustomerName.getText().toString());
        params.put("oem_dealer_name", edtCustomerName.getText().toString());
        params.put("month_year_insta", edtCustomerName.getText().toString());
        params.put("no_of_hwg", edtCustomerName.getText().toString());
        params.put("hwg_sr_no", edtCustomerName.getText().toString());
        params.put("no_of_heat", edtCustomerName.getText().toString());
        params.put("heat_p_sr_no", edtCustomerName.getText().toString());
        params.put("contact_person", edtCustomerName.getText().toString());
        params.put("report_no", edtCustomerName.getText().toString());
    }

    private void prepareParams() {
        // Complain Visit Burner
        //installation & commissionin
        //AND
        //Pre-installed


        HashMap<String, String> params = new HashMap<>();
        params.put("id", edtCustomerName.getText().toString());
        params.put("resolve_image", edtCustomerName.getText().toString());
        params.put("sign_repre", edtCustomerName.getText().toString());
        params.put("sign_customer", edtCustomerName.getText().toString());
        params.put("tax", edtCustomerName.getText().toString());
        params.put("others", edtCustomerName.getText().toString());
        params.put("to_form", edtCustomerName.getText().toString());
        params.put("conveyance", edtCustomerName.getText().toString());
        params.put("services_charges", edtCustomerName.getText().toString());
        params.put("training_given_by", edtCustomerName.getText().toString());
        params.put("checkout_date", edtCustomerName.getText().toString());
        params.put("customer_remark", edtCustomerName.getText().toString());
        params.put("rate_communication", edtCustomerName.getText().toString());
        params.put("rate_problem", edtCustomerName.getText().toString());
        params.put("rate_behaviour", edtCustomerName.getText().toString());
        params.put("rate_work", edtCustomerName.getText().toString());
        params.put("rate_cooperation", edtCustomerName.getText().toString());
        params.put("rate_knowladge", edtCustomerName.getText().toString());
        params.put("crate", edtCustomerName.getText().toString());
        params.put("engineer_remark", edtCustomerName.getText().toString());
        params.put("bcom_work_done", edtCustomerName.getText().toString());
        params.put("com_end_date", edtCustomerName.getText().toString());
        params.put("com_start_date", edtCustomerName.getText().toString());
        params.put("bin_work_des", edtCustomerName.getText().toString());
        params.put("in_end_date", edtCustomerName.getText().toString());
        params.put("in_start_date", edtCustomerName.getText().toString());
        params.put("bfuel", edtCustomerName.getText().toString());
        params.put("btype", edtCustomerName.getText().toString());
        params.put("bsrno", edtCustomerName.getText().toString());
        params.put("bcode", edtCustomerName.getText().toString());
        params.put("bquantity", edtCustomerName.getText().toString());
        params.put("bmodel", edtCustomerName.getText().toString());
        params.put("bapplication", edtCustomerName.getText().toString());
        params.put("contact_person", edtCustomerName.getText().toString());

    }


    private void createParam1() {
//        Complain Visit Burner
//        Service/Breakdown
//        AND AMC

        HashMap<String, String> params = new HashMap<>();
        params.put("spare_replace", edtCustomerName.getText().toString());
        params.put("action_taken", edtCustomerName.getText().toString());
        params.put("root_of_cause", edtCustomerName.getText().toString());
        params.put("nature_problem", edtCustomerName.getText().toString());
        params.put("observation", edtCustomerName.getText().toString());
        params.put("bflame_safe", edtCustomerName.getText().toString());
        params.put("bcontrol_type", edtCustomerName.getText().toString());
        params.put("bflue_loss", edtCustomerName.getText().toString());
        params.put("bexcess_air", edtCustomerName.getText().toString());
        params.put("bcoppm", edtCustomerName.getText().toString());
        params.put("bco2", edtCustomerName.getText().toString());
        params.put("bo2", edtCustomerName.getText().toString());
        params.put("bservo_motor", edtCustomerName.getText().toString());
        params.put("bdamper_pos", edtCustomerName.getText().toString());
        params.put("id", edtCustomerName.getText().toString());
        params.put("resolve_image", edtCustomerName.getText().toString());
        params.put("sign_repre", edtCustomerName.getText().toString());
        params.put("sign_customer", edtCustomerName.getText().toString());
        params.put("tax", edtCustomerName.getText().toString());
        params.put("others", edtCustomerName.getText().toString());
        params.put("to_form", edtCustomerName.getText().toString());
        params.put("conveyance", edtCustomerName.getText().toString());
        params.put("services_charges", edtCustomerName.getText().toString());
        params.put("training_given_by", edtCustomerName.getText().toString());
        params.put("training_given", edtCustomerName.getText().toString());
        params.put("checkout_date", edtCustomerName.getText().toString());
        params.put("customer_remark", edtCustomerName.getText().toString());
        params.put("rate_communication", edtCustomerName.getText().toString());
        params.put("rate_problem", edtCustomerName.getText().toString());
        params.put("rate_behaviour", edtCustomerName.getText().toString());
        params.put("rate_work", edtCustomerName.getText().toString());
        params.put("rate_cooperation", edtCustomerName.getText().toString());
        params.put("rate_knowladge", edtCustomerName.getText().toString());
        params.put("crate", edtCustomerName.getText().toString());
        params.put("engineer_remark", edtCustomerName.getText().toString());
        params.put("bgas_pr_sw", edtCustomerName.getText().toString());
        params.put("bair_pr_sw", edtCustomerName.getText().toString());
        params.put("bgas_pre", edtCustomerName.getText().toString());
        params.put("bfg_temp", edtCustomerName.getText().toString());
        params.put("bair_temp", edtCustomerName.getText().toString());
        params.put("btemp_press", edtCustomerName.getText().toString());
        params.put("bfuel", edtCustomerName.getText().toString());
        params.put("btype", edtCustomerName.getText().toString());
        params.put("bsrno", edtCustomerName.getText().toString());
        params.put("bcode", edtCustomerName.getText().toString());
        params.put("bquantity", edtCustomerName.getText().toString());
        params.put("bmodel", edtCustomerName.getText().toString());
        params.put("bapplication", edtCustomerName.getText().toString());
        params.put("contact_person", edtCustomerName.getText().toString());

    }
}


