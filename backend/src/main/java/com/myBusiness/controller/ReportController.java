package com.myBusiness.controller;

import com.myBusiness.service.ReporteService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReporteService reporteService;

    public ReportController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * Endpoint para generar y descargar reportes.
     *
     * @param reportType El tipo de reporte a generar (PRODUCTOS, MOVIMIENTOS, INVENTARIOS_BAJOS).
     * @param format El formato del reporte (PDF, EXCEL).
     * @return El archivo generado como respuesta.
     */
    @GetMapping("/low-stock")
    public void generateLowStockReport(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=low_stock_report.xlsx");
        // Cambiar reportService por reporteService
        reporteService.generateLowStockReport(response.getOutputStream());
    }

    @GetMapping
    public ResponseEntity<Resource> generateReport(
            @RequestParam String reportType,
            @RequestParam(defaultValue = "EXCEL") String format) {

        try {
            // Generar el reporte utilizando el servicio
            Resource report = reporteService.generarReporte(reportType, format);

            // Configurar los encabezados para la descarga del archivo
            String filename = String.format("%s_report.%s", reportType.toLowerCase(), format.toLowerCase());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(format.equalsIgnoreCase("EXCEL") ?
                            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") :
                            MediaType.APPLICATION_OCTET_STREAM)
                    .body(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(415).body(null); // Código 415 para formato no soportado
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null); // Error en el servidor
        }
    }
}
