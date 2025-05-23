package com.shop.coryworld.repository;

import com.shop.coryworld.dto.analyze.AnalyzeItemDto;
import com.shop.coryworld.entity.Item;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Query("select i from Item i where i.id in :itemIdList")
    List<Item> findItemByIdList(@Param("itemIdList") List<Long> itemIdList);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.id = :itemId")
    Optional<Item> findByIdForUpdate(@Param("itemId") Long itemId);

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("select i from Item i where i.id in :itemIdList")
    List<Item> findItemByIdListForUpdate(@Param("itemIdList") List<Long> itemIdList);

}
