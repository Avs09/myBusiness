package com.myBusiness.controller;

import com.myBusiness.service.ReporteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

/**
 * ReportController provides endpoints to generate and download reports.
 * It supports different report types (e.g., low stock, products, movements) and formats (PDF, EXCEL).
 */
@RestController
@RequestMapping("/api/reports")
@Validated
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    // Service to handle report generation logic
    private final ReporteService reporteService;

    /**
     * Constructor for dependency injection.
     */
    public ReportController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * Endpoint to generate and download a low stock report in Excel format.
     * Returns a downloadable resource wrapped in a ResponseEntity.
     *
     * @return ResponseEntity containing the generated report as a downloadable resource.
     */
    @GetMapping("/low-stock")
    public ResponseEntity<Resource> generateLowStockReport() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            reporteService.generateLowStockReport(outputStream);
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=low_stock_report.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error generating low stock report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint to generate a report based on the specified type and format.
     * Supports file download with appropriate HTTP headers.
     *
     * @param reportType The type of report to generate.
     * @param format     The format of the report (default is EXCEL).
     * @return ResponseEntity containing the generated report as a downloadable resource.
     */
    @GetMapping
    public ResponseEntity<Resource> generateReport(
            @RequestParam String reportType,
            @RequestParam(defaultValue = "EXCEL") String format) {

        try {
            // Generate the report using the specified parameters
            Resource report = reporteService.generarReporte(reportType, format);

            // Build a filename using the report type and format
            String filename = String.format("%s_report.%s", reportType.toLowerCase(), format.toLowerCase());
            return ResponseEntity.ok()
                    // Set header to prompt file download
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    // Set the appropriate content type based on the format
                    .contentType(format.equalsIgnoreCase("EXCEL") ?
                            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") :
                            MediaType.APPLICATION_OCTET_STREAM)
                    .body(report);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument provided for report generation", e);
            return ResponseEntity.badRequest().build();
        } catch (UnsupportedOperationException e) {
            logger.error("Unsupported report format requested", e);
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        } catch (Exception e) {
            logger.error("Error during report generation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
