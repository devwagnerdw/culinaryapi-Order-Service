package com.culinaryapi.Order_Service.consumers;


import com.culinaryapi.Order_Service.dtos.MenuEventDto;
import com.culinaryapi.Order_Service.enums.ActionType;
import com.culinaryapi.Order_Service.services.ProductService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class MenuConsumer {


    private final ProductService productService;

    public MenuConsumer(ProductService productService) {
        this.productService = productService;
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${Culinary.broker.queue.menuServiceEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${Culinary.broker.exchange.menuEventExchange}", type = ExchangeTypes.DIRECT, ignoreDeclarationExceptions = "true"),
            key = "menu.service.event")
    )


    public void listenMenuEvent(@Payload MenuEventDto menuEventDto){


       var productModel = menuEventDto.convertToProductModel();

        switch (ActionType.valueOf(menuEventDto.getActionType())){
            case CREATE:
            case UPDATE:
                productService.save(productModel);
                break;
            case DELETE:
                break;
        }
    }
}
