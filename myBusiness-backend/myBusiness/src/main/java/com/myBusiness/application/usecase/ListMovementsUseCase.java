// src/main/java/com/myBusiness/application/usecase/ListMovementsUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.MovementFilterDto;
import com.myBusiness.application.dto.MovementOutputDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.InventoryMovementRepository;
import com.myBusiness.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListMovementsUseCase {

    private final InventoryMovementRepository movementRepo;
    private final ProductRepository productRepo;

    @Transactional(readOnly = true)
    public Page<MovementOutputDto> execute(MovementFilterDto filterDto) {
        int page = filterDto.getPage() != null ? filterDto.getPage() : 0;
        int size = filterDto.getSize() != null ? filterDto.getSize() : 10;
        // parseSort para crear Sort metadata, luego usaremos para ordenar en memoria
        Sort sortMeta = parseSort(filterDto.getSort());
        Pageable pageable = PageRequest.of(page, size, sortMeta);

        // 1) Obtener lista cruda filtrada por repositorio
        List<InventoryMovement> all = movementRepo.findByFilter(
                filterDto.getProductId(),
                null,
                null,
                filterDto.getDateFrom(),
                filterDto.getDateTo()
        );

        // 2) Filtrar por movementType si aplica
        if (filterDto.getMovementType() != null && !filterDto.getMovementType().isBlank()) {
            String mtUpper = filterDto.getMovementType().toUpperCase();
            all = all.stream()
                    .filter(mv -> mv.getMovementType().name().equalsIgnoreCase(mtUpper))
                    .collect(Collectors.toList());
        }

        // 3) Filtrar por texto de búsqueda en motivo o nombre de producto
        if (filterDto.getSearch() != null && !filterDto.getSearch().isBlank()) {
            String term = filterDto.getSearch().toLowerCase();
            all = all.stream()
                    .filter(mv -> {
                        boolean inReason = mv.getReason() != null && mv.getReason().toLowerCase().contains(term);
                        Product p = mv.getProduct();
                        boolean inProdName = p != null && p.getName() != null && p.getName().toLowerCase().contains(term);
                        return inReason || inProdName;
                    })
                    .collect(Collectors.toList());
        }

        // 4) Ordenar en memoria según sortMeta
        Comparator<InventoryMovement> comparator = buildComparator(sortMeta);
        if (comparator != null) {
            all.sort(comparator);
        }

        // 5) Paginación manual
        int total = all.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, total);
        List<MovementOutputDto> pageContent;
        if (fromIndex >= total || fromIndex < 0) {
            pageContent = Collections.emptyList();
        } else {
            pageContent = all.subList(fromIndex, toIndex).stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }

        return new PageImpl<>(pageContent, pageable, total);
    }

    private Sort parseSort(String sort) {
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            if (parts.length == 2) {
                try {
                    return Sort.by(Sort.Direction.fromString(parts[1].trim()), parts[0].trim());
                } catch (Exception e) {
                    // ignorar
                }
            } else {
                return Sort.by(sort.trim());
            }
        }
        // default: movementDate DESC
        return Sort.by(Sort.Direction.DESC, "movementDate");
    }

    /**
     * Construye un Comparator<InventoryMovement> según Sort metadata.
     * Sólo soporta campos predefinidos: movementDate, quantity, productName, createdDate, etc.
     */
    private Comparator<InventoryMovement> buildComparator(Sort sort) {
        if (sort == null) return null;
        // Tomamos sólo la primera orden en Sort
        for (Sort.Order order : sort) {
            String prop = order.getProperty();
            boolean asc = order.isAscending();
            Comparator<InventoryMovement> cmp = null;
            switch (prop) {
                case "movementDate":
                    cmp = Comparator.comparing(InventoryMovement::getMovementDate, Comparator.nullsLast(Comparator.naturalOrder()));
                    break;
                case "quantity":
                    cmp = Comparator.comparing(InventoryMovement::getQuantity, Comparator.nullsLast(Comparator.naturalOrder()));
                    break;
                case "productName":
                    cmp = Comparator.comparing(
                            mv -> {
                                Product p = mv.getProduct();
                                return p != null ? p.getName() : null;
                            },
                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                    );
                    break;
                case "createdDate":
                    cmp = Comparator.comparing(InventoryMovement::getCreatedDate, Comparator.nullsLast(Comparator.naturalOrder()));
                    break;
                // añadir más campos si se desea
                default:
                    // si campo no reconocido, no ordenar
                    cmp = null;
            }
            if (cmp != null) {
                return asc ? cmp : cmp.reversed();
            }
        }
        return null;
    }

    private MovementOutputDto toDto(InventoryMovement ent) {
        // Suponiendo que MovementOutputDto.builder() existe:
        return MovementOutputDto.builder()
                .id(ent.getId())
                .productId(ent.getProduct() != null ? ent.getProduct().getId() : null)
                .productName(ent.getProduct() != null ? ent.getProduct().getName() : null)
                .movementType(ent.getMovementType().name())
                .quantity(ent.getQuantity())
                .reason(ent.getReason())
                .movementDate(ent.getMovementDate())
                .createdBy(ent.getCreatedBy())
                .createdDate(ent.getCreatedDate())
                .modifiedBy(ent.getModifiedBy())
                .modifiedDate(ent.getModifiedDate())
                .build();
    }
}
