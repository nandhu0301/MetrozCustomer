package com.smiligenceUAT1.metrozcustomer.bean;

public class MetrozStoreTime
{
    String creationDate;
    String shopStartTime;
    String shopEndTime;
    String sellerId;
    String storeStatus;

    public String getStoreStatus()
    {
        return storeStatus;
    }

    public void setStoreStatus(String storeStatus) {
        this.storeStatus = storeStatus;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getShopStartTime() {
        return shopStartTime;
    }

    public void setShopStartTime(String shopStartTime) {
        this.shopStartTime = shopStartTime;
    }

    public String getShopEndTime() {
        return shopEndTime;
    }

    public void setShopEndTime(String shopEndTime) {
        this.shopEndTime = shopEndTime;
    }
}
