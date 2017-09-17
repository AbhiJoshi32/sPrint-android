package com.binktec.sprint.modal.pojo;

import com.binktec.sprint.modal.pojo.shop.Shop;

import java.util.List;

public class PrintTransaction {
    private Shop shop;
    private List<FileDetail> fileDetails;
    private PrintDetail printDetail;
    private float bindingCost;
    private float printCost;
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<FileDetail> getFileDetails() {
        return fileDetails;
    }

    public void setFileDetails(List<FileDetail> fileDetails) {
        this.fileDetails = fileDetails;
    }

    public PrintDetail getPrintDetail() {
        return printDetail;
    }

    public void setPrintDetail(PrintDetail printDetail) {
        this.printDetail = printDetail;
    }

    public float getBindingCost() {
        return bindingCost;
    }

    public void setBindingCost(float bindingCost) {
        this.bindingCost = bindingCost;
    }

    public float getPrintCost() {
        return printCost;
    }

    public void setPrintCost(float printCost) {
        this.printCost = printCost;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}