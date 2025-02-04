package com.culinaryapi.Order_Service.dtos;

import com.culinaryapi.Order_Service.models.ProductModel;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.UUID;

public class MenuEventDto {

    private UUID productId;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private Boolean available;
    private String actionType;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public ProductModel convertToProductModel(){
        var productModel = new ProductModel();
        BeanUtils.copyProperties(this, productModel);
        return productModel;
    }
}
