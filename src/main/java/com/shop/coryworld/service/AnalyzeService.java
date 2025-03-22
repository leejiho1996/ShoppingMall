package com.shop.coryworld.service;

import com.shop.coryworld.dto.analyze.AnalyzeDataDto;
import com.shop.coryworld.dto.analyze.AnalyzeItemDto;
import com.shop.coryworld.repository.ItemRepository;
import com.shop.coryworld.entity.Item;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyzeService {

    private final ItemRepository itemRepository;

    private Map<String, Long> fishMap;

    @PostConstruct
    public void initFishMap() {
        fishMap = itemRepository.findAll().stream()
                .filter(item -> item.getItemName() != null)  // null 안전
                .collect(Collectors.toMap(
                        Item::getItemName,  // YOLO 클래스 이름
                        Item::getId         // 상품 ID
                ));
    }


    public List<AnalyzeItemDto> doAnalyze(MultipartFile multipartFile) throws IOException {

        byte[] imageByte = multipartFile.getBytes();
        ByteArrayResource byteArrayResource = new ByteArrayResource(imageByte) {
            @Override
            public String getFilename() {
                return "fish.jpg"; // 꼭 있어야 multipart 파일로 처리됨!
            }
        };

        // multipart/form-data 빌더
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("image", byteArrayResource);

        // WebClient 인스턴스 생성
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8000")
                .build();

        AnalyzeDataDto result = webClient.post()
                .uri("/detect")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        // 바로 AnalyzeDataDto로 파싱
                        return response.bodyToMono(AnalyzeDataDto.class);
                    } else {
                        throw new ServiceException("Error uploading file");
                    }
                }).block();

        // 분석결과가 없을 때 빈 리스트 리턴
        if (result.getResults().isEmpty()) {
            return new ArrayList<>();
        }

        // 분석된 아이템들의 이름을 itemId로 매핑
        List<Long> itemIdList = result.getResults().stream()
                .map(r -> fishMap.get(r.getClassName()))
                .toList();

        log.info("itemIdList {}", itemIdList);

        return itemRepository.findAnalyzedItemByItemList(itemIdList);
    }

}
