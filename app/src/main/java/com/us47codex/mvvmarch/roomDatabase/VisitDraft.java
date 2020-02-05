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

@Entity(tableName = "tbl_visit_draft")
public class VisitDraft {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "visit_data")
    private String visitData;

    @ColumnInfo(name = "report_no")
    private String reportNo;

    @ColumnInfo(name = "mc_type")
    private String mcType;

    @ColumnInfo(name = "visit_type")
    private String visitType;

    @ColumnInfo(name = "visit_date")
    private String visitDate;

    @ColumnInfo(name = "customer_name")
    private String customerName;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

//    @ColumnInfo(name = "sign_customer", typeAffinity = ColumnInfo.BLOB)
//    private byte[] signCustomer;
//
//    @ColumnInfo(name = "sign_repre", typeAffinity = ColumnInfo.BLOB)
//    private byte[] signRepre;
//
//    @ColumnInfo(name = "sign_marketing", typeAffinity = ColumnInfo.BLOB)
//    private byte[] signMarketing;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVisitData() {
        return visitData;
    }

    public void setVisitData(String visitData) {
        this.visitData = visitData;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public String getMcType() {
        return mcType;
    }

    public void setMcType(String mcType) {
        this.mcType = mcType;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

//    public byte[] getSignCustomer() {
//        return signCustomer;
//    }
//
//    public void setSignCustomer(byte[] signCustomer) {
//        this.signCustomer = signCustomer;
//    }
//
//    public byte[] getSignRepre() {
//        return signRepre;
//    }
//
//    public void setSignRepre(byte[] signRepre) {
//        this.signRepre = signRepre;
//    }
//
//    public byte[] getSignMarketing() {
//        return signMarketing;
//    }
//
//    public void setSignMarketing(byte[] signMarketing) {
//        this.signMarketing = signMarketing;
//    }
}
