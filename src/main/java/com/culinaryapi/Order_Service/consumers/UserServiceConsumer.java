package com.culinaryapi.Order_Service.consumers;


import com.culinaryapi.Order_Service.dtos.UserServiceEventDto;
import com.culinaryapi.Order_Service.enums.ActionType;
import com.culinaryapi.Order_Service.models.AddressModel;
import com.culinaryapi.Order_Service.models.UserModel;
import com.culinaryapi.Order_Service.services.AddressService;
import com.culinaryapi.Order_Service.services.UserService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserServiceConsumer {

    private final AddressService addressService;
    private final UserService userService;

    public UserServiceConsumer(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${Culinary.broker.queue.userServiceEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${Culinary.broker.exchange.userServiceEventExchange}", type = ExchangeTypes.DIRECT, ignoreDeclarationExceptions = "true"),
            key = "user.service.event") // Routing key
    )
    public void listenUserEvent(@Payload UserServiceEventDto userServiceEventDto) {

        Optional<UserModel> optionalUser = userService.findById(userServiceEventDto.getUserId());
        if (optionalUser.isEmpty()) {
            return;
        }
        UserModel userModel = optionalUser.get();
        AddressModel addressModel = convertToAddressModel(userServiceEventDto, userModel);

        switch (ActionType.valueOf(userServiceEventDto.getActionType())) {
            case CREATE:
            case UPDATE:
                addressService.save(addressModel);
                break;
            case DELETE:
                break;
        }
    }

    private AddressModel convertToAddressModel(UserServiceEventDto userServiceEventDto, UserModel userModel) {
        var addressModel = new AddressModel();
        addressModel.setAddressId(userServiceEventDto.getAddressId());
        addressModel.setStreet(userServiceEventDto.getStreet());
        addressModel.setCity(userServiceEventDto.getCity());
        addressModel.setState(userServiceEventDto.getState());
        addressModel.setPostalCode(userServiceEventDto.getPostalCode());
        addressModel.setCountry(userServiceEventDto.getCountry());
        addressModel.setUser(userModel);
        return addressModel;
    }
}