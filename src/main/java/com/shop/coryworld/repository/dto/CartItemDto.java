package com.shop.coryworld.repository.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {

    @NotNull(message = "상품 아이디는 필수 입력 값 입니다.")
    private Long itemId;

    private int count;
}