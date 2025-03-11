package com.developerali.bongbilling.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class InvoiceData implements Parcelable {
    @SerializedName("invoice_no")
    private String invoiceNo;

    @SerializedName("gName")
    private String gName;

    @SerializedName("gAddress")
    private String gAddress;

    @SerializedName("gPhone")
    private String gPhone;

    @SerializedName("total")
    private double total;

    @SerializedName("subTotal")
    private double subTotal;

    @SerializedName("due")
    private double due;

    @SerializedName("advance")
    private double advance;

    @SerializedName("discount")
    private double discount;

    @SerializedName("jDate")
    private String jDate;

    @SerializedName("checkIn")
    private String checkIn;

    @SerializedName("checkOut")
    private String checkOut;

    @SerializedName("rules1")
    private String rules1;

    @SerializedName("rules2")
    private String rules2;

    @SerializedName("details")
    private String details;

    @SerializedName("created_at")
    private String created_at;

    // Constructor, Getters, and Setters


    public InvoiceData() {
    }

    protected InvoiceData(Parcel in) {
        invoiceNo = in.readString();
        gName = in.readString();
        gAddress = in.readString();
        gPhone = in.readString();
        total = in.readDouble();
        subTotal = in.readDouble();
        due = in.readDouble();
        advance = in.readDouble();
        discount = in.readDouble();
        jDate = in.readString();
        checkIn = in.readString();
        checkOut = in.readString();
        rules1 = in.readString();
        rules2 = in.readString();
        details = in.readString();
        created_at = in.readString();
    }

    public static final Creator<InvoiceData> CREATOR = new Creator<InvoiceData>() {
        @Override
        public InvoiceData createFromParcel(Parcel in) {
            return new InvoiceData(in);
        }

        @Override
        public InvoiceData[] newArray(int size) {
            return new InvoiceData[size];
        }
    };

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getgAddress() {
        return gAddress;
    }

    public void setgAddress(String gAddress) {
        this.gAddress = gAddress;
    }

    public String getgPhone() {
        return gPhone;
    }

    public void setgPhone(String gPhone) {
        this.gPhone = gPhone;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }

    public double getAdvance() {
        return advance;
    }

    public void setAdvance(double advance) {
        this.advance = advance;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getjDate() {
        return jDate;
    }

    public void setjDate(String jDate) {
        this.jDate = jDate;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public String getRules1() {
        return rules1;
    }

    public void setRules1(String rules1) {
        this.rules1 = rules1;
    }

    public String getRules2() {
        return rules2;
    }

    public void setRules2(String rules2) {
        this.rules2 = rules2;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(invoiceNo);
        parcel.writeString(gName);
        parcel.writeString(gAddress);
        parcel.writeString(gPhone);
        parcel.writeDouble(total);
        parcel.writeDouble(subTotal);
        parcel.writeDouble(due);
        parcel.writeDouble(advance);
        parcel.writeDouble(discount);
        parcel.writeString(jDate);
        parcel.writeString(checkIn);
        parcel.writeString(checkOut);
        parcel.writeString(rules1);
        parcel.writeString(rules2);
        parcel.writeString(details);
        parcel.writeString(created_at);
    }
}
