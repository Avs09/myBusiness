// src/main/java/com/myBusiness/application/util/ExcelUtils.java
package com.myBusiness.application.util;

import com.myBusiness.application.dto.ProductOutputDto;
import com.myBusiness.application.exception.ImportExportException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExcelUtils {
    /** Genera un XLSX con las mismas columnas que el CSV */
	public static byte[] toExcel(List<ProductOutputDto> items) {
	    try (var wb = new XSSFWorkbook(); var out = new ByteArrayOutputStream()) {
	        Sheet sheet = wb.createSheet("Products");
	   
	        String[] cols = {
	            "id","name","thresholdMin","thresholdMax","price",
	            "categoryName","unitName","createdDate","modifiedDate"
	        };
	        var header = sheet.createRow(0);
	        for (int i = 0; i < cols.length; i++) {
	            header.createCell(i).setCellValue(cols[i]);
	        }
	        // filas
	        int rowIdx = 1;
	        for (var p : items) {
	            Row r = sheet.createRow(rowIdx++);
	            r.createCell(0).setCellValue(p.getId());
	            r.createCell(1).setCellValue(p.getName());
	            r.createCell(2).setCellValue(p.getThresholdMin());
	            r.createCell(3).setCellValue(p.getThresholdMax());
	            r.createCell(4).setCellValue(p.getPrice().doubleValue());
	            r.createCell(5).setCellValue(p.getCategoryName());
	            r.createCell(6).setCellValue(p.getUnitName());
	           
	            r.createCell(7).setCellValue(p.getCreatedDate().toString());
	            r.createCell(8).setCellValue(p.getModifiedDate().toString());
	        }
	        wb.write(out);
	        return out.toByteArray();
	    } catch (Exception e) {
	        throw new ImportExportException("Failed to generate Excel", e);
	    }
	}

}
