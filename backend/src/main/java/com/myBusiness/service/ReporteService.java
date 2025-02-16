package com.myBusiness.service;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.OutputStream;

public interface ReporteService {
    /**
     * Genera un reporte en el formato especificado.
     * 
     * @param reportType El tipo de reporte (productos, movimientos, inventarios bajos).
     * @param format El formato del reporte (PDF, EXCEL).
     * @return Un recurso que contiene el archivo generado.
     * @throws IOException Si ocurre algún error durante la generación.
     */
    Resource generarReporte(String reportType, String format) throws IOException;
    
    void generateLowStockReport(OutputStream outputStream);
    
    byte[] generateLowStockReportAsByteArray() throws IOException;
}
