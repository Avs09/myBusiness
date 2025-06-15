// src/main/java/com/myBusiness/adapters/inbound/rest/BulkController.java
package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.usecase.ExportProductsUseCase;
import com.myBusiness.application.usecase.ImportProductsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products/bulk")
@RequiredArgsConstructor
public class BulkController {

    private final ImportProductsUseCase importer;
    private final ExportProductsUseCase exporter;

    @PostMapping(path = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importCsv(@RequestPart("file") MultipartFile file) {
        importer.execute(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> export(
            @RequestParam(defaultValue = "excel") String format
    ) {
        byte[] data;
        String ct;
        String ext;
        if ("csv".equalsIgnoreCase(format)) {
            data = exporter.toCsv();
            ct = "text/csv";
            ext = "csv";
        } else {
            data = exporter.toExcel();
            ct = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            ext = "xlsx";
        }
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=products." + ext)
            .contentType(MediaType.parseMediaType(ct))
            .body(data);
    }
}

