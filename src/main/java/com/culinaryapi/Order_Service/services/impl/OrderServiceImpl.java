package com.culinaryapi.Order_Service.services.impl;


import com.culinaryapi.Order_Service.dtos.OrderDto;
import com.culinaryapi.Order_Service.dtos.OrderItemDTO;
import com.culinaryapi.Order_Service.enums.OrderStatus;
import com.culinaryapi.Order_Service.exception.NotFoundException;
import com.culinaryapi.Order_Service.models.*;
import com.culinaryapi.Order_Service.repositories.AddressRepository;
import com.culinaryapi.Order_Service.repositories.OrderRepository;
import com.culinaryapi.Order_Service.repositories.ProductRepository;
import com.culinaryapi.Order_Service.repositories.UserRepository;
import com.culinaryapi.Order_Service.services.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


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

        Optional<UserModel> userModel = userRepository.findById(orderDto.getUserId());
        if (userModel.isEmpty()) {
            throw new NotFoundException("User not found" + orderDto.getUserId());
        }

        Optional<AddressModel> addressModel = addressRepository.findByUser_UserIdAndAddressId(orderDto.getUserId(), orderDto.getAddressId());
        if (addressModel.isEmpty()) {
            throw new NotFoundException("Address not found");
        }



        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<OrderItemModel> orderItems = new HashSet<>();

        for (OrderItemDTO orderItem : orderDto.getOrderItems()) {
            Optional<ProductModel> optionalProductModel = productRepository.findById(orderItem.getProductId());
            if (optionalProductModel.isEmpty()) {
                throw new NotFoundException("Product not found with ID: " + orderItem.getProductId());
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
        orderModel.setAddress(addressModel.get());
        orderModel.setOrderStatus(OrderStatus.PROCESSING);
        orderModel.setUser(userModel.get());

        for (OrderItemModel item : orderItems) {
            item.setOrder(orderModel);
        }
        orderModel.setOrderItems(orderItems);
        orderRepository.save(orderModel);
        return orderModel;


    }
}
