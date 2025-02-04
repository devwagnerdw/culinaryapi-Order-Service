package com.culinaryapi.Order_Service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "TB_Product")
public class ProductModel {

    @Id
    private UUID productId;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column
    private String description;

    @Column
    private String category;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean available;
}
