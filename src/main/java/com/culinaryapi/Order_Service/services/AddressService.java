package com.culinaryapi.Order_Service.services;

import com.culinaryapi.Order_Service.models.AddressModel;

import java.util.Optional;
import java.util.UUID;

public interface AddressService {

    void save(AddressModel addressModel);

    Optional<AddressModel> findById(UUID addressId);

    Optional<AddressModel> findByUserIdAndAddressId(UUID userId, UUID addressId);

}