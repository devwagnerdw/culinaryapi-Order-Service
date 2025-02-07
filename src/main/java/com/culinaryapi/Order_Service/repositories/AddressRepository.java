package com.culinaryapi.Order_Service.repositories;

import com.culinaryapi.Order_Service.models.AddressModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<AddressModel, UUID> {
    Page<AddressModel> findByUserUserId(UUID userId, Pageable pageable);

    Optional<AddressModel> findByUser_UserIdAndAddressId(UUID userId, UUID addressId);

}
