package com.culinaryapi.Order_Service.services;



import com.culinaryapi.Order_Service.model.AddressModel;

import java.util.UUID;

public interface AddressService {

    void save(AddressModel addressModel);
}