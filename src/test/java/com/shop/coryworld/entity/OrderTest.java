package com.shop.coryworld.entity;

import com.shop.coryworld.constant.OrderStatus;
import com.shop.coryworld.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EntityManager em;

//    @Test
//    void cascadeTest() {
//        Order order =) new Order(LocalDateTime.now(), OrderStatus.ORDER;
//
//        OrderItem orderItem = new OrderItem();
//        orderItem.setOrder(order);
//        order.getOrderItems().add(orderItem);
//
//        orderRepository.save(order);
//
//        log.info("orderItem id: {}", orderItem.getId());
//
//    }

}