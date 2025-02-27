package com.culinaryapi.Order_Service.controllers;

import com.culinaryapi.Order_Service.dtos.OrderDto;
import com.culinaryapi.Order_Service.exception.NotFoundException;
import com.culinaryapi.Order_Service.models.OrderModel;
import com.culinaryapi.Order_Service.services.OrderService;
import com.culinaryapi.Order_Service.specifications.SpecificationTemplate;
import com.culinaryapi.Order_Service.utils.PermissionUtils;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final PermissionUtils permissionUtils;

    public OrderController(OrderService orderService, PermissionUtils permissionUtils) {
        this.orderService = orderService;
        this.permissionUtils = permissionUtils;
    }


    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @PostMapping
    public ResponseEntity<Object> registerOrder(@RequestBody @Validated(OrderDto.OrderView.NewOrderPost.class)
                                                    @JsonView(OrderDto.OrderView.NewOrderPost.class) OrderDto orderDto){
        return orderService.registerOrder(orderDto);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY','CHEF')")
    @PutMapping("/{orderId}")
    public ResponseEntity<Object>updateStatusOrder(@PathVariable(value = "orderId") UUID orderId,
                                                       @RequestBody @Validated(OrderDto.OrderView.statusPut.class) OrderDto orderDto){
        return orderService.updateStatusOrder(orderId, orderDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<OrderModel>> getAllOrders(
            SpecificationTemplate.CourseSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) UUID userId) {

        Page<OrderModel> orders = (userId != null) ? orderService.findAllByUserId(userId, spec, pageable) : orderService.findAll(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<Page<OrderModel>>getOrdersByUser(
            @PathVariable(value = "userId") UUID userId,
            @PageableDefault(page = 0, size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable
    ){
      return   orderService.getOrdersByUser(userId, pageable);
    }
}