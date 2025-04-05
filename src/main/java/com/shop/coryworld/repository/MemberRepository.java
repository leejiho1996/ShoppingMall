package com.shop.coryworld.repository;

import com.shop.coryworld.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    @Query("select m.id from Member m where m.email = :email")
    Long findIdByEmail(@Param("email") String email);
}
