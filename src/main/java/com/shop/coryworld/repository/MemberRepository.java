package com.shop.coryworld.repository;

import com.shop.coryworld.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);
}
