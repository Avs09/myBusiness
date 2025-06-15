package com.myBusiness.application.usecase;

import com.myBusiness.application.exception.ProductNotFoundException;
import com.myBusiness.domain.port.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteProductUseCase {

    private final ProductRepository productRepository;

    @Transactional
    public void execute(Long productId) {
        if (productRepository.findById(productId).isEmpty()) {
            throw new ProductNotFoundException(productId);
        }
        productRepository.deleteById(productId);
    }
}
