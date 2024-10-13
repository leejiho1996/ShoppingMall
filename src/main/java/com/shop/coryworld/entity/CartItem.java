package com.shop.coryworld.entity;

import com.shop.coryworld.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "cart_item")
@Getter
public class CartItem extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "cart_item_id")
    public Long id;

    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    public Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    public Cart cart;

    public static CartItem createCartItem(Item item, int count, Cart cart) {
        CartItem cartItem = new CartItem();
        cartItem.item = item;
        cartItem.count = count;
        cartItem.cart = cart;
        return cartItem;
    }

    public void addCount(int count) {
        this.count += count;
    }

    public void updateCount(int count) {
        this.count = count;
    }

}
