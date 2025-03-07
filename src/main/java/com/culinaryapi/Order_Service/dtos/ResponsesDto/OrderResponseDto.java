package com.culinaryapi.Order_Service.dtos.ResponsesDto;

import com.culinaryapi.Order_Service.dtos.AddressDto;
import com.culinaryapi.Order_Service.dtos.OrderItemDTO;
import com.culinaryapi.Order_Service.enums.OrderStatus;
import com.culinaryapi.Order_Service.models.UserModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponseDto {
    private UUID orderId;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime orderDate;

    private OrderStatus orderStatus;
    private Double totalAmount;
    private UserModel user;
    private AddressResponseDto address;
    private List<OrderItemResponseDto> orderItems;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public List<OrderItemResponseDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemResponseDto> orderItems) {
        this.orderItems = orderItems;
    }

    public AddressResponseDto getAddress() {
        return address;
    }

    public void setAddress(AddressResponseDto address) {
        this.address = address;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderResponseDto(UUID orderId, LocalDateTime orderDate, OrderStatus orderStatus, Double totalAmount, UserModel user, AddressResponseDto address, List<OrderItemResponseDto> orderItems) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.user = user;
        this.address = address;
        this.orderItems = orderItems;
    }
}
