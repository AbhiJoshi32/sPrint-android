
package com.binktec.sprint.modal.pojo.shop;

import java.util.List;

public class Shop {

    private List<String> availBinding = null;
    private String shopAvailColor;
    private List<String> shopAvailPaperType = null;
    private String shopAvailability;
    private ShopCost shopCost;
    private String shopId;
    private String shopLocation;
    private String shopName;
    private int shopQueue;

    public int getShopQueue() {
        return shopQueue;
    }

    public void setShopQueue(int shopQueue) {
        this.shopQueue = shopQueue;
    }

    public List<String> getAvailBinding() {
        return availBinding;
    }

    public void setAvailBinding(List<String> availBinding) {
        this.availBinding = availBinding;
    }

    public String getShopAvailColor() {
        return shopAvailColor;
    }

    public void setShopAvailColor(String shopAvailColor) {
        this.shopAvailColor = shopAvailColor;
    }

    public List<String> getShopAvailPaperType() {
        return shopAvailPaperType;
    }

    public void setShopAvailPaperType(List<String> shopAvailPaperType) {
        this.shopAvailPaperType = shopAvailPaperType;
    }

    public String getShopAvailability() {
        return shopAvailability;
    }

    public void setShopAvailability(String shopAvailability) {
        this.shopAvailability = shopAvailability;
    }

    public ShopCost getShopCost() {
        return shopCost;
    }

    public void setShopCost(ShopCost shopCost) {
        this.shopCost = shopCost;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopLocation() {
        return shopLocation;
    }

    public void setShopLocation(String shopLocation) {
        this.shopLocation = shopLocation;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

}
