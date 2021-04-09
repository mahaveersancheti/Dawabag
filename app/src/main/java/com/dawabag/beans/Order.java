package com.dawabag.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    int id, userId, status;
    String paymentMode, transactionId, couponId, date, fName, lName, addressLine1, addressLine2, addressLine3, pincode, landmark, city, state, country;
    double total, gst, discount, deliveryCharges, payable, grossTotal, couponDiscount;
    ArrayList<Medicine> alMedicines;
    ArrayList<String> alPrescriptions;
    boolean canReturn;
    String deliveryDate, statusLabel = "";

    public double getGrossTotal() {
        return grossTotal;
    }

    public void setGrossTotal(double grossTotal) {
        this.grossTotal = grossTotal;
    }

    public double getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(double couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public String getStatusLabel() {
        switch (status) {
            case 0 : this.statusLabel = "Order placed but payment not done"; break;
            case 1 : this.statusLabel = "Order placed and payment done"; break;
            case 2 : this.statusLabel = "Order Confirmed"; break;
            case 3 : this.statusLabel = "Order Processing"; break;
            case 4 : this.statusLabel = "Dispatched for delivery"; break;
            case 5 : this.statusLabel = "Order Delivered"; break;
            case 6 : this.statusLabel = "Order Cancelled"; break;
            case 7 : this.statusLabel = "Order Return Initiated"; break;
            case 8 : this.statusLabel = "Order Rejected"; break;
            case 9 : this.statusLabel = "Order Return Accepted"; break;
            case 10 : this.statusLabel = "Order Return Rejected"; break;
            case 11 : this.statusLabel = "Order Return Pickup"; break;
            case 12 : this.statusLabel = "Order Return Received"; break;
            case 13 : this.statusLabel = "Order Return Settled"; break;
            case 14 : this.statusLabel = "Order Return Cancelled"; break;
        }
        return statusLabel;
    }

    public String getStatusLabelOpenOrder() {
        switch (status) {
            case 0 : this.statusLabel = "Your order has been placed. Make online payment to process further."; break;
            case 1 : this.statusLabel = "Order placed and payment done"; break;
            case 2 : this.statusLabel = "Order Confirmed"; break;
            case 3 : this.statusLabel = "Order Processing"; break;
            case 4 : this.statusLabel = "Dispatched for delivery"; break;
            case 5 : this.statusLabel = "Order Delivered"; break;
            case 6 : this.statusLabel = "Order Cancelled"; break;
            case 7 : this.statusLabel = "Order Return Initiated"; break;
            case 8 : this.statusLabel = "Order Rejected"; break;
            case 9 : this.statusLabel = "Order Return Accepted"; break;
            case 10 : this.statusLabel = "Order Return Rejected"; break;
            case 11 : this.statusLabel = "Order Return Pickup"; break;
            case 12 : this.statusLabel = "Order Return Received"; break;
            case 13 : this.statusLabel = "Order Return Settled"; break;
            case 14 : this.statusLabel = "Order Return Cancelled"; break;
        }
        return statusLabel;
    }

    public boolean isCanReturn() {
        return canReturn;
    }

    public void setCanReturn(boolean canReturn) {
        this.canReturn = canReturn;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public ArrayList<String> getAlPrescriptions() {
        return alPrescriptions;
    }

    public void setAlPrescriptions(ArrayList<String> alPrescriptions) {
        this.alPrescriptions = alPrescriptions;
    }

    public ArrayList<Medicine> getAlMedicines() {
        return alMedicines;
    }

    public void setAlMedicines(ArrayList<Medicine> alMedicines) {
        this.alMedicines = alMedicines;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getGst() {
        return gst;
    }

    public void setGst(double gst) {
        this.gst = gst;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(double deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public double getPayable() {
        return payable;
    }

    public void setPayable(double payable) {
        this.payable = payable;
    }
}
