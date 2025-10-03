package com.myBusiness.adapters.outbound.persistence;

import com.myBusiness.domain.model.Business;
import com.myBusiness.domain.model.User;
import com.myBusiness.domain.port.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BusinessRepositoryImpl implements BusinessRepository {

    private final BusinessJpaRepository jpaRepository;

    @Override
    public Business save(Business business) {
        return jpaRepository.save(business);
    }

    @Override
    public Optional<Business> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Business> findByOwner(User owner) {
        return jpaRepository.findByOwner(owner);
    }

    @Override
    public Optional<Business> findByNit(String nit) {
        return jpaRepository.findByNit(nit);
    }

    @Override
    public boolean existsByOwner(User owner) {
        return jpaRepository.existsByOwner(owner);
    }

    @Override
    public boolean existsByNit(String nit) {
        return jpaRepository.existsByNit(nit);
    }
}