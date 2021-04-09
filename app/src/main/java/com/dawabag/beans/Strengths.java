package com.dawabag.beans;

import java.io.Serializable;

public class Strengths implements Serializable {
    int id, medicineBatchId;
    String strength, batchNumber, expiryDate, shelfLifeDate;
    double priceToRetailer, totalPriceToRetailer, MRP, NPR, TNPR, PTS, offerPrice;

    public double getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(double offerPrice) {
        this.offerPrice = offerPrice;
    }

    public int getMedicineBatchId() {
        return medicineBatchId;
    }

    public void setMedicineBatchId(int medicineBatchId) {
        this.medicineBatchId = medicineBatchId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getShelfLifeDate() {
        return shelfLifeDate;
    }

    public void setShelfLifeDate(String shelfLifeDate) {
        this.shelfLifeDate = shelfLifeDate;
    }

    public double getPriceToRetailer() {
        return priceToRetailer;
    }

    public void setPriceToRetailer(double priceToRetailer) {
        this.priceToRetailer = priceToRetailer;
    }

    public double getTotalPriceToRetailer() {
        return totalPriceToRetailer;
    }

    public void setTotalPriceToRetailer(double totalPriceToRetailer) {
        this.totalPriceToRetailer = totalPriceToRetailer;
    }

    public double getMRP() {
        return MRP;
    }

    public void setMRP(double MRP) {
        this.MRP = MRP;
    }

    public double getNPR() {
        return NPR;
    }

    public void setNPR(double NPR) {
        this.NPR = NPR;
    }

    public double getTNPR() {
        return TNPR;
    }

    public void setTNPR(double TNPR) {
        this.TNPR = TNPR;
    }

    public double getPTS() {
        return PTS;
    }

    public void setPTS(double PTS) {
        this.PTS = PTS;
    }
}
