package com.us47codex.mvvmarch.roomDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Upendra Shah on 04 September, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/

@Entity(tableName = "tbl_complaints")
public class Complaint {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "customer_name")
    private String customerName;

    @ColumnInfo(name = "mno")
    private String mno;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "mc_type")
    private String mcType;

    @ColumnInfo(name = "mc_model")
    private String mcModel;

    @ColumnInfo(name = "sr_no")
    private String srNo;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "alternate_num")
    private String alternateNum;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "dealer_name")
    private String dealerName;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "complain_form")
    private String complainForm;

    @ColumnInfo(name = "complain_to")
    private String complainTo;

    @ColumnInfo(name = "assign_date")
    private String assignDate;

    @ColumnInfo(name = "priority")
    private String priority;

    @ColumnInfo(name = "payment_customer")
    private String paymentCustomer;

    @ColumnInfo(name = "hold")
    private String hold;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "created_at")
    private String createdAt;

    @ColumnInfo(name = "updated_at")
    private String updatedAt;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "schedule_date")
    private String scheduleDate;

    @ColumnInfo(name = "observation")
    private String observation;

    @ColumnInfo(name = "work_date")
    private String workDate;

    @ColumnInfo(name = "spare_replace")
    private String spareReplace;

    @ColumnInfo(name = "checkout_date")
    private String checkoutDate;

    @ColumnInfo(name = "resolve_image")
    private String resolveImage;

    @ColumnInfo(name = "resolve_date")
    private String resolveDate;

    // customer last name :: Ashok
    @ColumnInfo(name = "customer_full_name")
    private String customerFullName;

    @ColumnInfo(name = "complain_to_full_name")
    private String complainToFullName;

    @ColumnInfo(name = "report_no")
    private String reportNo;

    @ColumnInfo(name = "contact_person")
    private String contactPerson;

    @ColumnInfo(name = "heat_p_sr_no")
    private String heatPSrNo;

    @ColumnInfo(name = "no_of_heat")
    private String noOfHeat;

    @ColumnInfo(name = "hwg_sr_no")
    private String hwgSrNo;

    @ColumnInfo(name = "no_of_hwg")
    private String noOfHwg;

    @ColumnInfo(name = "month_year_insta")
    private String monthYearInsta;

    @ColumnInfo(name = "oem_dealer_name")
    private String oemDealerName;

    @ColumnInfo(name = "d_contact_person")
    private String dContactPerson;

    @ColumnInfo(name = "d_address")
    private String dAddress;

    @ColumnInfo(name = "d_mno")
    private String dMno;

    @ColumnInfo(name = "equipment")
    private String equipment;

    @ColumnInfo(name = "po_no_date")
    private String poNoDate;

    @ColumnInfo(name = "complain_no_date")
    private String complainNoDate;

    @ColumnInfo(name = "type_of_call")
    private String typeOfCall;

    @ColumnInfo(name = "services")
    private String services;

    @ColumnInfo(name = "pre_installation")
    private String preInstallation;

    @ColumnInfo(name = "installation")
    private String installation;

    @ColumnInfo(name = "commissioning")
    private String commissioning;

    @ColumnInfo(name = "chargeable")
    private String chargeable;

    @ColumnInfo(name = "warranty")
    private String warranty;

    @ColumnInfo(name = "courtesy_visit")
    private String courtesyVisit;

    @ColumnInfo(name = "nature_problem")
    private String natureProblem;

    @ColumnInfo(name = "description_work")
    private String descriptionWork;

    @ColumnInfo(name = "suggetion_customer")
    private String suggetionCustomer;

    @ColumnInfo(name = "customer_remark")
    private String customerRemark;

    @ColumnInfo(name = "work_status")
    private String workStatus;

    @ColumnInfo(name = "quality_service")
    private String qualityService;

    @ColumnInfo(name = "services_charges")
    private String servicesCharges;

    @ColumnInfo(name = "conveyance")
    private String conveyance;

    @ColumnInfo(name = "to_form")
    private String toForm;

    @ColumnInfo(name = "others")
    private String others;

    @ColumnInfo(name = "tax")
    private String tax;

    @ColumnInfo(name = "sign_customer")
    private String signCustomer;

    @ColumnInfo(name = "sign_repre")
    private String signRepre;

    @ColumnInfo(name = "sign_marketing")
    private String signMarketing;

    @ColumnInfo(name = "reason_incomplete")
    private String reasonIncomplete;

    // Client/Company name :: Ashok
    @ColumnInfo(name = "customer_first_name")
    private String customerFirstName;

    // Customer full name :: Ashok
    @ColumnInfo(name = "customer_last_name")
    private String customerLastName;

    @ColumnInfo(name = "complain_form_first_name")
    private String complainFormFirstName;

    @ColumnInfo(name = "complain_form_last_name")
    private String complainFormLastName;

    @ColumnInfo(name = "engineer_first_name")
    private String engineerFirstName;

    @ColumnInfo(name = "engineer_last_name")
    private String engineerLastName;

    @ColumnInfo(name = "visit_type")
    private String visitType;

    public Complaint() {
    }

    public Complaint(long id, String customerName, String mno, String email, String mcType, String mcModel,
                     String srNo, String description, String alternateNum, String address, String dealerName,
                     String image, String complainForm, String complainTo, String assignDate, String priority,
                     String paymentCustomer, String hold, String note, String createdAt, String updatedAt,
                     String status, String scheduleDate, String observation, String workDate, String spareReplace,
                     String checkoutDate, String resolveImage, String resolveDate, String customerFullName,
                     String complainToFullName, String reportNo, String contactPerson, String heatPSrNo, String noOfHeat,
                     String hwgSrNo, String noOfHwg, String monthYearInsta, String oemDealerName, String dContactPerson,
                     String dAddress, String dMno, String equipment, String poNoDate, String complainNoDate,
                     String typeOfCall, String services, String preInstallation, String installation, String commissioning,
                     String chargeable, String warranty, String courtesyVisit, String natureProblem, String descriptionWork,
                     String suggetionCustomer, String customerRemark, String workStatus, String qualityService,
                     String servicesCharges, String conveyance, String toForm, String others, String tax,
                     String signCustomer, String signRepre, String signMarketing, String reasonIncomplete,
                     String customerFirstName, String customerLastName, String complainFormFirstName,
                     String complainFormLastName, String engineerFirstName, String engineerLastName) {

        this.id = id;
        this.customerName = customerName;
        this.mno = mno;
        this.email = email;
        this.mcType = mcType;
        this.mcModel = mcModel;
        this.srNo = srNo;
        this.description = description;
        this.alternateNum = alternateNum;
        this.address = address;
        this.dealerName = dealerName;
        this.image = image;
        this.complainForm = complainForm;
        this.complainTo = complainTo;
        this.assignDate = assignDate;
        this.priority = priority;
        this.paymentCustomer = paymentCustomer;
        this.hold = hold;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.scheduleDate = scheduleDate;
        this.observation = observation;
        this.workDate = workDate;
        this.spareReplace = spareReplace;
        this.checkoutDate = checkoutDate;
        this.resolveImage = resolveImage;
        this.resolveDate = resolveDate;
        this.customerFullName = customerFullName;
        this.complainToFullName = complainToFullName;
        this.reportNo = reportNo;
        this.contactPerson = contactPerson;
        this.heatPSrNo = heatPSrNo;
        this.noOfHeat = noOfHeat;
        this.hwgSrNo = hwgSrNo;
        this.noOfHwg = noOfHwg;
        this.monthYearInsta = monthYearInsta;
        this.oemDealerName = oemDealerName;
        this.dContactPerson = dContactPerson;
        this.dAddress = dAddress;
        this.dMno = dMno;
        this.equipment = equipment;
        this.poNoDate = poNoDate;
        this.complainNoDate = complainNoDate;
        this.typeOfCall = typeOfCall;
        this.services = services;
        this.preInstallation = preInstallation;
        this.installation = installation;
        this.commissioning = commissioning;
        this.chargeable = chargeable;
        this.warranty = warranty;
        this.courtesyVisit = courtesyVisit;
        this.natureProblem = natureProblem;
        this.descriptionWork = descriptionWork;
        this.suggetionCustomer = suggetionCustomer;
        this.customerRemark = customerRemark;
        this.workStatus = workStatus;
        this.qualityService = qualityService;
        this.servicesCharges = servicesCharges;
        this.conveyance = conveyance;
        this.toForm = toForm;
        this.others = others;
        this.tax = tax;
        this.signCustomer = signCustomer;
        this.signRepre = signRepre;
        this.signMarketing = signMarketing;
        this.reasonIncomplete = reasonIncomplete;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.complainFormFirstName = complainFormFirstName;
        this.complainFormLastName = complainFormLastName;
        this.engineerFirstName = engineerFirstName;
        this.engineerLastName = engineerLastName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMno() {
        return mno;
    }

    public void setMno(String mno) {
        this.mno = mno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMcType() {
        return mcType;
    }

    public void setMcType(String mcType) {
        this.mcType = mcType;
    }

    public String getMcModel() {
        return mcModel;
    }

    public void setMcModel(String mcModel) {
        this.mcModel = mcModel;
    }

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAlternateNum() {
        return alternateNum;
    }

    public void setAlternateNum(String alternateNum) {
        this.alternateNum = alternateNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getComplainForm() {
        return complainForm;
    }

    public void setComplainForm(String complainForm) {
        this.complainForm = complainForm;
    }

    public String getComplainTo() {
        return complainTo;
    }

    public void setComplainTo(String complainTo) {
        this.complainTo = complainTo;
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPaymentCustomer() {
        return paymentCustomer;
    }

    public void setPaymentCustomer(String paymentCustomer) {
        this.paymentCustomer = paymentCustomer;
    }

    public String getHold() {
        return hold;
    }

    public void setHold(String hold) {
        this.hold = hold;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getSpareReplace() {
        return spareReplace;
    }

    public void setSpareReplace(String spareReplace) {
        this.spareReplace = spareReplace;
    }

    public String getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public String getResolveImage() {
        return resolveImage;
    }

    public void setResolveImage(String resolveImage) {
        this.resolveImage = resolveImage;
    }

    public String getResolveDate() {
        return resolveDate;
    }

    public void setResolveDate(String resolveDate) {
        this.resolveDate = resolveDate;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public String getComplainToFullName() {
        return complainToFullName;
    }

    public void setComplainToFullName(String complainToFullName) {
        this.complainToFullName = complainToFullName;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getHeatPSrNo() {
        return heatPSrNo;
    }

    public void setHeatPSrNo(String heatPSrNo) {
        this.heatPSrNo = heatPSrNo;
    }

    public String getNoOfHeat() {
        return noOfHeat;
    }

    public void setNoOfHeat(String noOfHeat) {
        this.noOfHeat = noOfHeat;
    }

    public String getHwgSrNo() {
        return hwgSrNo;
    }

    public void setHwgSrNo(String hwgSrNo) {
        this.hwgSrNo = hwgSrNo;
    }

    public String getNoOfHwg() {
        return noOfHwg;
    }

    public void setNoOfHwg(String noOfHwg) {
        this.noOfHwg = noOfHwg;
    }

    public String getMonthYearInsta() {
        return monthYearInsta;
    }

    public void setMonthYearInsta(String monthYearInsta) {
        this.monthYearInsta = monthYearInsta;
    }

    public String getOemDealerName() {
        return oemDealerName;
    }

    public void setOemDealerName(String oemDealerName) {
        this.oemDealerName = oemDealerName;
    }

    public String getDContactPerson() {
        return dContactPerson;
    }

    public void setDContactPerson(String dContactPerson) {
        this.dContactPerson = dContactPerson;
    }

    public String getDAddress() {
        return dAddress;
    }

    public void setDAddress(String dAddress) {
        this.dAddress = dAddress;
    }

    public String getDMno() {
        return dMno;
    }

    public void setDMno(String dMno) {
        this.dMno = dMno;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getPoNoDate() {
        return poNoDate;
    }

    public void setPoNoDate(String poNoDate) {
        this.poNoDate = poNoDate;
    }

    public String getComplainNoDate() {
        return complainNoDate;
    }

    public void setComplainNoDate(String complainNoDate) {
        this.complainNoDate = complainNoDate;
    }

    public String getTypeOfCall() {
        return typeOfCall;
    }

    public void setTypeOfCall(String typeOfCall) {
        this.typeOfCall = typeOfCall;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getPreInstallation() {
        return preInstallation;
    }

    public void setPreInstallation(String preInstallation) {
        this.preInstallation = preInstallation;
    }

    public String getInstallation() {
        return installation;
    }

    public void setInstallation(String installation) {
        this.installation = installation;
    }

    public String getCommissioning() {
        return commissioning;
    }

    public void setCommissioning(String commissioning) {
        this.commissioning = commissioning;
    }

    public String getChargeable() {
        return chargeable;
    }

    public void setChargeable(String chargeable) {
        this.chargeable = chargeable;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getCourtesyVisit() {
        return courtesyVisit;
    }

    public void setCourtesyVisit(String courtesyVisit) {
        this.courtesyVisit = courtesyVisit;
    }

    public String getNatureProblem() {
        return natureProblem;
    }

    public void setNatureProblem(String natureProblem) {
        this.natureProblem = natureProblem;
    }

    public String getDescriptionWork() {
        return descriptionWork;
    }

    public void setDescriptionWork(String descriptionWork) {
        this.descriptionWork = descriptionWork;
    }

    public String getSuggetionCustomer() {
        return suggetionCustomer;
    }

    public void setSuggetionCustomer(String suggetionCustomer) {
        this.suggetionCustomer = suggetionCustomer;
    }

    public String getCustomerRemark() {
        return customerRemark;
    }

    public void setCustomerRemark(String customerRemark) {
        this.customerRemark = customerRemark;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getQualityService() {
        return qualityService;
    }

    public void setQualityService(String qualityService) {
        this.qualityService = qualityService;
    }

    public String getServicesCharges() {
        return servicesCharges;
    }

    public void setServicesCharges(String servicesCharges) {
        this.servicesCharges = servicesCharges;
    }

    public String getConveyance() {
        return conveyance;
    }

    public void setConveyance(String conveyance) {
        this.conveyance = conveyance;
    }

    public String getToForm() {
        return toForm;
    }

    public void setToForm(String toForm) {
        this.toForm = toForm;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getSignCustomer() {
        return signCustomer;
    }

    public void setSignCustomer(String signCustomer) {
        this.signCustomer = signCustomer;
    }

    public String getSignRepre() {
        return signRepre;
    }

    public void setSignRepre(String signRepre) {
        this.signRepre = signRepre;
    }

    public String getSignMarketing() {
        return signMarketing;
    }

    public void setSignMarketing(String signMarketing) {
        this.signMarketing = signMarketing;
    }

    public String getReasonIncomplete() {
        return reasonIncomplete;
    }

    public void setReasonIncomplete(String reasonIncomplete) {
        this.reasonIncomplete = reasonIncomplete;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getComplainFormFirstName() {
        return complainFormFirstName;
    }

    public void setComplainFormFirstName(String complainFormFirstName) {
        this.complainFormFirstName = complainFormFirstName;
    }

    public String getComplainFormLastName() {
        return complainFormLastName;
    }

    public void setComplainFormLastName(String complainFormLastName) {
        this.complainFormLastName = complainFormLastName;
    }

    public String getEngineerFirstName() {
        return engineerFirstName;
    }

    public void setEngineerFirstName(String engineerFirstName) {
        this.engineerFirstName = engineerFirstName;
    }

    public String getEngineerLastName() {
        return engineerLastName;
    }

    public void setEngineerLastName(String engineerLastName) {
        this.engineerLastName = engineerLastName;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }
}
