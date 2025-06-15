// src/main/java/com/myBusiness/application/usecase/GetCategorySummaryUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.CategorySummaryDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.CategoryRepository;
import com.myBusiness.domain.port.InventoryMovementRepository;
import com.myBusiness.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetCategorySummaryUseCase {

    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;
    private final InventoryMovementRepository movementRepo;

    public List<CategorySummaryDto> execute() {
        List<CategorySummaryDto> result = new ArrayList<>();
        var allCats = categoryRepo.findAll();
        for (var cat : allCats) {
            Long catId = cat.getId();
            // Filtrar productos de esta categoría
            List<Product> prods = productRepo.findAll().stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(catId))
                    .collect(Collectors.toList());
            long skuCount = prods.size();
            BigDecimal accumValue = BigDecimal.ZERO;
            for (var p : prods) {
                BigDecimal stockActual = computeStockForProduct(p.getId());
                accumValue = accumValue.add(p.getPrice().multiply(stockActual));
            }
            result.add(CategorySummaryDto.builder()
                    .categoryId(catId)
                    .categoryName(cat.getName())
                    .totalSkus(skuCount)
                    .totalValue(accumValue)
                    .build());
        }
        return result;
    }

    private BigDecimal computeStockForProduct(Long productId) {
        // Usar findAllByProductId (suma/recorre orden no estrictamente cronológico; 
        // si necesitas orden, podrías ordenar por movementDate tras obtener la lista)
        List<InventoryMovement> movements = movementRepo.findAllByProductId(productId);
        // Ordenar cronológicamente asc:
        movements.sort(Comparator.comparing(InventoryMovement::getMovementDate));
        BigDecimal stock = BigDecimal.ZERO;
        for (var m : movements) {
            switch (m.getMovementType()) {
                case ENTRY:
                    stock = stock.add(m.getQuantity());
                    break;
                case EXIT:
                    stock = stock.subtract(m.getQuantity());
                    break;
                case ADJUSTMENT:
                    // si tu lógica es fijar stock: stock = m.getQuantity();
                    stock = m.getQuantity();
                    break;
            }
        }
        return stock.max(BigDecimal.ZERO);
    }
}
