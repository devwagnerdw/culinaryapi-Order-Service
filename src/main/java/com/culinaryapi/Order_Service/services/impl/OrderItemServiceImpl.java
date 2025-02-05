package com.culinaryapi.Order_Service.services.impl;


import com.culinaryapi.Order_Service.repositories.OrderItemRepository;
import com.culinaryapi.Order_Service.services.OrderItemService;
import org.springframework.stereotype.Service;


@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }
}
