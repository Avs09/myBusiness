// src/main/java/com/myBusiness/application/usecase/ListMovementsPaginatedUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.MovementFilterDto;
import com.myBusiness.application.dto.MovementOutputDto;
import com.myBusiness.application.dto.PageResponseDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import com.myBusiness.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListMovementsPaginatedUseCase {

    private final InventoryMovementRepository movementRepo;
    private final ProductRepository productRepo;

    /**
     * Devuelve respuesta paginada de MovementOutputDto según filtros en DTO.
     * Filtra primero con repository.findByFilter(productId, null, null, dateFrom, dateTo),
     * luego en memoria aplica movementType, search, ordena y pagina.
     */
    public PageResponseDto<MovementOutputDto> execute(MovementFilterDto filterDto) {
        int page = Optional.ofNullable(filterDto.getPage()).orElse(0);
        int size = Optional.ofNullable(filterDto.getSize()).orElse(10);
        Long productId = filterDto.getProductId();
        java.time.LocalDate dateFrom = filterDto.getDateFrom();
        java.time.LocalDate dateTo = filterDto.getDateTo();

        // 1) Obtener lista base desde repo (dateFrom/dateTo y productId)
        List<InventoryMovement> baseList = movementRepo.findByFilter(
                productId,
                null,
                null,
                dateFrom,
                dateTo
        );

        // 2) Filtrar por movementType si se proporcionó
        String mtFilter = filterDto.getMovementType();
        if (mtFilter != null && !mtFilter.isBlank()) {
            String mtUpper = mtFilter.trim().toUpperCase();
            baseList = baseList.stream()
                    .filter(mov -> mov.getMovementType() != null
                            && mov.getMovementType().name().equalsIgnoreCase(mtUpper))
                    .collect(Collectors.toList());
        }

        // 3) Filtrar por search en motivo o nombre de producto
        String search = filterDto.getSearch();
        if (search != null && !search.isBlank()) {
            String searchLower = search.trim().toLowerCase();
            baseList = baseList.stream()
                    .filter(mov -> {
                        boolean matchReason = mov.getReason() != null && mov.getReason().toLowerCase().contains(searchLower);
                        String prodName = mov.getProduct() != null ? mov.getProduct().getName() : "";
                        boolean matchProd = prodName != null && prodName.toLowerCase().contains(searchLower);
                        return matchReason || matchProd;
                    })
                    .collect(Collectors.toList());
        }

        // 4) Ordenar según sort. Si no, por movementDate desc.
        Comparator<InventoryMovement> comparator;
        String sort = filterDto.getSort();
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            if (parts.length == 2) {
                String field = parts[0].trim();
                String dir = parts[1].trim().equalsIgnoreCase("asc") ? "asc" : "desc";
                comparator = createComparator(field, dir);
            } else {
                comparator = Comparator.comparing(InventoryMovement::getMovementDate).reversed();
            }
        } else {
            comparator = Comparator.comparing(InventoryMovement::getMovementDate).reversed();
        }
        baseList.sort(comparator);

        // 5) Paginación manual
        long totalElements = baseList.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        int fromIndex = page * size;
        List<InventoryMovement> pageList;
        if (fromIndex >= baseList.size() || size <= 0) {
            pageList = Collections.emptyList();
        } else {
            int toIndex = Math.min(fromIndex + size, baseList.size());
            pageList = baseList.subList(fromIndex, toIndex);
        }

        // 6) Mapear a MovementOutputDto
        List<MovementOutputDto> content = pageList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(content, page, size, totalElements, totalPages);
    }

    /** Crea comparator según campo y dirección; ajustar según campos disponibles. */
    private Comparator<InventoryMovement> createComparator(String field, String dir) {
        Comparator<InventoryMovement> cmp;
        switch (field) {
            case "id":
                cmp = Comparator.comparing(InventoryMovement::getId);
                break;
            case "quantity":
                cmp = Comparator.comparing(InventoryMovement::getQuantity);
                break;
            case "movementDate":
                cmp = Comparator.comparing(InventoryMovement::getMovementDate);
                break;
            case "reason":
                cmp = Comparator.comparing(m -> m.getReason() != null ? m.getReason() : "");
                break;
            default:
                // por defecto movementDate
                cmp = Comparator.comparing(InventoryMovement::getMovementDate);
                break;
        }
        if ("desc".equalsIgnoreCase(dir)) {
            cmp = cmp.reversed();
        }
        return cmp;
    }

    /** Método auxiliar para mapear entidad a DTO */
    private MovementOutputDto toDto(InventoryMovement m) {
        return MovementOutputDto.builder()
                .id(m.getId())
                .productId(m.getProduct() != null ? m.getProduct().getId() : null)
                .productName(m.getProduct() != null ? m.getProduct().getName() : null)
                .movementType(m.getMovementType().name())
                .quantity(m.getQuantity())
                .reason(m.getReason())
                .movementDate(m.getMovementDate())
                .createdBy(m.getCreatedBy())
                .createdDate(m.getCreatedDate())
                .modifiedBy(m.getModifiedBy())
                .modifiedDate(m.getModifiedDate())
                .build();
    }

    /**
     * Caso sencillo para fetchById
     */
    public MovementOutputDto fetchById(Long id) {
        return movementRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado id=" + id));
    }
}
