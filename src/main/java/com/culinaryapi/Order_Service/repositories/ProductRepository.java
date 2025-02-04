package com.culinaryapi.Order_Service.repositories;


import com.culinaryapi.Order_Service.models.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductModel, UUID> {

}
