package com.culinaryapi.Order_Service.dtos.ResponsesDto;

public class OrderItemResponseDto {

    private Integer quantity;
    private ProductResponseDto product;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ProductResponseDto getProduct() {
        return product;
    }

    public void setProduct(ProductResponseDto product) {
        this.product = product;
    }

    public OrderItemResponseDto(Integer quantity, ProductResponseDto product) {
        this.quantity = quantity;
        this.product = product;
    }

}