package com.smiligenceUAT1.metrozcustomer.bean;

import java.util.ArrayList;
import java.util.List;

public class OrderDetails
{
    String orderDay;
    String orderStatus;
    String orderDeliverCodeForSeller;
    String orderDeliverCodeForDrliveryBoy;
    int totalAmount;
    int totalItem;
    String orderTime;
    String storeName;
    String paymentId;
    String orderIdfromPaymentGateway;
    String orderId;
    int paymentamount;
    String paymentDate;
    String paymentType;
    String fullName;
    String shippingPincode;
    String storePincode;
    String shippingaddress;
    ItemDetails itemDetails;
    String assignedTo;
    String phoneNumber;
    String customerName;
    String customerId;
    List<PickUpAndDrop> pickUpAndDroplist=new ArrayList<PickUpAndDrop> ();
    String customerPhoneNumber;
    PickUpAndDrop pickUpAndDrop;
    String categoryType;
    String instructionsToDeliveryBoy;
    String orderDeliveryType;
    int tipAmount;
    String orderCreateDate;
    String deliverOtp;
    String notificationStatus;
    String timerForCancelDelivery;
    int deliveryBoyPaymentAmount;
    ArrayList<String> giftWrappingItemName = new ArrayList<>();
    String discountGivenBy;
    double sellerLatitude;
    double sellerLongtitude;
    String TimeStamp;
    String orderPlacedTiming;
    String ReadyForPickupTiming;
    String deliveryIsOnTheWayTiming;
    String deliveredTiming;

    public String getOrderPlacedTiming() {
        return orderPlacedTiming;
    }

    public void setOrderPlacedTiming(String orderPlacedTiming) {
        this.orderPlacedTiming = orderPlacedTiming;
    }

    public String getReadyForPickupTiming() {
        return ReadyForPickupTiming;
    }

    public void setReadyForPickupTiming(String readyForPickupTiming) {
        ReadyForPickupTiming = readyForPickupTiming;
    }

    public String getDeliveryIsOnTheWayTiming() {
        return deliveryIsOnTheWayTiming;
    }

    public void setDeliveryIsOnTheWayTiming(String deliveryIsOnTheWayTiming) {
        this.deliveryIsOnTheWayTiming = deliveryIsOnTheWayTiming;
    }

    public String getDeliveredTiming() {
        return deliveredTiming;
    }

    public void setDeliveredTiming(String deliveredTiming) {
        this.deliveredTiming = deliveredTiming;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public double getSellerLatitude() {
        return sellerLatitude;
    }

    public void setSellerLatitude(double sellerLatitude) {
        this.sellerLatitude = sellerLatitude;
    }

    public double getSellerLongtitude() {
        return sellerLongtitude;
    }

    public void setSellerLongtitude(double sellerLongtitude) {
        this.sellerLongtitude = sellerLongtitude;
    }

    public String getDiscountGivenBy() {
        return discountGivenBy;
    }

    public void setDiscountGivenBy(String discountGivenBy) {
        this.discountGivenBy = discountGivenBy;
    }

    public int getDeliveryBoyPaymentAmount() {
        return deliveryBoyPaymentAmount;
    }

    public void setDeliveryBoyPaymentAmount(int deliveryBoyPaymentAmount) {
        this.deliveryBoyPaymentAmount = deliveryBoyPaymentAmount;
    }

    public String getTimerForCancelDelivery() {
        return timerForCancelDelivery;
    }

    public void setTimerForCancelDelivery(String timerForCancelDelivery) {
        this.timerForCancelDelivery = timerForCancelDelivery;
    }

    public ArrayList<String> getGiftWrappingItemName() {
        return giftWrappingItemName;
    }

    public void setGiftWrappingItemName(ArrayList<String> giftWrappingItemName) {
        this.giftWrappingItemName = giftWrappingItemName;
    }

    public String getNotificationStatusForCustomer() {
        return notificationStatusForCustomer;
    }

    public void setNotificationStatusForCustomer(String notificationStatusForCustomer) {
        this.notificationStatusForCustomer = notificationStatusForCustomer;
    }

    String notificationStatusForSeller;
    int deliveryFee;
    int totalDistanceTraveled;
    String formattedDate;
    String notificationStatusForCustomer;

    int giftWrapCharge;
    String discountName;
    int discountAmount;
    int totalFeeForDeliveryBoy;

    public int getTotalFeeForDeliveryBoy() {
        return totalFeeForDeliveryBoy;
    }

    public void setTotalFeeForDeliveryBoy(int totalFeeForDeliveryBoy) {
        this.totalFeeForDeliveryBoy = totalFeeForDeliveryBoy;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
    }

    public int getGiftWrapCharge() {
        return giftWrapCharge;
    }

    public void setGiftWrapCharge(int giftWrapCharge) {
        this.giftWrapCharge = giftWrapCharge;
    }




    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }


