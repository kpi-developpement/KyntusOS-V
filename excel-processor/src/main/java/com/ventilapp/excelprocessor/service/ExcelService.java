package com.ventilapp.excelprocessor.service;

import com.ventilapp.excelprocessor.dto.PartnerTotalDto;
import com.ventilapp.excelprocessor.dto.ProcessResponseDto;
import com.ventilapp.excelprocessor.utils.ExcelHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;

@Service
public class ExcelService {

    public ProcessResponseDto processAndGenerateTotals(MultipartFile file) {
        File tempFile = null;
        try {
            String origName = file.getOriginalFilename();
            String ext = (origName != null && origName.toLowerCase().endsWith(".csv")) ? ".csv" : ".xlsx";

            tempFile = Files.createTempFile("ventilation-", ext).toFile();

            // OPTIMISATION STRATÉGIQUE: Files.copy 7sen mn transferTo bach n-evitiw FileAlreadyExistsException
            Files.copy(file.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            List<PartnerTotalDto> totalsList = ExcelHelper.extractTotals(tempFile);
            byte[] excelContent = ExcelHelper.generateResultExcel(totalsList);
            String base64File = Base64.getEncoder().encodeToString(excelContent);

            return new ProcessResponseDto(totalsList, base64File);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}