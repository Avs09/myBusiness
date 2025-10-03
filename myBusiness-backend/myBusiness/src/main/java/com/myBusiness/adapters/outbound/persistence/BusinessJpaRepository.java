package com.myBusiness.adapters.outbound.persistence;

import com.myBusiness.domain.model.Business;
import com.myBusiness.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessJpaRepository extends JpaRepository<Business, Long> {
    Optional<Business> findByOwner(User owner);
    Optional<Business> findByNit(String nit);
    boolean existsByOwner(User owner);
    boolean existsByNit(String nit);
}