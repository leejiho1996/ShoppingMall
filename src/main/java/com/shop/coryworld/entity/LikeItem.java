package com.shop.coryworld.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes_item")
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class LikeItem {
    @Id @GeneratedValue
    @Column(name="likes_item")
    private Long id;

    @ManyToOne
    @JoinColumn(name="likes_id")
    Like like;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="item_id")
    Item item;

    public LikeItem(Like like, Item item) {
        this.like = like;
        this.item = item;
    }

    public static LikeItem createLikeItem(Like like, Item item) {
        return new LikeItem(like, item);
    }

}
