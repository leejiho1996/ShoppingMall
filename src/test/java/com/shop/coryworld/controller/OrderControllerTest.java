package com.shop.coryworld.controller;

import com.shop.coryworld.constant.ItemSellStatus;
import com.shop.coryworld.entity.Item;
import com.shop.coryworld.entity.Member;
import com.shop.coryworld.entity.Order;
import com.shop.coryworld.entity.OrderItem;
import com.shop.coryworld.repository.ItemRepository;
import com.shop.coryworld.repository.MemberRepository;
import com.shop.coryworld.repository.OrderRepository;
import com.shop.coryworld.dto.OrderDto;
import com.shop.coryworld.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
class OrderControllerTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    public Item saveItem(){
        Item item = new Item();
        item.setItemName("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    public Member saveMember(){
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);

    }

    @Test
    @DisplayName("주문 테스트")
    public void order(){
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());

        Long orderId = orderService.order(orderDto, member.getEmail());

        em.flush();
        em.clear();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        List<OrderItem> orderItems = order.getOrderItems();

        OrderItem orderItem = orderItems.get(0);
        orderItem.getCount();

//        List<OrderItem> orderItems = order.getOrderItems();
//
//        log.info("orderItems = {}", orderItems.size());
//
//        for (OrderItem orderItem : orderItems) {
//            log.info("orderItems.getOrder = {}", orderItem.getOrder());
//        }
//        int totalPrice = orderDto.getCount()*item.getPrice();
//
//        assertEquals(totalPrice, order.getTotalPrice());
    }

}