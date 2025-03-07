package com.culinaryapi.Order_Service.consumers;


import com.culinaryapi.Order_Service.dtos.UserEventDto;
import com.culinaryapi.Order_Service.enums.ActionType;
import com.culinaryapi.Order_Service.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer {


    private  final UserService userService;

    public UserConsumer(UserService userService) {
        this.userService = userService;
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${Culinary.broker.queue.userEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${Culinary.broker.exchange.userEventExchange}", type = ExchangeTypes.DIRECT, ignoreDeclarationExceptions = "true"),
            key = "user.service.event") // Routing key
    )
    public void listenUserEvent(@Payload UserEventDto userEventDto){

       var userModel = userEventDto.convertToUserModel();

        switch (ActionType.valueOf(userEventDto.getActionType())){
            case CREATE:
            case UPDATE:
                userService.save(userModel);
                break;
            case DELETE:
                break;
        }
    }
}
