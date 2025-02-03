package com.culinaryapi.Order_Service.services.impl;


import com.culinaryapi.Order_Service.models.AddressModel;
import com.culinaryapi.Order_Service.repositories.AddressRepository;
import com.culinaryapi.Order_Service.services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressRepository addressRepository;

    @Override
    public void save(AddressModel addressModel) {
        addressRepository.save(addressModel);
    }
}
