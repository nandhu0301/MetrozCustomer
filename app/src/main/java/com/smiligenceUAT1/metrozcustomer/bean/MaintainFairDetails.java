package com.smiligenceUAT1.metrozcustomer.bean;

public class MaintainFairDetails
{
    int baseFairforPickUpAndDrop;
    int minimumFairPickUpAndDrop;
    int perKmPickUpAndDrop;
    String creationDate;
    int perKmOtherCategory;
    int minimumFairOtherCategory;

    int perKmForDeliveryBoy;


    public int getPerKmForDeliveryBoy() {
        return perKmForDeliveryBoy;
    }

    public void setPerKmForDeliveryBoy(int perKmForDeliveryBoy) {
        this.perKmForDeliveryBoy = perKmForDeliveryBoy;
    }

    public MaintainFairDetails() {
    }

    public int getBaseFairforPickUpAndDrop() {
        return baseFairforPickUpAndDrop;
    }

    public void setBaseFairforPickUpAndDrop(int baseFairforPickUpAndDrop) {
        this.baseFairforPickUpAndDrop = baseFairforPickUpAndDrop;
    }

    public int getMinimumFairPickUpAndDrop() {
        return minimumFairPickUpAndDrop;
    }

    public void setMinimumFairPickUpAndDrop(int minimumFairPickUpAndDrop) {
        this.minimumFairPickUpAndDrop = minimumFairPickUpAndDrop;
    }

    public int getPerKmPickUpAndDrop() {
        return perKmPickUpAndDrop;
    }

    public void setPerKmPickUpAndDrop(int perKmPickUpAndDrop) {
        this.perKmPickUpAndDrop = perKmPickUpAndDrop;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getPerKmOtherCategory() {
        return perKmOtherCategory;
    }

    public void setPerKmOtherCategory(int perKmOtherCategory) {
        this.perKmOtherCategory = perKmOtherCategory;
    }

    public int getMinimumFairOtherCategory() {
        return minimumFairOtherCategory;
    }

    public void setMinimumFairOtherCategory(int minimumFairOtherCategory) {
        this.minimumFairOtherCategory = minimumFairOtherCategory;
    }
}
