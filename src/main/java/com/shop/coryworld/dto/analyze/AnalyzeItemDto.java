package com.shop.coryworld.dto.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyzeItemDto {

    private Long itemId; // item id

    private String title; // 제목

    private String repImg; // 대표사진

}
