package com.culinaryapi.Order_Service.models;

import com.culinaryapi.Order_Service.dtos.*;
import com.culinaryapi.Order_Service.dtos.ResponsesDto.*;
import com.culinaryapi.Order_Service.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "TB_Order")
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private AddressModel address;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<OrderItemModel> orderItems;


    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }

    public Set<OrderItemModel> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItemModel> orderItems) {
        this.orderItems = orderItems;
    }

    public OrderEventDto convertToOrderEventDto() {
        var orderEventDto = new OrderEventDto();
        orderEventDto.setOrderId(orderId);
        orderEventDto.setUserId(user.getUserId());
        orderEventDto.setOrderStatus(orderStatus);
        orderEventDto.setTotalAmount(totalAmount);
        orderEventDto.setFullName(user.getFullName());
        orderEventDto.setPhoneNumber(user.getPhoneNumber());

        var addressDto = new AddressDto();
        addressDto.setAddressId(address.getAddressId());
        addressDto.setStreet(address.getStreet());
        addressDto.setCity(address.getCity());
        addressDto.setState(address.getState());
        addressDto.setPostalCode(address.getPostalCode());
        addressDto.setCountry(address.getCountry());

        orderEventDto.setAddressModel(addressDto);
        return orderEventDto;

    }


    public OrderResponseDto convertToOrderResponseDto() {
        return new OrderResponseDto(
                orderId,
                orderDate,
                orderStatus,
                totalAmount.doubleValue(),
                user,
                new AddressResponseDto(
                        address.getAddressId(),
                        address.getStreet(),
                        address.getCity(),
                        address.getState(),
                        address.getPostalCode(),
                        address.getCountry()
                ),
                orderItems.stream()
                        .map(orderItem -> new OrderItemResponseDto( // Usando OrderItemResponseDto
                                orderItem.getQuantity(),
                                new ProductResponseDto(
                                        orderItem.getProduct().getProductId(),
                                        orderItem.getProduct().getName(),
                                        orderItem.getProduct().getDescription(),
                                        orderItem.getProduct().getCategory(),
                                        orderItem.getProduct().getPrice().doubleValue(),
                                        orderItem.getProduct().getAvailable()
                                )
                        ))
                        .collect(Collectors.toList())
        );
    }


}
