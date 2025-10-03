package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.BusinessOutputDto;
import com.myBusiness.domain.model.Business;
import com.myBusiness.domain.model.User;
import com.myBusiness.domain.port.BusinessRepository;
import com.myBusiness.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetBusinessUseCase {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public BusinessOutputDto execute(Long userId) {
        // Verify user exists
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Get business
        Business business = businessRepository.findByOwner(owner)
                .orElseThrow(() -> new RuntimeException("No se encontró un negocio para este usuario"));

        return BusinessOutputDto.builder()
                .id(business.getId())
                .name(business.getName())
                .nit(business.getNit())
                .description(business.getDescription())
                .address(business.getAddress())
                .phone(business.getPhone())
                .email(business.getEmail())
                .website(business.getWebsite())
                .logoUrl(business.getLogoUrl())
                .industry(business.getIndustry())
                .createdAt(business.getCreatedAt())
                .updatedAt(business.getUpdatedAt())
                .build();
    }
}