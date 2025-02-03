package com.culinaryapi.Order_Service.services;



import com.culinaryapi.Order_Service.model.UserModel;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    void save(UserModel userModel);

    void delete(UUID userId);

    Optional<UserModel> findById(UUID userId);
}
