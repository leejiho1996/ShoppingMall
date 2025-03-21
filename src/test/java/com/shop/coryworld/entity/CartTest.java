package com.shop.coryworld.entity;

import com.shop.coryworld.repository.CartRepository;
import com.shop.coryworld.dto.MemberFormDto;
import com.shop.coryworld.service.MemberService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
class CartTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager em;

    @Test
    void cartRepositoryTest() {
        Member createMember = createTestMember();
        memberService.saveMember(createMember);
        Cart cart = new Cart(createMember);
        cartRepository.save(cart);

        em.flush();
        em.clear();

        assertEquals(createMember.getId(), cart.getMember().getId());


    }

    private Member createTestMember() {

        MemberFormDto memberDto = new MemberFormDto();
        memberDto.setEmail("test@email.com");
        memberDto.setPassword("123");
        memberDto.setAddress("test address");
        memberDto.setName("testUser");

        return new Member(memberDto, passwordEncoder);

    }

}