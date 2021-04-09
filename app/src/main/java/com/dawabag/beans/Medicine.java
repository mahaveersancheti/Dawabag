package com.dawabag.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class Medicine implements Serializable {
    int id, orderQtyLimit, igst, cgst, sgst, selectedStrengthId = 0, selectedQty = 0, medicineCartId;
    int medicineBatchId, orderDetailsId;
    double mrp, offerPrice;
    String genericName, brand, company, manufacturer, storageCondition, category, medicineCode;
    String hsnCode, image1, image2, image3, image4, image5, image6, strength;
    String tabletPack, packingType;
    ArrayList<Strengths> alStrength = null;
    boolean canReturn;
    int returnQty = 0;

    public int getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(int returnQty) {
        this.returnQty = returnQty;
    }

    public double getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(double offerPrice) {
        this.offerPrice = offerPrice;
    }

    public String getTabletPack() {
        return tabletPack;
    }

    public void setTabletPack(String tabletPack) {
        this.tabletPack = tabletPack;
    }

    public String getPackingType() {
        String str = "";
        switch (packingType) {
            case "0": str = "";
                break;
            case "1": str = "Tablets";
                break;
            case "2": str = "Inj";
                break;
            case "3": str = "Syrup";
                break;
            case "4": str = "Capsules";
                break;
        }
        return str;
    }

    public void setPackingType(String packingType) {
        this.packingType = packingType;
    }

    public int getOrderDetailsId() {
        return orderDetailsId;
    }

    public void setOrderDetailsId(int orderDetailsId) {
        this.orderDetailsId = orderDetailsId;
    }

    public boolean isCanReturn() {
        return canReturn;
    }

    public void setCanReturn(boolean canReturn) {
        this.canReturn = canReturn;
    }

    public int getMedicineBatchId() {
        return medicineBatchId;
    }

    public void setMedicineBatchId(int medicineBatchId) {
        this.medicineBatchId = medicineBatchId;
    }

    public int getMedicineCartId() {
        return medicineCartId;
    }

    public void setMedicineCartId(int medicineCartId) {
        this.medicineCartId = medicineCartId;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public int getSelectedQty() {
        return selectedQty;
    }

    public void setSelectedQty(int selectedQty) {
        this.selectedQty = selectedQty;
    }

    public Medicine() {
        alStrength = new ArrayList<>();
    }

    public ArrayList<Strengths> getAlStrength() {
        return alStrength;
    }

    public void setAlStrength(ArrayList<Strengths> alStrength) {
        this.alStrength = alStrength;
    }

    public int getSelectedStrengthId() {
        return selectedStrengthId;
    }

    public void setSelectedStrengthId(int selectedStrengthId) {
        this.selectedStrengthId = selectedStrengthId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderQtyLimit() {
        return orderQtyLimit;
    }

    public void setOrderQtyLimit(int orderQtyLimit) {
        this.orderQtyLimit = orderQtyLimit;
    }

    public int getIgst() {
        return igst;
    }

    public void setIgst(int igst) {
        this.igst = igst;
    }

    public int getCgst() {
        return cgst;
    }

    public void setCgst(int cgst) {
        this.cgst = cgst;
    }

    public int getSgst() {
        return sgst;
    }

    public void setSgst(int sgst) {
        this.sgst = sgst;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public String getImage5() {
        return image5;
    }

    public void setImage5(String image5) {
        this.image5 = image5;
    }

    public String getImage6() {
        return image6;
    }

    public void setImage6(String image6) {
        this.image6 = image6;
    }
}
