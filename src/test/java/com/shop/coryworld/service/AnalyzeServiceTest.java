package com.shop.coryworld.service;

import com.shop.coryworld.dto.analyze.AnalyzeDataDto;
import com.shop.coryworld.dto.analyze.AnalyzeItemDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "uploadPath=/tmp/test-uploads",
        "itemImgLocation=/tmp/item-imgs"  // 문제된 설정 값 임시 추가
})
class AnalyzeServiceTest {
    @Autowired
    AnalyzeService analyzeService;

    @MockBean
    ItemImgService itemImgService;  // 설정 문제 되는 서비스 무력화

    // FastApi와 통신 테스트
    @Test
    void analyze() throws IOException {
        Path imagePath = Path.of("src/test/resources/test-image.jpeg");
        byte[] imageBytes = Files.readAllBytes(imagePath);

        // MockMultipartFile 생성
        MultipartFile mockFile = new MockMultipartFile(
                // form field name
                "imageFile",
                // 파일 이름
                "test-image.jpg",
                // contentType
                "image/jpeg",
                // 바이트 데이터
                imageBytes
        );
        List<AnalyzeItemDto> analyzeItemDtos = analyzeService.doAnalyze(mockFile);
        System.out.println(analyzeItemDtos);
    }
}