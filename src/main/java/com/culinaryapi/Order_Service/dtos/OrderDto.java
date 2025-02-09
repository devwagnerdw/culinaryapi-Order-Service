package com.culinaryapi.Order_Service.dtos;

import com.culinaryapi.Order_Service.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public class OrderDto {

    public interface OrderView {
        public static interface NewOrderPost {}
        public static interface statusPut {}
    }
    @NotNull(groups = OrderView.NewOrderPost.class)
    @JsonView(OrderView.NewOrderPost.class)
    private UUID userId;

    @NotNull(groups = OrderView.NewOrderPost.class)
    @JsonView(OrderView.NewOrderPost.class)
    private UUID addressId;

    @NotNull(groups = OrderView.statusPut.class)
    @JsonView(OrderView.statusPut.class)
    private OrderStatus orderStatus;

    @JsonView({OrderView.NewOrderPost.class})
    private Set<OrderItemDTO> orderItems;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getAddressId() {
        return addressId;
    }

    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

    public @NotNull(groups = OrderView.statusPut.class) OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(@NotNull(groups = OrderView.statusPut.class) OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Set<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}