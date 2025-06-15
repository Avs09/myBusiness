// src/main/java/com/myBusiness/application/usecase/GenerateInventoryReportUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.InventoryReportRowDto;
import com.myBusiness.application.dto.ReportFilterDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.model.MovementType;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenerateInventoryReportUseCase {

    private final InventoryMovementRepository movementRepo;

    public List<InventoryReportRowDto> execute(ReportFilterDto filter) {
        // Llamamos directamente a findByFilter; si algún filtro es null, pasa null
        LocalDate dateFrom = filter.getDateFrom();
        LocalDate dateTo = filter.getDateTo();
        List<InventoryMovement> movements = movementRepo.findByFilter(
                filter.getProductId(),
                filter.getCategoryId(),
                filter.getUnitId(),
                dateFrom,
                dateTo
        );

        return movements.stream()
            .collect(Collectors.groupingBy(m -> m.getProduct().getId()))
            .entrySet().stream()
            .map(entry -> {
                List<InventoryMovement> group = entry.getValue();
                Product prod = group.get(0).getProduct();
                // Sumar/Restar según tipo
                BigDecimal stock = group.stream()
                    .map(m -> {
                        MovementType mt = m.getMovementType();
                        if (mt == MovementType.ENTRY) {
                            return m.getQuantity();
                        } else if (mt == MovementType.EXIT) {
                            return m.getQuantity().negate();
                        } else {
                            // ADJUSTMENT: aquí asumimos se suma; si tu lógica es “fijar stock”, habría que cambiar
                            return m.getQuantity();
                        }
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                Instant lastDate = group.stream()
                    .map(InventoryMovement::getMovementDate)
                    .max(Instant::compareTo)
                    .orElse(Instant.now());

                return InventoryReportRowDto.builder()
                    .productId(prod.getId())
                    .productName(prod.getName())
                    .categoryName(prod.getCategory().getName())
                    .unitName(prod.getUnit().getName())
                    .currentStock(stock)
                    .lastMovementDate(lastDate)
                    .build();
            })
            .collect(Collectors.toList());
    }

    public byte[] exportToExcel(ReportFilterDto filter) throws Exception {
        List<InventoryReportRowDto> rows = execute(filter);
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Inventory Report");
            Row header = sheet.createRow(0);
            String[] cols = {"Product ID","Name","Category","Unit","Current Stock","Last Movement"};
            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
            }
            int rowIdx = 1;
            for (InventoryReportRowDto r : rows) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getProductId());
                row.createCell(1).setCellValue(r.getProductName());
                row.createCell(2).setCellValue(r.getCategoryName());
                row.createCell(3).setCellValue(r.getUnitName());
                row.createCell(4).setCellValue(r.getCurrentStock().doubleValue());
                row.createCell(5).setCellValue(r.getLastMovementDate().toString());
            }
            wb.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportToPdf(ReportFilterDto filter) throws Exception {
        List<InventoryReportRowDto> rows = execute(filter);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            Table table = new Table(new float[]{2,4,4,3,3,4});
            table.addHeaderCell("Product ID");
            table.addHeaderCell("Name");
            table.addHeaderCell("Category");
            table.addHeaderCell("Unit");
            table.addHeaderCell("Current Stock");
            table.addHeaderCell("Last Movement");
            for (InventoryReportRowDto r : rows) {
                table.addCell(String.valueOf(r.getProductId()));
                table.addCell(r.getProductName());
                table.addCell(r.getCategoryName());
                table.addCell(r.getUnitName());
                table.addCell(r.getCurrentStock().toString());
                table.addCell(r.getLastMovementDate().toString());
            }
            doc.add(table);
            doc.close();
            return out.toByteArray();
        }
    }
}
