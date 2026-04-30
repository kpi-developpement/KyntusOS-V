package com.ventilapp.excelprocessor.utils;

import com.ventilapp.excelprocessor.dto.PartnerTotalDto;
import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {

    public static List<PartnerTotalDto> extractTotals(File file) {
        List<PartnerTotalDto> results = new ArrayList<>();

        // VITESSE MAX: Kankhedmou b WorkbookFactory m3a 'File' (Random Access) machi InputStream
        try (Workbook workbook = WorkbookFactory.create(file, null, true)) {
            int numberOfSheets = workbook.getNumberOfSheets();
            System.out.println("🚀 DÉMARRAGE EXTRACTION x4000 (Random Access)...");

            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                Double totalBrValue = 0.0;
                boolean found = false;

                try {
                    int lastRowNum = sheet.getLastRowNum();

                    // L'HERBA HNA: Kan-scanniw ghir 150 ligne lakhrin! (0 ms)
                    int limit = Math.max(0, lastRowNum - 150);

                    for (int r = lastRowNum; r >= limit; r--) {
                        Row row = sheet.getRow(r);
                        if (row == null) continue;

                        for (Cell cell : row) {
                            if (cell.getCellType() == CellType.STRING) {
                                String cellValue = cell.getStringCellValue();

                                if (cellValue != null && cellValue.trim().equalsIgnoreCase("Total BR")) {
                                    Cell valueCell = row.getCell(cell.getColumnIndex() + 1);

                                    if (valueCell != null) {
                                        String rawValue = "";

                                        // ==========================================
                                        // FIX DYAL 0: Njebdo l'valeur li mkhbya wst Formule!
                                        // ==========================================
                                        CellType valType = valueCell.getCellType();
                                        if (valType == CellType.FORMULA) {
                                            valType = valueCell.getCachedFormulaResultType();
                                        }

                                        if (valType == CellType.NUMERIC) {
                                            rawValue = String.valueOf(valueCell.getNumericCellValue());
                                        } else if (valType == CellType.STRING) {
                                            rawValue = valueCell.getStringCellValue();
                                        }

                                        // Nettoyage: nkhaliw ghir l'ar9am w virgule/point
                                        String cleanNum = rawValue.replaceAll("[^\\d.,-]", "").replace(",", ".");

                                        if (!cleanNum.isEmpty()) {
                                            try {
                                                totalBrValue = Double.parseDouble(cleanNum);
                                                System.out.println("✅ " + sheetName + " -> " + totalBrValue);
                                            } catch (NumberFormatException e) {
                                                System.out.println("❌ Erreur format f " + sheetName);
                                            }
                                        }
                                    }
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (found) break; // Lqina Total BR, dewwez l'feuille li moraha b zerba
                    }
                } catch (Exception ex) {
                    System.out.println("⚠️ Erreur mineure f " + sheetName + ": " + ex.getMessage());
                }

                results.add(new PartnerTotalDto(sheetName, totalBrValue));
            }
        } catch (Exception e) {
            throw new RuntimeException("Mouchkil f l'qraya dyal l'fichier: " + e.getMessage());
        }

        return results;
    }

    public static byte[] generateResultExcel(List<PartnerTotalDto> dataList) {
        try (Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Recap Total BR");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Partenaire (Feuille)");
            headerRow.createCell(1).setCellValue("Total BR");

            int rowIdx = 1;
            for (PartnerTotalDto dto : dataList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getSheetName());

                Cell valueCell = row.createCell(1);
                if (dto.getTotalBr() != null) {
                    valueCell.setCellValue(dto.getTotalBr());
                } else {
                    valueCell.setCellValue(0.0);
                }
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur f la création dyal l'fichier final: " + e.getMessage());
        }
    }
}