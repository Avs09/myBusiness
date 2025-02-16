package com.myBusiness.service.impl;

import com.myBusiness.model.InventoryMovement;
import com.myBusiness.model.Product;
import com.myBusiness.repository.InventoryMovementRepository;
import com.myBusiness.repository.ProductRepository;
import com.myBusiness.service.ReporteService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;

    public ReporteServiceImpl(ProductRepository productRepository, InventoryMovementRepository movementRepository) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    public Resource generarReporte(String reportType, String format) throws IOException {
        if ("PRODUCTOS".equalsIgnoreCase(reportType)) {
            return generarReporteProductos(format);
        } else if ("MOVIMIENTOS".equalsIgnoreCase(reportType)) {
            return generarReporteMovimientos(format);
        } else if ("INVENTARIOS_BAJOS".equalsIgnoreCase(reportType)) {
            return generarReporteInventariosBajos(format);
        } else {
            throw new IllegalArgumentException("Tipo de reporte no válido.");
        }
    }

    private Resource generarReporteProductos(String format) throws IOException {
        List<Product> products = productRepository.findAll();
        if ("EXCEL".equalsIgnoreCase(format)) {
            return generarExcelProductos(products);
        } else {
            throw new UnsupportedOperationException("Formato no soportado: " + format);
        }
    }

    private Resource generarReporteMovimientos(String format) throws IOException {
        List<InventoryMovement> movements = movementRepository.findAll();
        if ("EXCEL".equalsIgnoreCase(format)) {
            return generarExcelMovimientos(movements);
        } else {
            throw new UnsupportedOperationException("Formato no soportado: " + format);
        }
    }

    private Resource generarReporteInventariosBajos(String format) throws IOException {
        List<Product> lowStockProducts = productRepository.findByQuantityLessThan(10); // Ejemplo de umbral
        if ("EXCEL".equalsIgnoreCase(format)) {
            return generarExcelProductos(lowStockProducts);
        } else {
            throw new UnsupportedOperationException("Formato no soportado: " + format);
        }
    }

    private Resource generarExcelProductos(List<Product> products) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Productos");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Nombre", "Cantidad", "Precio"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (Product product : products) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getQuantity());
            row.createCell(3).setCellValue(product.getPrice().doubleValue());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayResource(out.toByteArray());
    }
    
    @Override
    public byte[] generateLowStockReportAsByteArray() throws IOException {
        List<Product> lowStockProducts = productRepository.findByQuantityLessThan(10);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventarios Bajos");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Nombre", "Cantidad"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (Product product : lowStockProducts) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getQuantity());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    
    @Override
    public void generateLowStockReport(OutputStream outputStream) {
        try {
            List<Product> lowStockProducts = productRepository.findByQuantityLessThan(10);
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Inventarios Bajos");

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Cantidad", "Precio"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Rellenar datos
            int rowNum = 1;
            for (Product product : lowStockProducts) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getQuantity());
                row.createCell(3).setCellValue(product.getPrice().doubleValue());
            }

            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el reporte de inventarios bajos.", e);
        }
    }


    private Resource generarExcelMovimientos(List<InventoryMovement> movements) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Movimientos");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Producto", "Cantidad", "Tipo", "Fecha"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (InventoryMovement movement : movements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(movement.getId());
            row.createCell(1).setCellValue(movement.getProduct().getName());
            row.createCell(2).setCellValue(movement.getQuantity());
            row.createCell(3).setCellValue(movement.getType());
            row.createCell(4).setCellValue(movement.getDate().toString());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayResource(out.toByteArray());
    }
}
