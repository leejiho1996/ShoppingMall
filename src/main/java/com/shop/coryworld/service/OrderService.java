package com.shop.coryworld.service;

import com.shop.coryworld.entity.*;
import com.shop.coryworld.repository.ItemImgRepository;
import com.shop.coryworld.repository.ItemRepository;
import com.shop.coryworld.repository.MemberRepository;
import com.shop.coryworld.repository.OrderRepository;
import com.shop.coryworld.dto.OrderDto;
import com.shop.coryworld.dto.OrderHistDto;
import com.shop.coryworld.dto.OrderItemDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto, String email) {
        Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItems = new ArrayList<>();

        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount(), item.getPrice());
        orderItems.add(orderItem);

        Order order = Order.createOrder(member, orderItems);
        orderRepository.save(order);

        for (OrderItem orderItem1 : order.getOrderItems()) {
            log.info("orderItem.getOrder() : {}",orderItem.getOrder());
        }

        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            int total = 0;
            for (OrderItem orderItem : orderItems) {
                ItemImg ItemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
                orderHistDto.addOrderItemDto(new OrderItemDto(orderItem, ItemImg.getImgUrl()));
                total += orderItem.getTotalPrice();
            }
            orderHistDto.setTotalOrderPrice(total);
            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<>(orderHistDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        Member orderMember = order.getMember();

        log.info("orderMember.getEmail() : {}", orderMember.getEmail());
        log.info ("curMember.getEmail() : {}", curMember.getEmail());

        if (!StringUtils.equals(curMember.getEmail(), orderMember.getEmail())) {
            return false;
        }

        return true;
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    public Long orders(List<OrderDto> orderDtoList, String email) {
        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem.createOrderItem(item, orderDto.getCount(), item.getPrice());
            orderItemList.add(OrderItem.createOrderItem(item, orderDto.getCount(), item.getPrice()));
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }
}
