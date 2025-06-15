// src/test/java/com/myBusiness/application/usecase/UpdateFieldValueUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.FieldValueInputDto;
import com.myBusiness.application.dto.FieldValueOutputDto;
import com.myBusiness.application.exception.FieldValueNotFoundException;
import com.myBusiness.domain.model.CustomField;
import com.myBusiness.domain.model.FieldValue;
import com.myBusiness.domain.port.FieldValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateFieldValueUseCaseTest {

    @Mock private FieldValueRepository repo;
    private UpdateFieldValueUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new UpdateFieldValueUseCase(repo);
    }

    @Test
    void executeThrowsWhenNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        FieldValueInputDto input = FieldValueInputDto.builder()
            .productId(1L)
            .fieldId(1L)
            .valueText("T")
            .build();
        assertThrows(FieldValueNotFoundException.class, () -> useCase.execute(1L, input));
    }

    @Test
    void executeUpdatesAndReturnsDto() {
        FieldValue fv = FieldValue.builder()
            .id(1L)
            .productId(1L)
            .field(CustomField.builder().id(1L).name("F").build())
            .valueText("Old")
            .valueNumber(1.0)
            .valueDate(LocalDate.of(2025,1,1))
            .createdBy("u")
            .createdDate(Instant.ofEpochSecond(0))
            .modifiedBy("u")
            .modifiedDate(Instant.ofEpochSecond(0))
            .build();
        when(repo.findById(1L)).thenReturn(Optional.of(fv));

        FieldValueInputDto input = FieldValueInputDto.builder()
            .productId(1L)
            .fieldId(1L)
            .valueText("New")
            .valueNumber(2.0)
            .valueDate(LocalDate.of(2025,1,2))
            .build();
        when(repo.save(any(FieldValue.class))).thenReturn(fv);

        FieldValueOutputDto dto = useCase.execute(1L, input);

        ArgumentCaptor<FieldValue> captor = ArgumentCaptor.forClass(FieldValue.class);
        verify(repo).save(captor.capture());
        FieldValue saved = captor.getValue();
        assertEquals("New", saved.getValueText());
        assertEquals(2.0, saved.getValueNumber());
        assertEquals(LocalDate.of(2025,1,2), saved.getValueDate());

        assertEquals("New", dto.getValueText());
        assertEquals(2.0, dto.getValueNumber());
        assertEquals(LocalDate.of(2025,1,2), dto.getValueDate());
    }
}
