package com.culinaryapi.Order_Service.services;

import com.culinaryapi.Order_Service.configs.security.UserDetailsImpl;
import com.culinaryapi.Order_Service.dtos.OrderDto;
import com.culinaryapi.Order_Service.dtos.OrderItemDTO;
import com.culinaryapi.Order_Service.enums.OrderStatus;
import com.culinaryapi.Order_Service.exception.InvalidOperationException;
import com.culinaryapi.Order_Service.exception.NotFoundException;
import com.culinaryapi.Order_Service.models.*;
import com.culinaryapi.Order_Service.repositories.*;
import com.culinaryapi.Order_Service.utils.PermissionUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    private UserModel createUser(String status) {
        UserModel user = new UserModel();
        user.setUserId(UUID.randomUUID());
        user.setUserStatus(status);
        return userRepository.save(user);
    }

    private void authenticate(UserModel user) {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user.getUserId(), "ROLE_USER");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private AddressModel createAddress(UserModel user) {
        AddressModel address = new AddressModel();
        address.setAddressId(UUID.randomUUID());
        address.setUser(user);
        return addressRepository.save(address);
    }

    private ProductModel createProduct() {
        ProductModel product = new ProductModel();
        product.setProductId(UUID.randomUUID());
        product.setName("product-1");
        product.setDescription("test...");
        product.setCategory("sobremesa");
        product.setPrice(new BigDecimal("29.99"));
        product.setAvailable(true);
        return productRepository.save(product);
    }

    private OrderDto createOrderDto(UserModel user, AddressModel address, ProductModel product) {
        OrderItemDTO itemDto = new OrderItemDTO();
        itemDto.setProductId(product.getProductId());
        itemDto.setQuantity(2);

        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(user.getUserId());
        orderDto.setAddressId(address.getAddressId());
        orderDto.setOrderItems(Set.of(itemDto));
        return orderDto;
    }

    @Nested
    @DisplayName("Tests for createOrder")
    class CreateOrderTests {

        @Test
        void shouldCreateOrderSuccessfully() {
            UserModel user = createUser("ACTIVE");
            authenticate(user);
            AddressModel address = createAddress(user);
            ProductModel product = createProduct();
            OrderDto orderDto = createOrderDto(user, address, product);

            ResponseEntity<Object> response = orderService.createOrder(orderDto);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        @Test
        void shouldThrowWhenUserBlocked() {
            UserModel user = createUser("BLOCKED");
            authenticate(user);
            AddressModel address = createAddress(user);
            ProductModel product = createProduct();
            OrderDto orderDto = createOrderDto(user, address, product);

            InvalidOperationException exception = assertThrows(InvalidOperationException.class, () -> orderService.createOrder(orderDto));
            assertThat(exception.getMessage()).isEqualTo("User is blocked and cannot place orders.");
        }

        @Test
        void shouldThrowWhenUserNotFound() {
            UUID fakeId = UUID.randomUUID();
            OrderDto orderDto = new OrderDto();
            orderDto.setUserId(fakeId);
            orderDto.setAddressId(UUID.randomUUID());
            orderDto.setOrderItems(new HashSet<>());

            assertThrows(NotFoundException.class, () -> orderService.createOrder(orderDto));
        }

        @Test
        void shouldThrowWhenProductNotFound() {
            UserModel user = createUser("ACTIVE");
            authenticate(user);
            AddressModel address = createAddress(user);

            OrderItemDTO itemDto = new OrderItemDTO();
            itemDto.setProductId(UUID.randomUUID());
            itemDto.setQuantity(1);

            OrderDto orderDto = new OrderDto();
            orderDto.setUserId(user.getUserId());
            orderDto.setAddressId(address.getAddressId());
            orderDto.setOrderItems(Set.of(itemDto));

            assertThrows(NotFoundException.class, () -> orderService.createOrder(orderDto));
        }

        @Test
        void shouldThrowWhenNoPermission() {
            UserModel user = createUser("ACTIVE");
            UserModel anotherUser = createUser("ACTIVE");
            authenticate(anotherUser);
            AddressModel address = createAddress(user);
            ProductModel product = createProduct();
            OrderDto orderDto = createOrderDto(user, address, product);

            assertThrows(InvalidOperationException.class, () -> orderService.createOrder(orderDto));
        }
    }

    @Nested
    @DisplayName("Tests for updateOrderStatus")
    class UpdateOrderStatusTests {

        @Test
        void shouldUpdateOrderStatus() {
            UserModel user = createUser("ACTIVE");
            authenticate(user);
            AddressModel address = createAddress(user);
            ProductModel product = createProduct();
            OrderDto orderDto = createOrderDto(user, address, product);
            ResponseEntity<Object> response = orderService.createOrder(orderDto);
            OrderModel savedOrder = orderRepository.findAll().get(0);

            OrderDto updateDto = new OrderDto();
            updateDto.setOrderStatus(OrderStatus.DELIVERED);

            var updated = orderService.updateOrderStatus(savedOrder.getOrderId(), updateDto);
            assertThat(updated.getBody().getOrderStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        void shouldThrowWhenOrderNotFound() {
            OrderDto dto = new OrderDto();
            dto.setOrderStatus(OrderStatus.READY_FOR_PICKUP);

            assertThrows(NotFoundException.class, () -> orderService.updateOrderStatus(UUID.randomUUID(), dto));
        }
    }

    @Nested
    @DisplayName("Tests for getOrdersByUser")
    class GetOrdersByUserTests {

        @Test
        void shouldReturnOrdersWhenAuthorized() {
            UserModel user = createUser("ACTIVE");
            authenticate(user);
            Pageable pageable = PageRequest.of(0, 10);

            ResponseEntity<Page<OrderModel>> response = orderService.getOrdersByUser(user.getUserId(), pageable);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void shouldReturnForbiddenWhenNotAuthorized() {
            UserModel user = createUser("ACTIVE");
            UserModel other = createUser("ACTIVE");
            authenticate(other);
            Pageable pageable = PageRequest.of(0, 10);

            ResponseEntity<Page<OrderModel>> response = orderService.getOrdersByUser(user.getUserId(), pageable);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }
}
