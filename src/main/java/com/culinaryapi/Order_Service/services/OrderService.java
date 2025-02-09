package com.culinaryapi.Order_Service.services;

import com.culinaryapi.Order_Service.dtos.OrderDto;
import com.culinaryapi.Order_Service.models.OrderModel;

import java.util.Optional;
import java.util.UUID;

public interface OrderService {

    OrderModel registerOrder(OrderDto orderDto);

    OrderModel updateStatusOrder(UUID orderId, OrderDto orderDto);
}