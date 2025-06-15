package com.myBusiness.application.util;

import com.myBusiness.application.dto.ProductInputDto;
import com.myBusiness.application.exception.ImportExportException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {
    /** Parsea un CSV con encabezados: name,thresholdMin,thresholdMax,price,categoryId,unitId */
    public static List<ProductInputDto> parseProducts(MultipartFile file) {
        try (var reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             var parser = CSVParser.parse(reader,
                     CSVFormat.DEFAULT.builder()
                         .setHeader("name","thresholdMin","thresholdMax","price","categoryId","unitId")
                         .setSkipHeaderRecord(true)
                         .build())) {
            List<ProductInputDto> list = new ArrayList<>();
            for (CSVRecord r : parser) {
                ProductInputDto dto = ProductInputDto.builder()
                    .name(r.get("name"))
                    .thresholdMin(Integer.valueOf(r.get("thresholdMin")))
                    .thresholdMax(Integer.valueOf(r.get("thresholdMax")))
                    .price(new BigDecimal(r.get("price")))
                    .categoryId(Long.valueOf(r.get("categoryId")))
                    .unitId(Long.valueOf(r.get("unitId")))
                    .build();
                list.add(dto);
            }
            return list;
        } catch (Exception e) {
            throw new ImportExportException("Failed to parse CSV", e);
        }
    }

    /** Genera CSV a partir de lista de ProductOutputDto */
    public static byte[] toCsv(List<com.myBusiness.application.dto.ProductOutputDto> items) {
        try (var out = new ByteArrayOutputStream();
             var writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             var printer = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.builder()
                         .setHeader(
                             "id","name","thresholdMin","thresholdMax","price",
                             "categoryName","unitName","createdDate","createdBy",
                             "modifiedDate","modifiedBy"
                         )
                         .build())) {
            for (var p : items) {
                printer.printRecord(
                    p.getId(),
                    p.getName(),
                    p.getThresholdMin(),
                    p.getThresholdMax(),
                    p.getPrice(),
                    p.getCategoryName(),
                    p.getUnitName(),
                    p.getCreatedDate(),
                    p.getCreatedBy(),
                    p.getModifiedDate(),
                    p.getModifiedBy()
                );
            }
            printer.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new ImportExportException("Failed to generate CSV", e);
        }
    }
}
