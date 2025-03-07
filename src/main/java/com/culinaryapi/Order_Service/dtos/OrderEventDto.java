package com.culinaryapi.Order_Service.dtos;

import com.culinaryapi.Order_Service.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderEventDto {

    private UUID orderId;
    private UUID userId;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private String fullName;
    private String phoneNumber;
    private String actionType;
    private AddressDto addressModel;


    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public AddressDto getAddressModel() {
        return addressModel;
    }

    public void setAddressModel(AddressDto addressModel) {
        this.addressModel = addressModel;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
