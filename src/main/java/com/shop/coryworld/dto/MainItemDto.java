package com.shop.coryworld.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItemDto {

    private Long id;

    private String itemNm;

    private String itemDetail;

    private String imgUrl;

    private Integer price;

    private Integer likeCnt;

    private Boolean isLiked;

    @QueryProjection
    public MainItemDto(Long id, String itemNm, String itemDetail, String imgUrl,
                       Integer price, Integer likeCnt, Boolean isLiked) {
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
        this.likeCnt = likeCnt;
        this.isLiked = isLiked != null && isLiked; // null 방지;
    }
}
