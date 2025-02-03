package com.culinaryapi.Order_Service.consumers;


import com.culinaryapi.Order_Service.dtos.UserEventDto;
import com.culinaryapi.Order_Service.dtos.UserServiceEventDto;
import com.culinaryapi.Order_Service.enums.ActionType;
import com.culinaryapi.Order_Service.model.AddressModel;
import com.culinaryapi.Order_Service.model.UserModel;
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
import java.util.UUID;

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
            exchange = @Exchange(value = "${Culinary.broker.exchange.userServiceEvent}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true"))
    )
    public void listenUserEvent(@Payload UserServiceEventDto userServiceEventDto) {

        if (userServiceEventDto.getUserId() == null) {
            System.err.println("Erro: userId é nulo. Mensagem descartada.");
            return;
        }
        System.out.println("🔍 Detalhes do DTO recebido:");
        System.out.println("UserId: " + userServiceEventDto.getUserId());
        System.out.println("ActionType: " + userServiceEventDto.getActionType());
        System.out.println("AddressId: " + userServiceEventDto.getAddressId());
        System.out.println("Street: " + userServiceEventDto.getStreet());
        System.out.println("City: " + userServiceEventDto.getCity());
        System.out.println("State: " + userServiceEventDto.getState());
        System.out.println("PostalCode: " + userServiceEventDto.getPostalCode());
        System.out.println("Country: " + userServiceEventDto.getCountry());

        UUID userId = userServiceEventDto.getUserId();
        Optional<UserModel> optionalUser = userService.findById(userId);

        if (optionalUser.isEmpty()) {
            return;
        }

        UserModel user = optionalUser.get();
        AddressModel addressModel = convertToAddressModel(userServiceEventDto, user);

        switch (ActionType.valueOf(userServiceEventDto.getActionType())) {
            case CREATE:
            case UPDATE:
                addressService.save(addressModel);
                break;
            case DELETE:
                break;
        }
    }

    private AddressModel convertToAddressModel(UserServiceEventDto dto, UserModel user) {
        AddressModel address = new AddressModel();
        address.setAddressId(dto.getAddressId());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setUser(user);
        return address;
    }
}