package com.myBusiness.service;

import org.springframework.core.io.Resource;
import java.io.IOException;
import java.io.OutputStream;

/**
 * ReporteService defines the contract for generating various types of reports.
 * <p>
 * This service is responsible for creating reports (e.g., low stock reports, product or movement reports)
 * in different formats (e.g., Excel, PDF) and returning them as downloadable resources.
 * Implementations should handle report generation errors appropriately and validate input parameters.
 * </p>
 */
public interface ReporteService {

    /**
     * Generates a low stock report as a byte array.
     * <p>
     * This method is typically used for scheduled jobs or email attachments.
     * </p>
     *
     * @return a byte array containing the low stock report data.
     * @throws IOException if an error occurs during report generation.
     */
    byte[] generateLowStockReportAsByteArray() throws IOException;

    /**
     * Generates a report based on the specified type and format.
     * <p>
     * The reportType parameter determines which report to generate (e.g., "low-stock", "products", "movements"),
     * while the format parameter specifies the output format (e.g., "EXCEL", "PDF").
     * </p>
     *
     * @param reportType the type of report to generate.
     * @param format the format of the report.
     * @return a Resource representing the generated report for download.
     * @throws IllegalArgumentException if the provided report type or format is invalid.
     * @throws UnsupportedOperationException if the requested format is not supported.
     * @throws IOException if an error occurs during report generation.
     */
    Resource generarReporte(String reportType, String format)
            throws IllegalArgumentException, UnsupportedOperationException, IOException;
    
    /**
     * Generates a low stock report and writes it directly to the provided OutputStream.
     *
     * @param outputStream the stream to which the report will be written.
     * @throws IOException if an error occurs during report generation.
     */
    void generateLowStockReport(OutputStream outputStream) throws IOException;
}
