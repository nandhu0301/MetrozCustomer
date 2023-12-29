package com.smiligenceUAT1.metrozcustomer.bean;

import java.io.Serializable;

public class ItemDetails implements Serializable {
    private int itemId;
    private String itemName;
    private String createDate;
    private String itemImage;
    private int itemPrice;
    private int itemQuantity;
    private int itemAvailableQuantity;
    private String itemStatus;
    private String itemInactiveDate;
    private int totalItemQtyPrice;
    private String itemBrand;
    private int MRP_Price;
    String subCategoryName;
    String categoryName;
    String itemApprovalStatus;
    String itemType;
    String giftWrapOption;

    int giftAmount;

    public int getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(int giftAmount) {
        this.giftAmount = giftAmount;
    }

    public String getGiftWrapOption() {
        return giftWrapOption;
    }

    public void setGiftWrapOption(String giftWrapOption) {
        this.giftWrapOption = giftWrapOption;
    }


    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemApprovalStatus() {
        return itemApprovalStatus;
    }

    public void setItemApprovalStatus(String itemApprovalStatus) {
        this.itemApprovalStatus = itemApprovalStatus;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getItemCounter() {
        return itemCounter;
    }

    public void setItemCounter(int itemCounter) {
        this.itemCounter = itemCounter;
    }

    private  int itemCounter;
    CategoryDetails categoryDetails;

    private String quantityUnits;
    private int itemBuyQuantity;

    private Boolean wishList;
    private String itemDescription;
    private String itemFeatures;
    private String itemManufacture;
    private String itemRating;
    private String itemReview;

    private int itemMaxLimitation;
    private int itemMinLimitation;

    private String sellerId;

    private String storeName;
    private String storePincode;
    private String storeAdress;
    private String storeLogo;

    public String getStoreLogo() {
        return storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public int getItemBuyQuantity() {
        return itemBuyQuantity;
    }

    public void setItemBuyQuantity(int itemBuyQuantity) {
        this.itemBuyQuantity = itemBuyQuantity;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public int getItemAvailableQuantity() {
        return itemAvailableQuantity;
    }

    public void setItemAvailableQuantity(int itemAvailableQuantity) {
        this.itemAvailableQuantity = itemAvailableQuantity;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getItemInactiveDate() {
        return itemInactiveDate;
    }

    public void setItemInactiveDate(String itemInactiveDate) {
        this.itemInactiveDate = itemInactiveDate;
    }

    public int getTotalItemQtyPrice() {
        return totalItemQtyPrice;
    }

    public void setTotalItemQtyPrice(int totalItemQtyPrice) {
        this.totalItemQtyPrice = totalItemQtyPrice;
    }

    public String getItemBrand() {
        return itemBrand;
    }

    public void setItemBrand(String itemBrand) {
        this.itemBrand = itemBrand;
    }

    public int getMRP_Price() {
        return MRP_Price;
    }

    public void setMRP_Price(int MRP_Price) {
        this.MRP_Price = MRP_Price;
    }

    public CategoryDetails getCategoryDetails() {
        return categoryDetails;
    }

    public void setCategoryDetails(CategoryDetails categoryDetails) {
        this.categoryDetails = categoryDetails;
    }

    public String getQuantityUnits() {
        return quantityUnits;
    }

    public void setQuantityUnits(String quantityUnits) {
        this.quantityUnits = quantityUnits;
    }

    public Boolean getWishList() {
        return wishList;
    }

    public void setWishList(Boolean wishList) {
        this.wishList = wishList;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemFeatures() {
        return itemFeatures;
    }

    public void setItemFeatures(String itemFeatures) {
        this.itemFeatures = itemFeatures;
    }

    public String getItemManufacture() {
        return itemManufacture;
    }

    public void setItemManufacture(String itemManufacture) {
        this.itemManufacture = itemManufacture;
    }

    public String getItemRating() {
        return itemRating;
    }

    public void setItemRating(String itemRating) {
        this.itemRating = itemRating;
    }

    public String getItemReview() {
        return itemReview;
    }

    public void setItemReview(String itemReview) {
        this.itemReview = itemReview;
    }

    public int getItemMaxLimitation() {
        return itemMaxLimitation;
    }

    public void setItemMaxLimitation(int itemMaxLimitation) {
        this.itemMaxLimitation = itemMaxLimitation;
    }

    public int getItemMinLimitation() {
        return itemMinLimitation;
    }

    public void setItemMinLimitation(int itemMinLimitation) {
        this.itemMinLimitation = itemMinLimitation;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStorePincode() {
        return storePincode;
    }

    public void setStorePincode(String storePincode) {
        this.storePincode = storePincode;
    }

    public String getStoreAdress() {
        return storeAdress;
    }

    public void setStoreAdress(String storeAdress) {
        this.storeAdress = storeAdress;
    }
}
