package com.culinaryapi.Order_Service.repositories;

import com.culinaryapi.Order_Service.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel,UUID> {
}
