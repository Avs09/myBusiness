// src/main/java/com/myBusiness/adapters/inbound/rest/ReportController.java
package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.CategorySummaryDto;
import com.myBusiness.application.dto.InventoryReportRowDto;
import com.myBusiness.application.dto.ReportFilterDto;
import com.myBusiness.application.dto.ReportSummaryDto;
import com.myBusiness.application.usecase.GenerateInventoryReportUseCase;
import com.myBusiness.application.usecase.GetCategorySummaryUseCase;
import com.myBusiness.application.usecase.GetReportSummaryUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final GenerateInventoryReportUseCase reportUseCase;
    private final GetCategorySummaryUseCase categorySummaryUseCase;
    private final GetReportSummaryUseCase reportSummaryUseCase;

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryReportRowDto>> getInventoryReport(ReportFilterDto filter) {
        List<InventoryReportRowDto> report = reportUseCase.execute(filter);
        return ResponseEntity.ok(report);
    }

    @GetMapping(value = "/inventory/export", produces = {
            MediaType.APPLICATION_PDF_VALUE,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    })
    public ResponseEntity<byte[]> exportInventoryReport(
            ReportFilterDto filter,
            @RequestParam String format
    ) throws Exception {
        byte[] data;
        String contentType;
        if ("excel".equalsIgnoreCase(format)) {
            data = reportUseCase.exportToExcel(filter);
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else {
            data = reportUseCase.exportToPdf(filter);
            contentType = MediaType.APPLICATION_PDF_VALUE;
        }
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=inventory_report." + format)
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }
    
    @GetMapping("/categories-summary")
    public ResponseEntity<List<CategorySummaryDto>> getCategorySummary() {
        List<CategorySummaryDto> dto = categorySummaryUseCase.execute();
        return ResponseEntity.ok(dto);
    }
    
   

    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryDto> getReportSummary(ReportFilterDto filter) {
        ReportSummaryDto dto = reportSummaryUseCase.execute(filter);
        return ResponseEntity.ok(dto);
    }
}
