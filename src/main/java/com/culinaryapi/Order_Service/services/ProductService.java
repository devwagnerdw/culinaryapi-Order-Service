package com.culinaryapi.Order_Service.services;

import com.culinaryapi.Order_Service.models.ProductModel;

import java.util.Optional;
import java.util.UUID;

public interface ProductService {

    void save(ProductModel productModel);

    Optional<ProductModel> findById(UUID productId);
}
