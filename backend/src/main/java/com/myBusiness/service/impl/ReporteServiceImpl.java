package com.myBusiness.service.impl;

import com.myBusiness.model.InventoryMovement;
import com.myBusiness.model.Product;
import com.myBusiness.repository.InventoryMovementRepository;
import com.myBusiness.repository.ProductRepository;
import com.myBusiness.service.ReporteService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * ReporteServiceImpl provides implementations for generating reports.
 * It uses Apache POI to create Excel reports for products, inventory movements,
 * and low stock products.
 */
@Service
public class ReporteServiceImpl implements ReporteService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteServiceImpl.class);
    
    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;

    /**
     * Constructor for dependency injection.
     */
    public ReporteServiceImpl(ProductRepository productRepository, InventoryMovementRepository movementRepository) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    /**
     * Generates a report based on the report type and format.
     * Supports "PRODUCTOS", "MOVIMIENTOS", and "INVENTARIOS_BAJOS" types in EXCEL format.
     */
    @Override
    public Resource generarReporte(String reportType, String format) throws IOException {
        logger.info("Generating report. Type: {}, Format: {}", reportType, format);
        if ("PRODUCTOS".equalsIgnoreCase(reportType)) {
            return generarReporteProductos(format);
        } else if ("MOVIMIENTOS".equalsIgnoreCase(reportType)) {
            return generarReporteMovimientos(format);
        } else if ("INVENTARIOS_BAJOS".equalsIgnoreCase(reportType)) {
            return generarReporteInventariosBajos(format);
        } else {
            logger.error("Invalid report type: {}", reportType);
            throw new IllegalArgumentException("Invalid report type.");
        }
    }

    /**
     * Generates a report for all products.
     */
    private Resource generarReporteProductos(String format) throws IOException {
        List<Product> products = productRepository.findAll();
        logger.info("Found {} products for report.", products.size());
        if ("EXCEL".equalsIgnoreCase(format)) {
            return generarExcelProductos(products);
        } else {
            logger.error("Unsupported format: {}", format);
            throw new UnsupportedOperationException("Format not supported: " + format);
        }
    }

    /**
     * Generates a report for all inventory movements.
     */
    private Resource generarReporteMovimientos(String format) throws IOException {
        List<InventoryMovement> movements = movementRepository.findAll();
        logger.info("Found {} inventory movements for report.", movements.size());
        if ("EXCEL".equalsIgnoreCase(format)) {
            return generarExcelMovimientos(movements);
        } else {
            logger.error("Unsupported format: {}", format);
            throw new UnsupportedOperationException("Format not supported: " + format);
        }
    }

    /**
     * Generates a report for products with low stock.
     */
    private Resource generarReporteInventariosBajos(String format) throws IOException {
        List<Product> lowStockProducts = productRepository.findByQuantityLessThan(10); // Example threshold
        logger.info("Found {} low stock products for report.", lowStockProducts.size());
        if ("EXCEL".equalsIgnoreCase(format)) {
            return generarExcelProductos(lowStockProducts);
        } else {
            logger.error("Unsupported format: {}", format);
            throw new UnsupportedOperationException("Format not supported: " + format);
        }
    }

    /**
     * Generates an Excel report for a list of products.
     */
    private Resource generarExcelProductos(List<Product> products) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Productos");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Cantidad", "Precio"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Fill rows with product data
            int rowNum = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getQuantity());
                row.createCell(3).setCellValue(product.getPrice().doubleValue());
            }

            workbook.write(out);
            logger.info("Excel report for products generated successfully.");
            return new ByteArrayResource(out.toByteArray());
        }
    }

    /**
     * Generates a low stock report as a byte array.
     */
    @Override
    public byte[] generateLowStockReportAsByteArray() throws IOException {
        List<Product> lowStockProducts = productRepository.findByQuantityLessThan(10);
        logger.info("Generating low stock report as byte array. Low stock products: {}", lowStockProducts.size());
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Inventarios Bajos");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Cantidad"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Fill rows with product data
            int rowNum = 1;
            for (Product product : lowStockProducts) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getQuantity());
            }

            workbook.write(out);
            logger.info("Low stock report generated as byte array successfully.");
            return out.toByteArray();
        }
    }

    /**
     * Generates a low stock report and writes it directly to the provided OutputStream.
     */
    @Override
    public void generateLowStockReport(OutputStream outputStream) {
        logger.info("Generating low stock report to OutputStream.");
        try (Workbook workbook = new XSSFWorkbook()) {
            List<Product> lowStockProducts = productRepository.findByQuantityLessThan(10);
            Sheet sheet = workbook.createSheet("Inventarios Bajos");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Cantidad", "Precio"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Fill rows with product data
            int rowNum = 1;
            for (Product product : lowStockProducts) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getQuantity());
                row.createCell(3).setCellValue(product.getPrice().doubleValue());
            }

            workbook.write(outputStream);
            logger.info("Low stock report written to OutputStream successfully.");
        } catch (IOException e) {
            logger.error("Error generating low stock report to OutputStream: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating low stock report.", e);
        }
    }

    /**
     * Generates an Excel report for a list of inventory movements.
     */
    private Resource generarExcelMovimientos(List<InventoryMovement> movements) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Movimientos");

            // Create header row for movements
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Producto", "Cantidad", "Tipo", "Fecha"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Fill rows with movement data
            int rowNum = 1;
            for (InventoryMovement movement : movements) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(movement.getId());
                row.createCell(1).setCellValue(movement.getProduct().getName());
                row.createCell(2).setCellValue(movement.getQuantity());
                row.createCell(3).setCellValue(movement.getType().toString());
                row.createCell(4).setCellValue(movement.getDate().toString());
            }

            workbook.write(out);
            logger.info("Excel report for inventory movements generated successfully.");
            return new ByteArrayResource(out.toByteArray());
        }
    }
}
