package com.culinaryapi.Order_Service.repositories;

import com.culinaryapi.Order_Service.models.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderModel, UUID>, JpaSpecificationExecutor<OrderModel> {

    Page<OrderModel> findByUser_UserId(UUID userId, Pageable pageable);

}
