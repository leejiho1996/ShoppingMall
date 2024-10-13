package com.shop.coryworld.entity;

import com.shop.coryworld.constant.ItemSellStatus;
import com.shop.coryworld.repository.ItemRepository;
import com.shop.coryworld.repository.OrderItemRepository;
import com.shop.coryworld.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class OrderItemTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("FetchType.Eager")
    void eagerFetch() {
        Order order = createOrder();
        Long orderId = order.getOrderItems().get(0).getId();

        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        log.info("Order = {}", orderItem.getOrder().getClass());


    }

    @Test
    @DisplayName("FetchType.Lazy")
    void lazyFetch() {
        Order order = createOrder();
        Long orderId = order.getOrderItems().get(0).getId();

        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        log.info("Order = {}", orderItem.getOrder().getClass());


    }

    public Order createOrder() {
        Order order = new Order();

        for (int i = 0; i < 3; i++) {
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        orderRepository.save(order);

        return order;
    }

    Item createItem() {

        Item item = new Item();
        item.setItemName("testItem" + 1);
        item.setPrice(10000 + 1 * 10);
        item.setItemDetail("테스트 상품 상세 설명" + 1);
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(1000 + 1);

        item.setUpdateTime(LocalDateTime.now());
        item.setRegTime(LocalDateTime.now());

        return item;
        }
}