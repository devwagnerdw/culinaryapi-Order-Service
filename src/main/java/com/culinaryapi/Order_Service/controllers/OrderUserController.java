package com.culinaryapi.Order_Service.controllers;

import com.culinaryapi.Order_Service.models.OrderModel;
import com.culinaryapi.Order_Service.services.OrderService;
import com.culinaryapi.Order_Service.specifications.SpecificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class OrderUserController {

    private final OrderService orderService;

    public OrderUserController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order/user/{userId}")
    public ResponseEntity<Object> getAllOrdersByUser(
            @PathVariable(value = "userId") UUID userId,
            SpecificationTemplate.CourseSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<OrderModel> orders = orderService.findAllByUserId(userId, spec, pageable);
        return ResponseEntity.ok(orders);
    }
}