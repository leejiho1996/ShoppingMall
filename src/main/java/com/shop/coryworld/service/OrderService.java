package com.shop.coryworld.service;

import com.shop.coryworld.entity.*;
import com.shop.coryworld.repository.ItemImgRepository;
import com.shop.coryworld.repository.ItemRepository;
import com.shop.coryworld.repository.MemberRepository;
import com.shop.coryworld.repository.OrderRepository;
import com.shop.coryworld.dto.OrderDto;
import com.shop.coryworld.dto.OrderHistDto;
import com.shop.coryworld.dto.OrderItemDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@EnableRetry
public class OrderService {

    // 할인이나 대규모 주문이 발생할 만한 이벤트시 true
    @Value("${order.event.mode}")
    private boolean orderEventMode;

    private static final int STOCK_THRESHOLD = 10;

    private final EntityManager em;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    // 아이템 바로 구매 메서드
    @Transactional
    @Retryable(
            retryFor = {
                    OptimisticLockException.class,
                    ObjectOptimisticLockingFailureException.class,
                    StaleObjectStateException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 300)
    )
    public Long order(OrderDto orderDto, String email) {
        Item item = findItem(orderDto.getItemId());

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItems = new ArrayList<>();

        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount(), item.getPrice());
        orderItems.add(orderItem);

        Order order = Order.createOrder(member, orderItems);
        orderRepository.save(order);

        return order.getId();
    }

    // 카트에 담긴 아이템 구매 메서드
    @Transactional
    @Retryable(
            retryFor = {
                    OptimisticLockException.class,
                    ObjectOptimisticLockingFailureException.class,
                    StaleObjectStateException.class
            },
            maxAttempts = 5,
            backoff = @Backoff(delay = 50)
    )
    public Long orderCartItems(List<OrderDto> orderDtoList, Long userId) {
        List<OrderItem> orderItems = new ArrayList<>();

        List<Long> itemIds = new ArrayList<>(orderDtoList.stream()
                .map(OrderDto::getItemId)
                .toList());

        itemIds.sort(Comparator.naturalOrder());

        List<Item> items = itemRepository.findAllById(itemIds);

        if (itemIds.size() != items.size()) {
            throw new EntityNotFoundException();
        }

        List<Long> pessimisticOrderItemIds;
        if (orderEventMode) {
            pessimisticOrderItemIds = new ArrayList<>(itemIds);
        } else { // 재고가 적은 아이템 선별
            pessimisticOrderItemIds = items.stream()
                    .filter(i -> i.getStockNumber() <= STOCK_THRESHOLD)
                    .peek(i -> em.detach(i)) // 준영속 시켜줘야 낙관적락 예외 방지 가능
                    .map(Item::getId)
                    .toList();
        }

        log.info("비관적 락:{}", pessimisticOrderItemIds);
        // 재고가 적은 아이템을 비관적 락으로 다시 search
        List<Item> pessimisticOrderItem = pessimisticOrderItemIds.isEmpty()
                ? List.of()
                : findItemPessimistic(pessimisticOrderItemIds);

        // Map에 모든 아이템을 모은다
        Map<Long, Item> finalItemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, i -> i));

        pessimisticOrderItem.forEach(locked -> finalItemMap.put(locked.getId(), locked));

        for (OrderDto orderDto : orderDtoList) {
            Item item = finalItemMap.get(orderDto.getItemId());
            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount(), item.getPrice());
            orderItems.add(orderItem);
        }

        Order newOrder = Order.createOrder(Member.getDummyMember(userId), orderItems);
        orderRepository.save(newOrder);

        return newOrder.getId();
    }

    // 카트에 담긴 아이템 구매 메서드
    @Transactional
    public Long orders(List<OrderDto> orderDtoList, String email) {
        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            orderItemList.add(OrderItem.createOrderItem(item, orderDto.getCount(), item.getPrice()));
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

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

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public boolean validateOrder(Long orderId, String email) {

        Order order = orderRepository.findOrderByIdWithMember(orderId)
                .orElseThrow(EntityNotFoundException::new);

        Member orderMember = order.getMember();

        log.info("orderMember.getEmail() : {}", orderMember.getEmail());
        log.info ("curMember.getEmail() : {}", email);

        if (!StringUtils.equals(email, orderMember.getEmail())) {
            return false;
        }

        return true;
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    @Transactional
    public Item findItem(Long itemId) {
        return (orderEventMode
                ? itemRepository.findByIdForUpdate(itemId)
                : itemRepository.findById(itemId))
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public List<Item> findItemPessimistic(List<Long> itemIds) {
        return itemRepository.findItemByIdListForUpdate(itemIds);
    }

    // @Transactional
//    public Long orderCartItems(List<OrderDto> orderDtoList, Long userId) {
//
//        List<OrderItem> orderItemList = new ArrayList<>();
//
//        List<Long> itemIds = orderDtoList.stream()
//                .map(OrderDto::getItemId)
//                .toList();
//
//        List<Item> items = itemRepository.findAllById(itemIds);
//
//        if (itemIds.size() != items.size()) {
//            throw new EntityNotFoundException();
//        }
//
//
//        items.sort((a, b) -> a.getId().compareTo(b.getId()));
//        orderDtoList.sort((a, b) -> a.getItemId().compareTo(b.getItemId()));
//
//
//        for (int i = 0; i < items.size(); i++) {
//            Item item = items.get(i);
//            OrderDto orderDto = orderDtoList.get(i);
//            orderItemList.add(OrderItem.createOrderItem(item, orderDto.getCount(), item.getPrice()));
//        }
//
//        Order order = Order.createOrder(Member.getDummyMember(userId), orderItemList);
//        orderRepository.save(order);
//        return order.getId();
//    }
}
