package com.shop.coryworld.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeItemDto {
    @NotNull(message="상품 아이디는 필수 입력 값 입니다.")
    private Long id;
}
