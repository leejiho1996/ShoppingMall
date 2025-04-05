package com.shop.coryworld.entity;

import com.shop.coryworld.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes",
    uniqueConstraints = {
            @UniqueConstraint(name = "unique_item_member", columnNames = {"like_id", "member_id"})
    }
)
@Getter
@NoArgsConstructor
public class Like extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    Item item;

    private Like(Member member, Item item) {
        this.member = member;
        this.item = item;
    }

    public static Like createLike(Member member, Item item) {
        return new Like(member, item);
    }
}
