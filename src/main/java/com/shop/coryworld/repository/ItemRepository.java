package com.shop.coryworld.repository;

import com.shop.coryworld.dto.analyze.AnalyzeItemDto;
import com.shop.coryworld.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    List<Item> findByItemName(String itemName);

    List<Item> findByItemNameOrItemDetail(String itemName, String itemDetail);

    List<Item> findByPriceLessThan(int price);

    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    // ai 분석 후 해당 물고기들의 정보 쿼리
    @Query("select new com.shop.coryworld.dto.analyze.AnalyzeItemDto(i.id, i.itemName, im.imgUrl) " +
            "from ItemImg im join im.item i " +
            "where im.repImgYn = 'Y' and i.id in :itemList")
    List<AnalyzeItemDto> findAnalyzedItemByItemList(@Param("itemList") List<Long> itemList);

}
