package com.smiligenceUAT1.metrozcustomer.bean;

public class Discount {

    private int discountId;
    private String discountName;
    private String discountDescription;
    private String discountType;
    private String discountPercentageValue;
    private String discountPrice;
    private String discountImage;
    private String discountStatus;
    private String buydiscountItem;
    private String getdiscountItem;
    private int buyOfferCount;
    private int getOfferCount;
    private String minmumBillAmount;
    private String maxAmountForDiscount;
    private String createDate;
    private String sellerId;
    String typeOfDiscount;
    String discountGivenBy;


    public String getDiscountGivenBy() {
        return discountGivenBy;
    }

    public void setDiscountGivenBy(String discountGivenBy) {
        this.discountGivenBy = discountGivenBy;
    }
    public String getTypeOfDiscount() {
        return typeOfDiscount;
    }

    public void setTypeOfDiscount(String typeOfDiscount) {
        this.typeOfDiscount = typeOfDiscount;
    }
    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    // TODO Yet to be implemented
    private String discountCoupon;

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public void setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
    }

    public String getDiscountPercentageValue() {
        return discountPercentageValue;
    }

    public void setDiscountPercentageValue(String discountPercentageValue) {
        this.discountPercentageValue = discountPercentageValue;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getDiscountImage() {
        return discountImage;
    }

    public void setDiscountImage(String discountImage) {
        this.discountImage = discountImage;
    }

    public String getMinmumBillAmount() {
        return minmumBillAmount;
    }

    public void setMinmumBillAmount(String minmumBillAmount) {
        this.minmumBillAmount = minmumBillAmount;
    }

    public String getBuydiscountItem() {
        return buydiscountItem;
    }

    public void setBuydiscountItem(String buydiscountItem) {
        this.buydiscountItem = buydiscountItem;
    }

    public String getGetdiscountItem() {
        return getdiscountItem;
    }

    public void setGetdiscountItem(String getdiscountItem) {
        this.getdiscountItem = getdiscountItem;
    }

    public int getGetOfferCount() {
        return getOfferCount;
    }

    public void setGetOfferCount(int getOfferCount) {
        this.getOfferCount = getOfferCount;
    }

    public int getBuyOfferCount() {
        return buyOfferCount;
    }

    public void setBuyOfferCount(int buyOfferCount) {
        this.buyOfferCount = buyOfferCount;
    }

    public Discount() {
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getDiscountStatus() {
        return discountStatus;
    }

    public void setDiscountStatus(String discountStatus) {
        this.discountStatus = discountStatus;
    }

    public String getDiscountCoupon() {
        return discountCoupon;
    }

    public void setDiscountCoupon(String discountCoupon) {
        this.discountCoupon = discountCoupon;
    }

    public String getMaxAmountForDiscount() {
        return maxAmountForDiscount;
    }

    public void setMaxAmountForDiscount(String maxAmountForDiscount) {
        this.maxAmountForDiscount = maxAmountForDiscount;
    }
}
