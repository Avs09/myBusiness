// src/test/java/com/myBusiness/application/usecase/ListFieldValuesUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.FieldValueOutputDto;
import com.myBusiness.domain.model.CustomField;
import com.myBusiness.domain.model.FieldValue;
import com.myBusiness.domain.port.FieldValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListFieldValuesUseCaseTest {

    @Mock private FieldValueRepository valueRepo;
    private ListFieldValuesUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ListFieldValuesUseCase(valueRepo);
    }

    @Test
    void executeReturnsEmptyListWhenNoValues() {
        when(valueRepo.findAllByProductIdAndFieldId(1L, 2L))
            .thenReturn(Collections.emptyList());

        List<FieldValueOutputDto> result = useCase.execute(1L, 2L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void executeMapsValuesToDto() {
        FieldValue fv = FieldValue.builder()
            .id(3L)
            .productId(1L)
            .field(CustomField.builder().id(2L).name("F").build())
            .valueText("T")
            .valueNumber(4.5)
            .valueDate(LocalDate.of(2025,5,1))
            .createdBy("u")
            .createdDate(Instant.ofEpochSecond(0))
            .modifiedBy("u2")
            .modifiedDate(Instant.ofEpochSecond(10))
            .build();
        when(valueRepo.findAllByProductIdAndFieldId(1L,2L))
            .thenReturn(List.of(fv));

        List<FieldValueOutputDto> dtos = useCase.execute(1L, 2L);

        assertEquals(1, dtos.size());
        FieldValueOutputDto dto = dtos.get(0);
        assertEquals(3L, dto.getId());
        assertEquals(1L, dto.getProductId());
        assertEquals(2L, dto.getFieldId());
        assertEquals("T", dto.getValueText());
        assertEquals(4.5, dto.getValueNumber());
        assertEquals(LocalDate.of(2025,5,1), dto.getValueDate());
        assertEquals(Instant.ofEpochSecond(0), dto.getCreatedDate());
        assertEquals("u", dto.getCreatedBy());
        assertEquals(Instant.ofEpochSecond(10), dto.getModifiedDate());
        assertEquals("u2", dto.getModifiedBy());
    }
}
