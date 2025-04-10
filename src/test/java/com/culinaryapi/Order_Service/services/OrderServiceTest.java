package com.culinaryapi.Order_Service.services;

import com.culinaryapi.Order_Service.configs.security.UserDetailsImpl;
import com.culinaryapi.Order_Service.dtos.OrderDto;
import com.culinaryapi.Order_Service.dtos.OrderItemDTO;
import com.culinaryapi.Order_Service.enums.OrderStatus;
import com.culinaryapi.Order_Service.models.*;
import com.culinaryapi.Order_Service.repositories.*;
import com.culinaryapi.Order_Service.utils.PermissionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PermissionUtils permissionUtils;

    @Nested
    @DisplayName("Tests for createOrder")
    class CreateOrderTests {

        @Test
        @DisplayName("Should return CREATED when order is successfully created")
        void createOrder_success() {
            UserModel user = new UserModel();
            user.setUserId(UUID.randomUUID());
            user.setUserStatus("ACTIVE");
            userRepository.save(user);

            UserDetailsImpl userDetails = UserDetailsImpl.build(user.getUserId(), "ROLE_USER");
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            AddressModel address = new AddressModel();
            address.setAddressId(UUID.randomUUID());
            address.setUser(user);
            addressRepository.save(address);

            ProductModel product = new ProductModel();
            product.setProductId(UUID.randomUUID());
            product.setName("product-1");
            product.setDescription("test...");
            product.setCategory("sobremesa");
            product.setPrice(new BigDecimal("29.99"));
            product.setAvailable(true);
            productRepository.save(product);

            OrderItemDTO itemDto = new OrderItemDTO();
            itemDto.setProductId(product.getProductId());
            itemDto.setQuantity(2);

            OrderDto orderDto = new OrderDto();
            orderDto.setUserId(user.getUserId());
            orderDto.setAddressId(address.getAddressId());
            orderDto.setOrderItems(Set.of(itemDto));

            ResponseEntity<Object> response = orderService.createOrder(orderDto);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
        }
    }
    }
