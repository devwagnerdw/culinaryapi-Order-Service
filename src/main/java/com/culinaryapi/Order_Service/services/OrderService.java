package com.culinaryapi.Order_Service.services;

import com.culinaryapi.Order_Service.dtos.OrderDto;
import com.culinaryapi.Order_Service.models.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface OrderService {

    OrderModel registerOrder(OrderDto orderDto);

    OrderModel updateStatusOrder(UUID orderId, OrderDto orderDto);

    Page<OrderModel> findAllByUserId(UUID userId, Specification<OrderModel> spec, Pageable pageable);
}