package com.shop.coryworld.repository;

import com.shop.coryworld.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o where o.member.email = :email" +
    " order by o.orderDate desc")
    List<Order> findOrders(@Param("email") String email, Pageable pageable); //count 쿼리 사용 안함

    @Query("select count(o) from Order o where o.member.email = :email")
    Long countOrder(@Param("email") String email);
}
