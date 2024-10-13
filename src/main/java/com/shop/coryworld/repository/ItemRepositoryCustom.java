package com.shop.coryworld.repository;

import com.shop.coryworld.entity.Item;
import com.shop.coryworld.repository.dto.ItemSearchDto;
import com.shop.coryworld.repository.dto.MainItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
