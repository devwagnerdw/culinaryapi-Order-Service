package com.culinaryapi.Order_Service.services;

import com.culinaryapi.Order_Service.models.OrderModel;

import java.util.Optional;
import java.util.UUID;

public interface OrderService {

    Optional<OrderModel> findById(UUID userId);

    OrderModel save(OrderModel orderModel);
}