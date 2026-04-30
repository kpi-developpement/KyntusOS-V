package com.ventilapp.excelprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessResponseDto {
    private List<PartnerTotalDto> data;
    private String fileBase64;
}