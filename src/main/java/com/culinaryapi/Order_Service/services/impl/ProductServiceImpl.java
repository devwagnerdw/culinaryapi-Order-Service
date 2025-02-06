package com.culinaryapi.Order_Service.services.impl;

import com.culinaryapi.Order_Service.models.ProductModel;
import com.culinaryapi.Order_Service.repositories.ProductRepository;
import com.culinaryapi.Order_Service.services.ProductService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void save(ProductModel productModel) {
        productRepository.save(productModel);
    }

    @Override
    public Optional<ProductModel> findById(UUID productId) {
        return productRepository.findById(productId);
    }
}
