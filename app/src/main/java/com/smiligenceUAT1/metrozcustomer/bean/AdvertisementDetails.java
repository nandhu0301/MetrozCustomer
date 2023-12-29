package com.smiligenceUAT1.metrozcustomer.bean;

import java.util.List;

public class AdvertisementDetails
{
    String creationdate;
    String image;
    String id;
    String advertisingAgent;
    String advertisingBrandName;
    String advertisingType;
    String advertisingMedia;
    String url;
    String advertisingDuration;
    String advertisementstatus;
    String advertisementStartingDurationDate;
    String advertisementEndingDurationDate;
    String scheduledTime;
    String scheduledDate;
    int advertisementAmount;
    String paidStatus;
    String advertisementPaymentType;

    public String getAdvertisementPaymentType() {
        return advertisementPaymentType;
    }

    public void setAdvertisementPaymentType(String advertisementPaymentType) {
        this.advertisementPaymentType = advertisementPaymentType;
    }

    public String getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(String paidStatus) {
        this.paidStatus = paidStatus;
    }

    public int getAdvertisementAmount() {
        return advertisementAmount;
    }

    public void setAdvertisementAmount(int advertisementAmount) {
        this.advertisementAmount = advertisementAmount;
    }

    public String getAdvertisementPlacing() {
        return advertisementPlacing;
    }

    public void setAdvertisementPlacing(String advertisementPlacing) {
        this.advertisementPlacing = advertisementPlacing;
    }

    String advertisementPlacing;

    List<UserDetails> advertisingStoreList;

    public List<UserDetails> getAdvertisingStoreList() {
        return advertisingStoreList;
    }

    public void setAdvertisingStoreList(List<UserDetails> advertisingStoreList) {
        this.advertisingStoreList = advertisingStoreList;
    }

    public String getAdvertisingCategoryName() {
        return advertisingCategoryName;
    }

    public void setAdvertisingCategoryName(String advertisingCategoryName) {
        this.advertisingCategoryName = advertisingCategoryName;
    }

    String advertisingCategoryName;



    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }




    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }


    public String getAdvertisementStartingDurationDate() {
        return advertisementStartingDurationDate;
    }

    public void setAdvertisementStartingDurationDate(String advertisementStartingDurationDate) {
        this.advertisementStartingDurationDate = advertisementStartingDurationDate;
    }

    public String getAdvertisementEndingDurationDate() {
        return advertisementEndingDurationDate;
    }

    public void setAdvertisementEndingDurationDate(String advertisementEndingDurationDate) {
        this.advertisementEndingDurationDate = advertisementEndingDurationDate;
    }

    public String getAdvertisementstatus() {
        return advertisementstatus;
    }

    public void setAdvertisementstatus(String advertisementstatus) {
        this.advertisementstatus = advertisementstatus;
    }



    public String getAdvertisementStartingDuration() {
        return advertisementStartingDuration;
    }

    public void setAdvertisementStartingDuration(String advertisementStartingDuration) {
        this.advertisementStartingDuration = advertisementStartingDuration;
    }

    String advertisementStartingDuration;
    String advertisementExpiringDuration;

    public String getAdvertisementExpiringDuration() {
        return advertisementExpiringDuration;
    }

    public void setAdvertisementExpiringDuration(String advertisementExpiringDuration) {
        this.advertisementExpiringDuration = advertisementExpiringDuration;
    }



    public String getAdvertisementpriority() {
        return advertisementpriority;
    }

    public void setAdvertisementpriority(String advertisementpriority) {
        this.advertisementpriority = advertisementpriority;
    }

    String advertisementpriority;

    public String getAdvertisingAgent() {
        return advertisingAgent;
    }

    public void setAdvertisingAgent(String advertisingAgent) {
        this.advertisingAgent = advertisingAgent;
    }

    public String getAdvertisingBrandName() {
        return advertisingBrandName;
    }

    public void setAdvertisingBrandName(String advertisingBrandName) {
        this.advertisingBrandName = advertisingBrandName;
    }



    public String getAdvertisingType() {
        return advertisingType;
    }

    public void setAdvertisingType(String advertisingType) {
        this.advertisingType = advertisingType;
    }

    public String getAdvertisingMedia() {
        return advertisingMedia;
    }

    public void setAdvertisingMedia(String advertisingMedia) {
        this.advertisingMedia = advertisingMedia;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAdvertisingDuration() {
        return advertisingDuration;
    }

    public void setAdvertisingDuration(String advertisingDuration) {
        this.advertisingDuration = advertisingDuration;
    }






    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AdvertisementDetails() {
    }

    public String getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(String creationdate) {
        this.creationdate = creationdate;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
