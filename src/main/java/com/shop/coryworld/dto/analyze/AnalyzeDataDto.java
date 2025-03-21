package com.shop.coryworld.dto.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 분석 결과 Json 파싱 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeDataDto {
    private List<DetectionResult> results;

    public List<DetectionResult> getResults() {
        return results;
    }
}
