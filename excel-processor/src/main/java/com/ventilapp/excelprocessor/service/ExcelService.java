package com.ventilapp.excelprocessor.service;

import com.ventilapp.excelprocessor.dto.PartnerTotalDto;
import com.ventilapp.excelprocessor.dto.ProcessResponseDto;
import com.ventilapp.excelprocessor.utils.ExcelHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

@Service
public class ExcelService {

    public ProcessResponseDto processAndGenerateTotals(MultipartFile file) {
        File tempFile = null;
        try {
            tempFile = Files.createTempFile("ventilation-", ".xlsx").toFile();
            file.transferTo(tempFile);

            // 1. Njebdo l'Data (List)
            List<PartnerTotalDto> totalsList = ExcelHelper.extractTotals(tempFile);

            // 2. N-génériw l'Excel f memoire (byte[])
            byte[] excelContent = ExcelHelper.generateResultExcel(totalsList);

            // 3. N7wlou l'Excel l'String Base64 bach ydouz f JSON
            String base64File = Base64.getEncoder().encodeToString(excelContent);

            // 4. Nseftou kolchi l'Controller
            return new ProcessResponseDto(totalsList, base64File);

        } catch (Exception e) {
            throw new RuntimeException("Mouchkil f l'traitement dyal l'fichier: " + e.getMessage());
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}