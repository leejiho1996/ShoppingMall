package com.shop.coryworld.repository;

import com.shop.coryworld.entity.CartItem;
import com.shop.coryworld.dto.CartDetailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    @Query("select new com.shop.coryworld.dto.CartDetailDto" +
            "(ci.id, i.itemName, i.price, ci.count, im.imgUrl) " +
            "from CartItem ci " +
            "join ci.item i " +
            "join ItemImg im on im.item.id = i.id " +
            "where ci.cart.id = :cartId " +
            "and im.repImgYn = 'Y' " +
            "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);

    @Query("select ci from CartItem ci join fetch ci.cart where ci.id = :cartItemId")
    Optional<CartItem> findCartItemByIdWithCart(Long cartItemId);

    @Query("select ci from CartItem ci where ci.id in :cartIdList")
    List<CartItem> findCartItemByIdList(List<Long> cartIdList);
}
