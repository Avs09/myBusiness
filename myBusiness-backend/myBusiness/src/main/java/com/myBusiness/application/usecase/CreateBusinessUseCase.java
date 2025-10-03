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
public class CreateBusinessUseCase {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    @Transactional
    public BusinessOutputDto execute(Long userId, BusinessInputDto inputDto) {
        // Verify user exists
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Check if user already has a business
        if (businessRepository.existsByOwner(owner)) {
            throw new RuntimeException("El usuario ya tiene un negocio registrado");
        }

        // Check if NIT is already taken
        if (businessRepository.existsByNit(inputDto.getNit())) {
            throw new RuntimeException("El NIT ya está registrado");
        }

        // Create business
        Business business = Business.builder()
                .name(inputDto.getName())
                .nit(inputDto.getNit())
                .description(inputDto.getDescription())
                .address(inputDto.getAddress())
                .phone(inputDto.getPhone())
                .email(inputDto.getEmail())
                .website(inputDto.getWebsite())
                .logoUrl(inputDto.getLogoUrl())
                .industry(inputDto.getIndustry())
                .owner(owner)
                .build();

        Business savedBusiness = businessRepository.save(business);

        return BusinessOutputDto.builder()
                .id(savedBusiness.getId())
                .name(savedBusiness.getName())
                .nit(savedBusiness.getNit())
                .description(savedBusiness.getDescription())
                .address(savedBusiness.getAddress())
                .phone(savedBusiness.getPhone())
                .email(savedBusiness.getEmail())
                .website(savedBusiness.getWebsite())
                .logoUrl(savedBusiness.getLogoUrl())
                .industry(savedBusiness.getIndustry())
                .createdAt(savedBusiness.getCreatedAt())
                .updatedAt(savedBusiness.getUpdatedAt())
                .build();
    }
}