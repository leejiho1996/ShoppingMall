package com.shop.coryworld.dto;

import com.shop.coryworld.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

// Item 검색 조건 DTO

@Getter
@Setter
public class ItemSearchDto {

    public ItemSearchDto() {
        this.searchDateType = "all";
    }

    private String searchDateType;

    private ItemSellStatus searchSellStatus;

    private String searchBy;

    private String searchQuery = "";
}
