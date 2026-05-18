package com.ventilapp.excelprocessor.utils;

import com.github.pjfanning.xlsx.StreamingReader;
import com.ventilapp.excelprocessor.dto.PartnerTotalDto;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {

    public static List<PartnerTotalDto> extractTotals(File file) {
        if (file.getName().toLowerCase().endsWith(".csv")) {
            return extractFromCsv(file);
        }
        return extractFromXlsx(file);
    }

    private static List<PartnerTotalDto> extractFromCsv(File file) {
        List<PartnerTotalDto> results = new ArrayList<>();
        Double totalSst = 0.0;
        boolean hasValidColumn = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int targetIndex = -1;
            String delimiter = ",";

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (targetIndex == -1 && line.contains(";")) delimiter = ";";

                String[] cols = line.split(delimiter);

                if (targetIndex == -1) {
                    for (int i = 0; i < cols.length; i++) {
                        if (cols[i] == null) continue;
                        String header = cols[i].trim().replace("\"", "");
                        if (header.equalsIgnoreCase("Mt SST") || header.equalsIgnoreCase("Total BR")) {
                            targetIndex = i;
                            hasValidColumn = true;
                            break;
                        }
                    }
                } else {
                    if (targetIndex < cols.length && cols[targetIndex] != null) {
                        try {
                            String cleanNum = cols[targetIndex].replaceAll("[^\\d.,-]", "").replace(",", ".");
                            if (!cleanNum.isEmpty() && !cleanNum.equals("-")) {
                                totalSst += Double.parseDouble(cleanNum);
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }

            if (!hasValidColumn) {
                System.out.println("⚠️ SKIP : Aucune colonne trouvée f l'fichier CSV.");
                return results;
            }

            String sheetName = file.getName().replaceAll("(?i)\\.csv$", "");
            sheetName = sheetName.replaceAll("^ventilation-\\d+-?", "");
            if(sheetName.length() > 25) sheetName = sheetName.substring(0, 25) + "...";

            results.add(new PartnerTotalDto(sheetName, totalSst));

        } catch (Exception e) {
            throw new RuntimeException("Erreur f l'extraction CSV: " + e.getMessage());
        }
        return results;
    }

    private static List<PartnerTotalDto> extractFromXlsx(File file) {
        List<PartnerTotalDto> results = new ArrayList<>();

        // L'HERBA HNA: Utilisation dyal StreamingReader bach l'RAM ma-tcrachich (Anti-OOM)
        try (Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(file)) {

            System.out.println("🚀 DÉMARRAGE EXTRACTION EXCEL STREAMING (Anti-OOM)...");

            for (Sheet sheet : workbook) {
                String sheetName = sheet.getSheetName();
                Double totalBrValue = 0.0;
                boolean found = false;

                try {
                    // Kanjbedou ga3 les lignes d l'feuille (Zidna sur3a w n9esna RAM)
                    for (Row row : sheet) {
                        for (Cell cell : row) {
                            if (cell.getCellType() == CellType.STRING) {
                                String cellValue = cell.getStringCellValue();

                                // Ila l9ina "Total BR" f la cellule
                                if (cellValue != null && cellValue.trim().equalsIgnoreCase("Total BR")) {

                                    // Nakhedou l'valeur li 3la limen (colonne suivante)
                                    Cell valueCell = row.getCell(cell.getColumnIndex() + 1);

                                    if (valueCell != null) {
                                        if (valueCell.getCellType() == CellType.NUMERIC) {
                                            totalBrValue = valueCell.getNumericCellValue();
                                        } else {
                                            String rawValue = valueCell.getStringCellValue();
                                            if (rawValue != null) {
                                                String cleanNum = rawValue.replaceAll("[^\\d.,-]", "").replace(",", ".");
                                                if (!cleanNum.isEmpty() && !cleanNum.equals("-")) {
                                                    totalBrValue = Double.parseDouble(cleanNum);
                                                }
                                            }
                                        }
                                    }
                                    found = true;
                                    System.out.println("✅ " + sheetName + " -> " + totalBrValue);
                                    break; // Tl9ina l'valeur, khrej mn l'boucle d les cellules
                                }
                            }
                        }
                        if (found) break; // Tl9ina l'valeur, khrej mn l'boucle d les lignes
                    }
                } catch (Exception ex) {
                    System.out.println("⚠️ Erreur mineure f " + sheetName + ": " + ex.getMessage());
                }

                // L'ALGORITHME LI BGHITI : Ila lqina Total BR, kan-ajoutiwha. Sinoun (ex: TCD), SKIP !
                if (found) {
                    results.add(new PartnerTotalDto(sheetName, totalBrValue));
                } else {
                    System.out.println("⏩ SKIP : La feuille '" + sheetName + "' ma fihach 'Total BR'.");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur f l'extraction EXCEL Streaming: " + e.getMessage());
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