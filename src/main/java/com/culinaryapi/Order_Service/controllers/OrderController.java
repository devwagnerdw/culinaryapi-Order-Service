package com.culinaryapi.Order_Service.controllers;

import com.culinaryapi.Order_Service.dtos.OrderDto;
import com.culinaryapi.Order_Service.dtos.ResponsesDto.OrderResponseDto;
import com.culinaryapi.Order_Service.models.OrderModel;
import com.culinaryapi.Order_Service.services.OrderService;
import com.culinaryapi.Order_Service.specifications.SpecificationTemplate;
import com.culinaryapi.Order_Service.utils.PermissionUtils;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Registrar novo pedido", description = "Cria um novo pedido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @PostMapping
    public ResponseEntity<Object> registerOrder(
            @RequestBody @Validated(OrderDto.OrderView.NewOrderPost.class)
            @JsonView(OrderDto.OrderView.NewOrderPost.class) OrderDto orderDto) {
        return orderService.createOrder(orderDto);
    }

    @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY','CHEF')")
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> updateStatusOrder(
            @PathVariable(value = "orderId") UUID orderId,
            @RequestBody @Validated(OrderDto.OrderView.statusPut.class) OrderDto orderDto) {
        return orderService.updateOrderStatus(orderId, orderDto);
    }

    @Operation(summary = "Listar todos os pedidos", description = "Retorna uma lista paginada de todos os pedidos. Se informado userId, retorna apenas pedidos deste usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos listados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<OrderModel>> getAllOrders(
            SpecificationTemplate.CourseSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) UUID userId) {

        Page<OrderModel> orders = (userId != null) ?
                orderService.findAllByUserId(userId, spec, pageable) :
                orderService.findAll(spec, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    @Operation(summary = "Listar pedidos de um usuário", description = "Retorna uma lista paginada dos pedidos de um usuário específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos listados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<Page<OrderModel>> getOrdersByUser(
            @PathVariable(value = "userId") UUID userId,
            @PageableDefault(page = 0, size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.getOrdersByUser(userId, pageable);
    }
}