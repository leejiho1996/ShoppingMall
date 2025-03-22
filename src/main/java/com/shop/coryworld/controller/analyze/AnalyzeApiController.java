package com.shop.coryworld.controller.analyze;

import com.shop.coryworld.dto.analyze.AnalyzeItemDto;
import com.shop.coryworld.service.AnalyzeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AnalyzeApiController {

    private final AnalyzeService analyzeService;

    @PostMapping("/api/analyze")
    public ResponseEntity<List<AnalyzeItemDto>> analyze(@RequestParam("multipartFile") MultipartFile multipartFile) throws IOException {
        log.info("multipartFile = {}", multipartFile);
        List<AnalyzeItemDto> analyzeDataDto = analyzeService.doAnalyze(multipartFile);
        log.info("result: {}", analyzeDataDto);
        return new ResponseEntity<>(analyzeDataDto, HttpStatus.OK);
    }
}
