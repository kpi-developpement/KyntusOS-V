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

    private static Double parseMoney(String raw) {
        if (raw == null || raw.trim().isEmpty() || raw.equals("-")) return 0.0;

        String s = raw.replaceAll("[\\s€$a-zA-Z]", "");

        int commas = s.length() - s.replace(",", "").length();
        int dots = s.length() - s.replace(".", "").length();

        if (commas > 0 && dots > 0) {
            int lastComma = s.lastIndexOf(",");
            int lastDot = s.lastIndexOf(".");
            if (lastDot > lastComma) {
                s = s.replace(",", "");
            } else {
                s = s.replace(".", "").replace(",", ".");
            }
        } else if (commas > 0) {
            if (commas == 1 && s.matches(".*,\\d{3}$")) {
                s = s.replace(",", "");
            } else {
                s = s.replace(",", ".");
            }
        } else if (dots > 0) {
            if (dots == 1 && s.matches(".*\\.\\d{3}$")) {
                s = s.replace(".", "");
            }
        }

        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
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
                        if (header.equalsIgnoreCase("Mt SST") || header.equalsIgnoreCase("Total BR") || header.equalsIgnoreCase("Somme de Mt SST")) {
                            targetIndex = i;
                            hasValidColumn = true;
                            break;
                        }
                    }
                } else {
                    if (targetIndex < cols.length && cols[targetIndex] != null) {
                        try {
                            totalSst += parseMoney(cols[targetIndex]);
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

        try (Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(file)) {

            for (Sheet sheet : workbook) {
                String sheetName = sheet.getSheetName();
                Double totalBrValue = 0.0;
                boolean found = false;

                try {
                    for (Row row : sheet) {
                        for (Cell cell : row) {
                            if (cell.getCellType() == CellType.STRING) {
                                String cellValue = cell.getStringCellValue();

                                if (cellValue != null && cellValue.trim().replace(":", "").trim().equalsIgnoreCase("Total BR")) {

                                    Cell valueCell = row.getCell(cell.getColumnIndex() + 1);

                                    if (valueCell != null) {
                                        String rawValue = "";
                                        try {
                                            if (valueCell.getCellType() == CellType.NUMERIC) {
                                                rawValue = String.valueOf(valueCell.getNumericCellValue());
                                            } else if (valueCell.getCellType() == CellType.FORMULA) {
                                                if (valueCell.getCachedFormulaResultType() == CellType.NUMERIC) {
                                                    rawValue = String.valueOf(valueCell.getNumericCellValue());
                                                } else {
                                                    rawValue = valueCell.getStringCellValue();
                                                }
                                            } else {
                                                rawValue = valueCell.getStringCellValue();
                                            }
                                        } catch (Exception e) {
                                            rawValue = valueCell.toString();
                                        }
                                        totalBrValue = parseMoney(rawValue);
                                    }
                                    found = true;
                                    System.out.println("✅ " + sheetName + " -> " + totalBrValue);
                                    break;
                                }
                            }
                        }
                        if (found) break;
                    }
                } catch (Exception ex) {
                    System.out.println("⚠️ Erreur mineure f " + sheetName + ": " + ex.getMessage());
                }

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

            // 🚀 THE FIX: Création d'un format entier b l'espace w l'Euro "2 885 €"
            CellStyle numberStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            numberStyle.setDataFormat(format.getFormat("#,##0\" €\""));

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Partenaire (Feuille)");
            headerRow.createCell(1).setCellValue("Total BR");

            int rowIdx = 1;
            for (PartnerTotalDto dto : dataList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getSheetName());

                Cell valueCell = row.createCell(1);
                if (dto.getTotalBr() != null && dto.getTotalBr() != 0.0) {
                    // On arrondi hna b Math.round bach Excel may-ktbch l'fasila mkhbya f la mémoire
                    valueCell.setCellValue(Math.round(dto.getTotalBr()));
                    valueCell.setCellStyle(numberStyle);
                } else {
                    valueCell.setCellValue("-");
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