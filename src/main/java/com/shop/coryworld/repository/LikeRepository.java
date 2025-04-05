package com.shop.coryworld.repository;

import com.shop.coryworld.entity.Item;
import com.shop.coryworld.entity.Like;
import com.shop.coryworld.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByMemberId(Long member_id);

    @Query("select l from Like l join fetch l.item where l.member.id = :memberId and l.item.id = :itemId")
    Optional<Like> findByMemberAndItem(Long memberId, Long itemId);

    @Query("select l from Like l join fetch l.member")
    Like findLikeWithMemberAndItem(Member member);

    @Query("select l from Like l join fetch l.item where l.id = :likeId")
    Optional<Like> findLikeWithItem(@Param("likeId") Long likeId);
}
