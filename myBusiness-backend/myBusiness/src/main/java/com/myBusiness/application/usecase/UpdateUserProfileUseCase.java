// src/main/java/com/myBusiness/application/usecase/UpdateUserProfileUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.UserProfileDto;
import com.myBusiness.domain.model.User;
import com.myBusiness.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserProfileUseCase {

    private final UserRepository userRepository;

    @Transactional
    public void execute(Long userId, UserProfileDto profileDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Update only the profile fields
        if (profileDto.getName() != null) {
            user.setName(profileDto.getName());
        }
        if (profileDto.getPhone() != null) {
            user.setPhone(profileDto.getPhone());
        }
        if (profileDto.getLocation() != null) {
            user.setLocation(profileDto.getLocation());
        }
        if (profileDto.getBio() != null) {
            user.setBio(profileDto.getBio());
        }

        userRepository.save(user);
    }
}