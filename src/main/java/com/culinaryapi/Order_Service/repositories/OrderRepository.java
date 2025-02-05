package com.culinaryapi.Order_Service.repositories;

import com.culinaryapi.Order_Service.models.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderModel, UUID> {
}
