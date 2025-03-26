package com.culinaryapi.Order_Service.dtos;


import com.culinaryapi.Order_Service.enums.OrderStatus;

import java.util.UUID;

public class DeliveryEventDto {

    private UUID orderId;
    private OrderStatus orderStatus;
    private String actionType;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
