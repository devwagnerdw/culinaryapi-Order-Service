package com.culinaryapi.Order_Service.consumers;

import com.culinaryapi.Order_Service.dtos.DeliveryEventDto;
import com.culinaryapi.Order_Service.enums.ActionType;
import com.culinaryapi.Order_Service.enums.OrderStatus;
import com.culinaryapi.Order_Service.exception.NotFoundException;
import com.culinaryapi.Order_Service.models.OrderModel;
import com.culinaryapi.Order_Service.repositories.OrderRepository;
import com.culinaryapi.Order_Service.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeliveryConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryConsumer.class);
    private final OrderRepository orderRepository;

    public DeliveryConsumer(OrderService orderService, OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${Culinary.broker.queue.deliveryServiceEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${Culinary.broker.exchange.deliveryEventExchange}", type = ExchangeTypes.DIRECT, ignoreDeclarationExceptions = "true"),
            key = "delivery.service.event")
    )
    public void handleDeliveryEvent(@Payload DeliveryEventDto deliveryEventDto) {
        logger.info("Received delivery event: {}", deliveryEventDto);

        try {
            ActionType actionType = ActionType.valueOf(deliveryEventDto.getActionType());
            logger.info("Processing action type: {} for order ID: {}", actionType, deliveryEventDto.getOrderId());

            switch (actionType) {
                case CREATE:
                case UPDATE:
                    updateOrderStatus(deliveryEventDto.getOrderId(), deliveryEventDto.getOrderStatus());
                    break;
                case DELETE:
                    logger.warn("Delete action received, but no operation is defined.");
                    break;
                default:
                    logger.error("Unknown action type: {}", actionType);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid action type received: {}", deliveryEventDto.getActionType(), e);
        } catch (Exception e) {
            logger.error("Error processing delivery event: {}", deliveryEventDto, e);
        }
    }

    private void updateOrderStatus(UUID orderId, OrderStatus orderStatus) {
        logger.info("Updating order status for order ID: {} to {}", orderId, orderStatus);

        OrderModel order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found: {}", orderId);
                    return new NotFoundException("Order not found: " + orderId);
                });

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        logger.info("Order ID: {} updated successfully to status: {}", orderId, orderStatus);
    }
}
