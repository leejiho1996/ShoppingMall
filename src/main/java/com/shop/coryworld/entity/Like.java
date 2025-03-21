package com.shop.coryworld.entity;

import com.shop.coryworld.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes")
@Getter
@NoArgsConstructor
public class Like extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "like_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    Member member;

    public Like(Member member) {
        this.member = member;
    }

    public static Like createLike(Member member) {
        return new Like(member);
    }
}
