package com.culinaryapi.Order_Service.services;



import com.culinaryapi.Order_Service.models.UserModel;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    void save(UserModel userModel);
    Optional<UserModel> findById(UUID userId);
}
