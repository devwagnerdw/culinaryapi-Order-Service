//package com.culinaryapi.Order_Service.consumers;
//
//
//import com.culinaryapi.Order_Service.dtos.UserServiceEventDto;
//import com.culinaryapi.Order_Service.enums.ActionType;
//import org.springframework.amqp.core.ExchangeTypes;
//import org.springframework.amqp.rabbit.annotation.Exchange;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.annotation.QueueBinding;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
//@Component
//public class UserConsumer {
//
//
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "${Culinary.broker.queue.userServiceEventQueue.name}", durable = "true"),
//            exchange = @Exchange(value = "${Culinary.broker.exchange.userServiceEvent}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true"))
//    )
//
//}
