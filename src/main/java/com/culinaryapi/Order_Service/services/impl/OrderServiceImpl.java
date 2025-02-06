package com.culinaryapi.Order_Service.services.impl;


import com.culinaryapi.Order_Service.models.OrderModel;
import com.culinaryapi.Order_Service.repositories.OrderRepository;
import com.culinaryapi.Order_Service.services.OrderService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;


    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Optional<OrderModel> findById(UUID userId) {
        return orderRepository.findById(userId);
    }

    @Override
    public OrderModel save(OrderModel orderModel) {
        return orderRepository.save(orderModel);
    }
}
