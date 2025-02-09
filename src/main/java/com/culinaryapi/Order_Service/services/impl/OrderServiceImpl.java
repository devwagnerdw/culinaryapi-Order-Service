package com.culinaryapi.Order_Service.services.impl;

import com.culinaryapi.Order_Service.dtos.OrderDto;
import com.culinaryapi.Order_Service.dtos.OrderItemDTO;
import com.culinaryapi.Order_Service.enums.OrderStatus;
import com.culinaryapi.Order_Service.exception.InvalidOperationException;
import com.culinaryapi.Order_Service.exception.NotFoundException;
import com.culinaryapi.Order_Service.models.*;
import com.culinaryapi.Order_Service.repositories.AddressRepository;
import com.culinaryapi.Order_Service.repositories.OrderRepository;
import com.culinaryapi.Order_Service.repositories.ProductRepository;
import com.culinaryapi.Order_Service.repositories.UserRepository;
import com.culinaryapi.Order_Service.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, AddressRepository addressRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderModel registerOrder(OrderDto orderDto) {
        UserModel userModel = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + orderDto.getUserId()));

        AddressModel addressModel = addressRepository.findByUser_UserIdAndAddressId(orderDto.getUserId(), orderDto.getAddressId())
                .orElseThrow(() -> new NotFoundException("Address not found: " + orderDto.getAddressId()));

        Set<UUID> productIds = orderDto.getOrderItems().stream()
                .map(OrderItemDTO::getProductId)
                .collect(Collectors.toSet());

        Map<UUID, ProductModel> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(ProductModel::getProductId, product -> product));

        for (UUID productId : productIds) {
            if (!productMap.containsKey(productId)) {
                throw new NotFoundException("Product not found with ID: " + productId);
            }
        }

        BigDecimal totalAmount = orderDto.getOrderItems().stream()
                .map(orderItem -> {
                    ProductModel product = productMap.get(orderItem.getProductId());
                    return product.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Set<OrderItemModel> orderItems = new HashSet<>();
        for (OrderItemDTO orderItem : orderDto.getOrderItems()) {
            ProductModel product = productMap.get(orderItem.getProductId());

            OrderItemModel orderItemModel = new OrderItemModel();
            orderItemModel.setProduct(product);
            orderItemModel.setQuantity(orderItem.getQuantity());
            orderItems.add(orderItemModel);
        }

        OrderModel orderModel = new OrderModel();
        orderModel.setOrderDate(LocalDateTime.now());
        orderModel.setTotalAmount(totalAmount);
        orderModel.setAddress(addressModel);
        orderModel.setOrderStatus(OrderStatus.PROCESSING);
        orderModel.setUser(userModel);

        for (OrderItemModel item : orderItems) {
            item.setOrder(orderModel);
        }
        orderModel.setOrderItems(orderItems);

        orderRepository.save(orderModel);
        return orderModel;
    }

    @Override
    public OrderModel updateStatusOrder(UUID orderId, OrderDto orderDto) {
        OrderModel orderModel = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        OrderStatus currentStatus = orderModel.getOrderStatus();
        OrderStatus newStatus = orderDto.getOrderStatus();

        if (!isStatusTransitionAllowed(currentStatus, newStatus)) {
            throw new InvalidOperationException("Transition from " + currentStatus + " to " + newStatus + " is not allowed.");
        }

        orderModel.setOrderStatus(orderDto.getOrderStatus());
        orderRepository.save(orderModel);
        return orderModel;
    }

    private boolean isStatusTransitionAllowed(OrderStatus currentStatus, OrderStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                return newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.CANCELED;
            case PROCESSING:
                return newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELED;
            case SHIPPED:
                return newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.RETURNED;
            case DELIVERED:
                return newStatus == OrderStatus.RETURNED;
            case CANCELED:
            case RETURNED:
                return false;
            default:
                throw new IllegalArgumentException("Invalid current status: " + currentStatus);
        }
    }
}