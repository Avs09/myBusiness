// src/test/java/com/myBusiness/application/usecase/DeleteProductUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.exception.ProductNotFoundException;
import com.myBusiness.domain.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteProductUseCaseTest {

    @Mock
    private ProductRepository repo;
    private DeleteProductUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new DeleteProductUseCase(repo);
    }

    @Test
    void executeDeletesWhenExists() {
        // Simular que existe: findById devuelve un Optional con un Product
        when(repo.findById(1L)).thenReturn(Optional.of(mock(com.myBusiness.domain.model.Product.class)));
        doNothing().when(repo).deleteById(1L);

        assertDoesNotThrow(() -> useCase.execute(1L));
        verify(repo).deleteById(1L);
    }

    @Test
    void executeThrowsWhenNotFound() {
        when(repo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> useCase.execute(2L));
    }
}
