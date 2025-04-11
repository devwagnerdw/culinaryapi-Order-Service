package com.culinaryapi.Order_Service.services.impl;

import com.culinaryapi.Order_Service.dtos.OrderDto;
import com.culinaryapi.Order_Service.dtos.OrderItemDTO;
import com.culinaryapi.Order_Service.dtos.ResponsesDto.OrderResponseDto;
import com.culinaryapi.Order_Service.enums.ActionType;
import com.culinaryapi.Order_Service.enums.OrderStatus;
import com.culinaryapi.Order_Service.exception.InvalidOperationException;
import com.culinaryapi.Order_Service.exception.NotFoundException;
import com.culinaryapi.Order_Service.models.*;
import com.culinaryapi.Order_Service.publishers.OrderEventPublisher;
import com.culinaryapi.Order_Service.repositories.AddressRepository;
import com.culinaryapi.Order_Service.repositories.OrderRepository;
import com.culinaryapi.Order_Service.repositories.ProductRepository;
import com.culinaryapi.Order_Service.repositories.UserRepository;
import com.culinaryapi.Order_Service.services.OrderService;
import com.culinaryapi.Order_Service.specifications.SpecificationTemplate;
import com.culinaryapi.Order_Service.utils.PermissionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final PermissionUtils permissionUtils;
    private final OrderEventPublisher orderEventPublisher;

    public OrderServiceImpl(OrderRepository orderRepository, AddressRepository addressRepository, UserRepository userRepository, ProductRepository productRepository, PermissionUtils permissionUtils, OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.permissionUtils = permissionUtils;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    public ResponseEntity<Object> createOrder(OrderDto orderDto) {
        UserModel userModel = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + orderDto.getUserId()));

        if (!permissionUtils.hasPermission(userModel.getUserId())) {
            throw new InvalidOperationException("You are not allowed to create a new order for another user.");
        }

        if ("BLOCKED".equalsIgnoreCase(userModel.getUserStatus())) {
            throw new InvalidOperationException("User is blocked and cannot place orders.");
        }

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
        orderModel.setOrderStatus(OrderStatus.PENDING);
        orderModel.setUser(userModel);

        for (OrderItemModel item : orderItems) {
            item.setOrder(orderModel);
        }
        orderModel.setOrderItems(orderItems);

        orderRepository.save(orderModel);

      return  ResponseEntity.status(HttpStatus.CREATED).body(orderModel.convertToOrderResponseDto());
    }

    @Override
    public ResponseEntity<OrderResponseDto>  updateOrderStatus(UUID orderId, OrderDto orderDto) {
        OrderModel orderModel = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        OrderStatus newStatus = orderDto.getOrderStatus();

        orderModel.setOrderStatus(newStatus);
        orderRepository.save(orderModel);

        if (newStatus == OrderStatus.READY_FOR_PICKUP) {
            orderEventPublisher.publishOrderEvent(orderModel.convertToOrderEventDto(), ActionType.CREATE);
        }

        return ResponseEntity.ok(orderModel.convertToOrderResponseDto());
    }

    @Override
    public Page<OrderModel> findAllByUserId(UUID userId, Specification<OrderModel> spec, Pageable pageable) {
        return orderRepository.findAll(SpecificationTemplate.byUserId(userId).and(spec), pageable);
    }

    @Override
    public Page<OrderModel> findAll(Specification<OrderModel> spec, Pageable pageable) {
        return orderRepository.findAll(spec, pageable);
    }

    @Override
    public ResponseEntity<Page<OrderModel>> getOrdersByUser(UUID userId, Pageable pageable) {
        Page<OrderModel> orders = orderRepository.findByUser_UserId(userId, pageable);
        if (!permissionUtils.hasPermission(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(orders);

    }

}