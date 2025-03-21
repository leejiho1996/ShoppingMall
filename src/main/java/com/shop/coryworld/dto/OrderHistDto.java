package com.shop.coryworld.dto;

import com.shop.coryworld.constant.OrderStatus;
import com.shop.coryworld.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Getter @Setter
public class OrderHistDto {

    public OrderHistDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();

    }
    private Long orderId;

    private String orderDate;

    private OrderStatus orderStatus;

    private int totalOrderPrice;

    //주문 상품 리스트
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    public void addOrderItemDto(OrderItemDto orderItemDto) {
        orderItemDtoList.add(orderItemDto);
    }
}
