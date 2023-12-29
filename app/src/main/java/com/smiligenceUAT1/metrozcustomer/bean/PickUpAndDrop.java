package com.smiligenceUAT1.metrozcustomer.bean;

import java.util.List;

public class PickUpAndDrop
{
    String pickupAddress;
    String dropAddress;
    List<String> deliverObject;
    int totalAmountFair;
    double startPickupLatitude;
    double endPickupLongtitude;
    double startDeliveryLatitude;
    double endDeliveryLongtitude;
    int totalDistance;
    int totalAmountToPaid;
    int tipAmount;
    boolean parcelWeightLessThanFiveKgs;
    boolean parcelCanBeCarriedinBike;
    int basic_Fair;
    int minimum_Fair;
    int per_km;
    int total_Basic_Fair;
    int total_Minimum_Fair;

    public int getBasic_Fair() {
        return basic_Fair;
    }

    public void setBasic_Fair(int basic_Fair) {
        this.basic_Fair = basic_Fair;
    }

    public int getMinimum_Fair() {
        return minimum_Fair;
    }

    public void setMinimum_Fair(int minimum_Fair) {
        this.minimum_Fair = minimum_Fair;
    }

    public int getPer_km() {
        return per_km;
    }

    public void setPer_km(int per_km) {
        this.per_km = per_km;
    }

    public int getTotal_Basic_Fair() {
        return total_Basic_Fair;
    }

    public void setTotal_Basic_Fair(int total_Basic_Fair) {
        this.total_Basic_Fair = total_Basic_Fair;
    }

    public int getTotal_Minimum_Fair() {
        return total_Minimum_Fair;
    }

    public void setTotal_Minimum_Fair(int total_Minimum_Fair) {
        this.total_Minimum_Fair = total_Minimum_Fair;
    }

    public boolean isParcelWeightLessThanFiveKgs() {
        return parcelWeightLessThanFiveKgs;
    }

    public void setParcelWeightLessThanFiveKgs(boolean parcelWeightLessThanFiveKgs) {
        this.parcelWeightLessThanFiveKgs = parcelWeightLessThanFiveKgs;
    }

    public boolean isParcelCanBeCarriedinBike() {
        return parcelCanBeCarriedinBike;
    }

    public void setParcelCanBeCarriedinBike(boolean parcelCanBeCarriedinBike) {
        this.parcelCanBeCarriedinBike = parcelCanBeCarriedinBike;
    }

    public int getTotalAmountToPaid() {
        return totalAmountToPaid;
    }

    public void setTotalAmountToPaid(int totalAmountToPaid) {
        this.totalAmountToPaid = totalAmountToPaid;
    }

    public int getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(int tipAmount) {
        this.tipAmount = tipAmount;
    }

    public int getTotalAmountFair() {
        return totalAmountFair;
    }

    public void setTotalAmountFair(int totalAmountFair) {
        this.totalAmountFair = totalAmountFair;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getEndPickupLongtitude() {
        return endPickupLongtitude;
    }

    public void setEndPickupLongtitude(double endPickupLongtitude) {
        this.endPickupLongtitude = endPickupLongtitude;
    }

    public double getEndDeliveryLongtitude() {
        return endDeliveryLongtitude;
    }

    public void setEndDeliveryLongtitude(double endDeliveryLongtitude) {
        this.endDeliveryLongtitude = endDeliveryLongtitude;
    }

    public double getStartPickupLatitude() {
        return startPickupLatitude;
    }

    public void setStartPickupLatitude(double startPickupLatitude) {
        this.startPickupLatitude = startPickupLatitude;
    }


    public double getStartDeliveryLatitude() {
        return startDeliveryLatitude;
    }

    public void setStartDeliveryLatitude(double startDeliveryLatitude) {
        this.startDeliveryLatitude = startDeliveryLatitude;
    }



    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public List<String> getDeliverObject() {
        return deliverObject;
    }

    public void setDeliverObject(List<String> deliverObject) {
        this.deliverObject = deliverObject;
    }

}
