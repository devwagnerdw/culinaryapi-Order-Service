package com.culinaryapi.Order_Service.dtos;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class OrderItemDTO {

    @NotNull(groups = OrderDto.OrderView.NewOrderPost.class)
    @JsonView(OrderDto.OrderView.NewOrderPost.class)
    private UUID productId;

    @NotNull(groups = OrderDto.OrderView.NewOrderPost.class)
    @JsonView(OrderDto.OrderView.NewOrderPost.class)
    @Min(1)
    private Integer quantity;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
