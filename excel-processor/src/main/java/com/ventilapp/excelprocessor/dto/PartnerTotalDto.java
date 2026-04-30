package com.ventilapp.excelprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartnerTotalDto {
    private String sheetName;
    private Double totalBr;
}
