package com.shop.coryworld.dto.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetectionResult {
    private String className;

    private Double confidence;
}