    public int getDeliveryFee() {

        return deliveryFee;
    }

    public void setDeliveryFee(int deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public int getTotalDistanceTraveled() {
        return totalDistanceTraveled;
    }

    public void setTotalDistanceTraveled(int totalDistanceTraveled) {
        this.totalDistanceTraveled = totalDistanceTraveled;
    }

    public String getNotificationStatusForSeller() {
        return notificationStatusForSeller;
    }

    public void setNotificationStatusForSeller(String notificationStatusForSeller) {
        this.notificationStatusForSeller = notificationStatusForSeller;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getDeliverOtp() {
        return deliverOtp;
    }

    public void setDeliverOtp(String deliverOtp) {
        this.deliverOtp = deliverOtp;
    }
    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    String storeAddress;

    public String getCategoryTypeId() {
        return categoryTypeId;
    }

    public void setCategoryTypeId(String categoryTypeId) {
        this.categoryTypeId = categoryTypeId;
    }

    String categoryTypeId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderCreateDate() {
        return orderCreateDate;
    }

    public void setOrderCreateDate(String orderCreateDate) {
        this.orderCreateDate = orderCreateDate;
    }

    public int getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(int tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getOrderDeliveryType() {
        return orderDeliveryType;
    }

    public void setOrderDeliveryType(String orderDeliveryType) {
        this.orderDeliveryType = orderDeliveryType;
    }

    public String getInstructionsToDeliveryBoy() {
        return instructionsToDeliveryBoy;
    }

    public void setInstructionsToDeliveryBoy(String instructionsToDeliveryBoy) {
        this.instructionsToDeliveryBoy = instructionsToDeliveryBoy;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public PickUpAndDrop getPickUpAndDrop() {
        return pickUpAndDrop;
    }

    public void setPickUpAndDrop(PickUpAndDrop pickUpAndDrop) {
        this.pickUpAndDrop = pickUpAndDrop;
    }

    public List<PickUpAndDrop> getPickUpAndDroplist() {
        return pickUpAndDroplist;
    }

    public void setPickUpAndDroplist(List<PickUpAndDrop> pickUpAndDroplist) {
        this.pickUpAndDroplist = pickUpAndDroplist;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    String deliveryType;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getShippingPincode() {
        return shippingPincode;
    }

    public void setShippingPincode(String shippingPincode) {
        this.shippingPincode = shippingPincode;
    }

    public String getStorePincode() {
        return storePincode;
    }

    public void setStorePincode(String storePincode) {
        this.storePincode = storePincode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    ArrayList<ItemDetails> itemDetailList=new ArrayList<ItemDetails> ();

    public String getOrderDay() {
        return orderDay;
    }

    public void setOrderDay(String orderDay) {
        this.orderDay = orderDay;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDeliverCodeForSeller() {
        return orderDeliverCodeForSeller;
    }

    public void setOrderDeliverCodeForSeller(String orderDeliverCodeForSeller) {
        this.orderDeliverCodeForSeller = orderDeliverCodeForSeller;
    }

    public String getOrderDeliverCodeForDrliveryBoy() {
        return orderDeliverCodeForDrliveryBoy;
    }

    public void setOrderDeliverCodeForDrliveryBoy(String orderDeliverCodeForDrliveryBoy) {
        this.orderDeliverCodeForDrliveryBoy = orderDeliverCodeForDrliveryBoy;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderIdfromPaymentGateway() {
        return orderIdfromPaymentGateway;
    }

    public void setOrderIdfromPaymentGateway(String orderIdfromPaymentGateway) {
        this.orderIdfromPaymentGateway = orderIdfromPaymentGateway;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getPaymentamount() {
        return paymentamount;
    }

    public void setPaymentamount(int paymentamount) {
        this.paymentamount = paymentamount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getShippingaddress() {
        return shippingaddress;
    }

    public void setShippingaddress(String shippingaddress) {
        this.shippingaddress = shippingaddress;
    }

    public ItemDetails getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(ItemDetails itemDetails) {
        this.itemDetails = itemDetails;
    }

    public ArrayList<ItemDetails> getItemDetailList() {
        return itemDetailList;
    }

    public void setItemDetailList(ArrayList<ItemDetails> itemDetailList) {
        this.itemDetailList = itemDetailList;
    }
}