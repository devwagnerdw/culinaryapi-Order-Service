package com.culinaryapi.Order_Service.publishers;


import com.culinaryapi.Order_Service.dtos.OrderEventDto;
import com.culinaryapi.Order_Service.enums.ActionType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {


    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    @Value(value="${Culinary.broker.exchange.orderServiceEventExchange}" )
    private String exchangeOrderEvent;

    public void publishOrderEvent(OrderEventDto orderEventDto, ActionType actionType) {
        orderEventDto.setActionType(actionType.toString());
        rabbitTemplate.convertAndSend(exchangeOrderEvent, "order.service.event", orderEventDto);
    }

}
