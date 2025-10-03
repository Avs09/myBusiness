package com.myBusiness.domain.port;

import com.myBusiness.domain.model.Business;
import com.myBusiness.domain.model.User;

import java.util.Optional;

public interface BusinessRepository {
    Business save(Business business);
    Optional<Business> findById(Long id);
    Optional<Business> findByOwner(User owner);
    Optional<Business> findByNit(String nit);
    boolean existsByOwner(User owner);
    boolean existsByNit(String nit);
}