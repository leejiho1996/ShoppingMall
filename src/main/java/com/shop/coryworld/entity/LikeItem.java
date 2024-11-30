package com.shop.coryworld.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "like_item")
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class LikeItem {
    @Id @GeneratedValue
    @Column(name="like_item")
    private Long id;

    @ManyToOne
    @JoinColumn(name="like_id")
    Like like;

    @ManyToOne
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
