// src/main/java/com/myBusiness/adapters/inbound/rest/FieldController.java
package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.*;
import com.myBusiness.application.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/fields")
@RequiredArgsConstructor
public class FieldController {

    private final CreateCustomFieldUseCase createField;
    private final ListCustomFieldsUseCase listFields;
    private final UpdateCustomFieldUseCase updateField;
    private final DeleteCustomFieldUseCase deleteField;

    private final CreateFieldValueUseCase createValue;
    private final ListFieldValuesUseCase listValues;
    private final UpdateFieldValueUseCase updateValue;
    private final DeleteFieldValueUseCase deleteValue;

    @PostMapping
    public ResponseEntity<CustomFieldOutputDto> createField(
            @PathVariable Long productId,
            @Valid @RequestBody CustomFieldInputDto input
    ) {
        input.setProductId(productId);
        var out = createField.execute(input);
        return ResponseEntity
            .created(URI.create("/api/products/" + productId + "/fields/" + out.getId()))
            .body(out);
    }

    @GetMapping
    public ResponseEntity<List<CustomFieldOutputDto>> listFields(@PathVariable Long productId) {
        return ResponseEntity.ok(listFields.execute(productId));
    }

    @PutMapping("/{fieldId}")
    public ResponseEntity<CustomFieldOutputDto> updateField(
            @PathVariable Long productId,
            @PathVariable Long fieldId,
            @Valid @RequestBody CustomFieldInputDto input
    ) {
        input.setProductId(productId);
        var out = updateField.execute(fieldId, input);
        return ResponseEntity.ok(out);
    }

    @DeleteMapping("/{fieldId}")
    public ResponseEntity<Void> deleteField(
            @PathVariable Long productId,
            @PathVariable Long fieldId
    ) {
        deleteField.execute(fieldId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{fieldId}/values")
    public ResponseEntity<FieldValueOutputDto> createValue(
            @PathVariable Long productId,
            @PathVariable Long fieldId,
            @Valid @RequestBody FieldValueInputDto input
    ) {
        input.setProductId(productId);
        input.setFieldId(fieldId);
        var out = createValue.execute(input);
        return ResponseEntity
            .created(URI.create("/api/products/" + productId + "/fields/" + fieldId + "/values/" + out.getId()))
            .body(out);
    }

    @GetMapping("/{fieldId}/values")
    public ResponseEntity<List<FieldValueOutputDto>> listValues(
            @PathVariable Long productId,
            @PathVariable Long fieldId
    ) {
        return ResponseEntity.ok(listValues.execute(productId, fieldId));
    }

    @PutMapping("/{fieldId}/values/{valueId}")
    public ResponseEntity<FieldValueOutputDto> updateValue(
            @PathVariable Long productId,
            @PathVariable Long fieldId,
            @PathVariable Long valueId,
            @Valid @RequestBody FieldValueInputDto input
    ) {
        input.setProductId(productId);
        input.setFieldId(fieldId);
        var out = updateValue.execute(valueId, input);
        return ResponseEntity.ok(out);
    }

    @DeleteMapping("/{fieldId}/values/{valueId}")
    public ResponseEntity<Void> deleteValue(
            @PathVariable Long productId,
            @PathVariable Long fieldId,
            @PathVariable Long valueId
    ) {
        deleteValue.execute(valueId);
        return ResponseEntity.noContent().build();
    }
}
