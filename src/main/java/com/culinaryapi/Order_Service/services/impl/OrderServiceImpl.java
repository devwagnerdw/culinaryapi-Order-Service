package com.culinaryapi.Order_Service.services.impl;


import com.culinaryapi.Order_Service.repositories.OrderRepository;
import com.culinaryapi.Order_Service.services.OrderService;
import org.springframework.stereotype.Service;


@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;


    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
