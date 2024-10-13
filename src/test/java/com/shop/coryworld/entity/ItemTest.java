package com.shop.coryworld.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.coryworld.constant.ItemSellStatus;
import com.shop.coryworld.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.jaxb.mapping.marshall.InheritanceTypeMarshalling;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.shop.coryworld.entity.QItem.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ItemTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void setQueryFactory() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Test
    @DisplayName("상품 저장 테스트")
    void storeItemTest() {
        Item item = new Item();
        item.setItemName("test");
        item.setPrice(10000);
        item.setStockNumber(1000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        itemRepository.save(item);

        log.info("item id = {}", item.getId());

    }

    @Test
    void findByItemDetailTest() {
        createItem();

        List<Item> results = itemRepository.findByItemDetail("테스트 상품 상세 설명5");

        for (Item result : results) {
            log.info("result = {}" , result);
        }

    }

    @Test
    void querydslTest() {
        createItem();
        List<Item> result = queryFactory.selectFrom(item)
                .fetch();

        for (Item item : result) {
            log.info("item = {}", item);
        }
    }

    void createItem() {
        for (int i = 0; i < 10; i++) {
            Item item = new Item();
            item.setItemName("testItem" + i);
            item.setPrice(10000 + i * 10);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(1000 + i);

            item.setUpdateTime(LocalDateTime.now());
            item.setRegTime(LocalDateTime.now());

            itemRepository.save(item);
        }
    }




}