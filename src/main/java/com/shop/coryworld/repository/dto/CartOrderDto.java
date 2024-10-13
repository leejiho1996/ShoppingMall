package com.shop.coryworld.repository.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CartOrderDto {

    private long cartItemId;

    private List<CartOrderDto> cartOrderDtoList;
}
