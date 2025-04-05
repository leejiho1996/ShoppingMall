package com.shop.coryworld.repository;

import com.shop.coryworld.entity.Item;
import com.shop.coryworld.dto.ItemSearchDto;
import com.shop.coryworld.dto.MainItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable, Long memberId);
}
