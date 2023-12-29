package com.smiligenceUAT1.metrozcustomer.bean;

public class OneTimeDiscount
{
    String id;
    String createdDate;
    String customerName;
    String customerId;
    String storeId;
    String storeName;
    String couponName;
    int couponAmount;
    String orderId;
    String usedTag;
    int totalAmount;
    String reasonForThisCoupon;
    String discountGivenBy;


    public String getDiscountGivenBy() {
        return discountGivenBy;
    }

    public void setDiscountGivenBy(String discountGivenBy) {
        this.discountGivenBy = discountGivenBy;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getReasonForThisCoupon() {
        return reasonForThisCoupon;
    }

    public void setReasonForThisCoupon(String reasonForThisCoupon) {
        this.reasonForThisCoupon = reasonForThisCoupon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public int getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(int couponAmount) {
        this.couponAmount = couponAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUsedTag() {
        return usedTag;
    }

    public void setUsedTag(String usedTag) {
        this.usedTag = usedTag;
    }
}
