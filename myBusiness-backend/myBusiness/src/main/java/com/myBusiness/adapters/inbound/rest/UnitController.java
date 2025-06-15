// src/main/java/com/myBusiness/adapters/inbound/rest/UnitController.java
package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.UnitInputDto;
import com.myBusiness.application.dto.UnitOutputDto;
import com.myBusiness.application.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
@Validated
public class UnitController {

    private final CreateUnitUseCase createUseCase;
    private final GetUnitUseCase getUseCase;
    private final ListUnitsUseCase listUseCase;
    private final UpdateUnitUseCase updateUseCase;
    private final DeleteUnitUseCase deleteUseCase;

    @PostMapping
    public ResponseEntity<UnitOutputDto> create(@RequestBody @Validated UnitInputDto input) {
        UnitOutputDto out = createUseCase.execute(input);
        URI location = URI.create("/api/units/" + out.getId());
        return ResponseEntity.created(location).body(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitOutputDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getUseCase.execute(id));
    }

    @GetMapping
    public ResponseEntity<List<UnitOutputDto>> listAll() {
        return ResponseEntity.ok(listUseCase.execute());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnitOutputDto> update(
            @PathVariable Long id,
            @RequestBody @Validated UnitInputDto input
    ) {
        return ResponseEntity.ok(updateUseCase.execute(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
