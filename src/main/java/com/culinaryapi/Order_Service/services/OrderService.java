package com.culinaryapi.Order_Service.services;

import com.culinaryapi.Order_Service.dtos.OrderDto;
import com.culinaryapi.Order_Service.dtos.ResponsesDto.OrderResponseDto;
import com.culinaryapi.Order_Service.models.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface OrderService {

    ResponseEntity<Object> createOrder(OrderDto orderDto);

    ResponseEntity<OrderResponseDto> updateOrderStatus(UUID orderId, OrderDto orderDto);

    Page<OrderModel> findAllByUserId(UUID userId, Specification<OrderModel> spec, Pageable pageable);

    Page<OrderModel> findAll(Specification<OrderModel> spec, Pageable pageable);

    ResponseEntity<Page<OrderModel>> getOrdersByUser(UUID userId, Pageable pageable);
}


