package com.shop.coryworld.repository;

import com.shop.coryworld.entity.Cart;
import com.shop.coryworld.repository.dto.CartDetailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByMemberId(Long memberId);

}
