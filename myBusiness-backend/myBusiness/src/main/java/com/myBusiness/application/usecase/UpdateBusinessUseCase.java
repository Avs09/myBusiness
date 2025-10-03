package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.BusinessInputDto;
import com.myBusiness.application.dto.BusinessOutputDto;
import com.myBusiness.domain.model.Business;
import com.myBusiness.domain.model.User;
import com.myBusiness.domain.port.BusinessRepository;
import com.myBusiness.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateBusinessUseCase {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    @Transactional
    public BusinessOutputDto execute(Long userId, BusinessInputDto inputDto) {
        // Verify user exists
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Get existing business
        Business business = businessRepository.findByOwner(owner)
                .orElseThrow(() -> new RuntimeException("No se encontró un negocio para este usuario"));

        // Check if NIT is already taken by another business
        if (!business.getNit().equals(inputDto.getNit()) &&
            businessRepository.existsByNit(inputDto.getNit())) {
            throw new RuntimeException("El NIT ya está registrado por otro negocio");
        }

        // Update business
        business.setName(inputDto.getName());
        business.setNit(inputDto.getNit());
        business.setDescription(inputDto.getDescription());
        business.setAddress(inputDto.getAddress());
        business.setPhone(inputDto.getPhone());
        business.setEmail(inputDto.getEmail());
        business.setWebsite(inputDto.getWebsite());
        business.setLogoUrl(inputDto.getLogoUrl());
        business.setIndustry(inputDto.getIndustry());

        Business updatedBusiness = businessRepository.save(business);

        return BusinessOutputDto.builder()
                .id(updatedBusiness.getId())
                .name(updatedBusiness.getName())
                .nit(updatedBusiness.getNit())
                .description(updatedBusiness.getDescription())
                .address(updatedBusiness.getAddress())
                .phone(updatedBusiness.getPhone())
                .email(updatedBusiness.getEmail())
                .website(updatedBusiness.getWebsite())
                .logoUrl(updatedBusiness.getLogoUrl())
                .industry(updatedBusiness.getIndustry())
                .createdAt(updatedBusiness.getCreatedAt())
                .updatedAt(updatedBusiness.getUpdatedAt())
                .build();
    }
}