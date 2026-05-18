package com.ventilapp.excelprocessor.controller;

import com.ventilapp.excelprocessor.dto.ProcessResponseDto;
import com.ventilapp.excelprocessor.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
@CrossOrigin(origins = "*")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndProcessFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Fichier vide");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            ProcessResponseDto response = excelService.processAndGenerateTotals(file);
            return ResponseEntity.ok(response);
        } catch (Throwable e) { // Zedt Throwable bach t-capturi hta l'OutOfMemoryError
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            String errorMsg = e.getMessage();

            // Ila kant NullPointerException awla ma3ndhach message, n-affichiw smiyat l'erreur
            if (errorMsg == null || errorMsg.trim().isEmpty()) {
                errorMsg = "Crash système : " + e.getClass().getSimpleName();
            }

            errorResponse.put("error", errorMsg);
            errorResponse.put("cause", e.getCause() != null ? e.getCause().toString() : "");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}