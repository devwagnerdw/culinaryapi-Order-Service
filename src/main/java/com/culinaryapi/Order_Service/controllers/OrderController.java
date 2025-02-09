package com.culinaryapi.Order_Service.controllers;

import com.culinaryapi.Order_Service.dtos.OrderDto;
import com.culinaryapi.Order_Service.exception.NotFoundException;
import com.culinaryapi.Order_Service.models.OrderModel;
import com.culinaryapi.Order_Service.services.OrderService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderModel> registerOrder(@RequestBody @Validated(OrderDto.OrderView.NewOrderPost.class)
                                                    @JsonView(OrderDto.OrderView.NewOrderPost.class) OrderDto orderDto){
        OrderModel orderModel = orderService.registerOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderModel);
    }


    @PutMapping("/{orderId}")
    public ResponseEntity<OrderModel>updateStatusOrder(@PathVariable(value = "orderId") UUID orderId,
                                                       @RequestBody @Validated(OrderDto.OrderView.statusPut.class) OrderDto orderDto){
        OrderModel orderModel = orderService.updateStatusOrder(orderId,orderDto);
        return ResponseEntity.status(HttpStatus.OK).body(orderModel);
    }
}