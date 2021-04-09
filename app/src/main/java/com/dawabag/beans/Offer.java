package com.dawabag.beans;

import java.io.Serializable;

public class Offer implements Serializable {
    int id, isFlatOrPer, isRedeemMultipleTimes;
    String title, description, startDate, endDate, code, image;
    double minimumOrderAmount, discountAmount, discountAmountLimit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsFlatOrPer() {
        return isFlatOrPer;
    }

    public void setIsFlatOrPer(int isFlatOrPer) {
        this.isFlatOrPer = isFlatOrPer;
    }

    public int getIsRedeemMultipleTimes() {
        return isRedeemMultipleTimes;
    }

    public void setIsRedeemMultipleTimes(int isRedeemMultipleTimes) {
        this.isRedeemMultipleTimes = isRedeemMultipleTimes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(double minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getDiscountAmountLimit() {
        return discountAmountLimit;
    }

    public void setDiscountAmountLimit(double discountAmountLimit) {
        this.discountAmountLimit = discountAmountLimit;
    }
}
