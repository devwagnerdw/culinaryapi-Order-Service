package com.culinaryapi.Order_Service.controllers;

import com.culinaryapi.Order_Service.dtos.OrderDto;
import com.culinaryapi.Order_Service.dtos.OrderItemDTO;
import com.culinaryapi.Order_Service.models.*;
import com.culinaryapi.Order_Service.services.AddressService;
import com.culinaryapi.Order_Service.services.OrderService;
import com.culinaryapi.Order_Service.services.ProductService;
import com.culinaryapi.Order_Service.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final UserService userService;
    private final AddressService addressService;
    private final OrderService orderService;
    private final ProductService productService;

    public OrderController(UserService userService, AddressService addressService, OrderService orderService, ProductService productService) {
        this.userService = userService;
        this.addressService = addressService;
        this.orderService = orderService;
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Object> registerOrder(@RequestBody @Validated OrderDto orderDto) {
        Optional<AddressModel> optionalAddressModel = addressService.findById(orderDto.getAddressId());
        if (optionalAddressModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Address not found");
        }
        Optional<UserModel> optionalUserModel = userService.findById(orderDto.getUserId());
        if (optionalUserModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<OrderItemModel> orderItems = new HashSet<>();

        for (OrderItemDTO orderItem : orderDto.getOrderItems()) {
            Optional<ProductModel> optionalProductModel = productService.findById(orderItem.getProductId());
            if (optionalProductModel.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product not found with ID: " + orderItem.getProductId());
            }

            ProductModel product = optionalProductModel.get();
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItemModel orderItemModel = new OrderItemModel();
            orderItemModel.setProduct(product);
            orderItemModel.setQuantity(orderItem.getQuantity());
            orderItems.add(orderItemModel);
        }

        OrderModel orderModel = new OrderModel();
        orderModel.setOrderDate(LocalDateTime.now());
        orderModel.setTotalAmount(totalAmount);
        orderModel.setAddress(optionalAddressModel.get());
        orderModel.setStatus("ABERTO");
        orderModel.setUser(optionalUserModel.get());

        OrderModel savedOrder = orderService.save(orderModel);

        for (OrderItemModel item : orderItems) {
            item.setOrder(savedOrder);
        }

        savedOrder.setOrderItems(orderItems);
        orderService.save(savedOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }


}
