package com.shop.coryworld.repository;

import com.shop.coryworld.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByMemberId(Long member_id);
}
